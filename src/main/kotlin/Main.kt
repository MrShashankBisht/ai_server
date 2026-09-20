package shashankbisht.com

import dev.langchain4j.model.chat.StreamingChatLanguageModel
import dev.langchain4j.model.ollama.OllamaStreamingChatModel
import dev.langchain4j.service.AiServices
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.routing.*
import io.ktor.server.sse.*
import io.ktor.sse.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow

fun main() {
    // Starts the Netty server on port 8080
    embeddedServer(Netty, port = 8090, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    // 1. Install JSON Serialization
    install(ContentNegotiation) {
        json()
    }

    // 2. Install SSE for token streaming
    install(SSE)

    // 3. Initialize routes
    configureRouting()
}

// Initialize the Ollama Client (pointing to our internal Docker network)
val ollamaModel: StreamingChatLanguageModel = OllamaStreamingChatModel.builder()
    .baseUrl(System.getenv("OLLAMA_HOST") ?: "http://localhost:11434")
    .modelName("qwen2.5-coder:1.5b")
    .temperature(0.3) // Lower temperature for deterministic tool calling
    .build()

// 2. Build the Agent with the Tools attached
val assistant: PortfolioAgent = AiServices.builder(PortfolioAgent::class.java)
    .streamingChatLanguageModel(ollamaModel)
    .tools(PortfolioTools()) // Inject your Kotlin functions here
    .build()

fun Application.configureRouting() {
    routing {

        sse("/chat/stream") {
            // 1. In a real app, parse the incoming JSON request here for the user's prompt
            val prompt = "Tell me about your portfolio projects."

            // 2. Convert LangChain4j's callback into a Kotlin Flow
            val tokenFlow = callbackFlow {
                assistant.chat(prompt)
                    .onNext { token ->
                        trySend(token)
                    }
                    .onComplete {
                        close()
                    }
                    .onError { error ->
                        close(error)
                    }
                awaitClose { /* Cleanup if client disconnects early */ }
            }

            // 3. Collect the flow and push to the client via SSE
            tokenFlow.collect { token ->
                send(ServerSentEvent(data = token))
            }
        }
    }
}