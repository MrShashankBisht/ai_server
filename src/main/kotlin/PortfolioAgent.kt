package shashankbisht.com

import dev.langchain4j.service.TokenStream

interface PortfolioAgent {
    // The TokenStream return type tells LangChain4j to stream the response
    fun chat(userMessage: String): TokenStream
}