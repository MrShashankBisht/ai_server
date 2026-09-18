package shashankbisht.com

import dev.langchain4j.agent.tool.Tool
import dev.langchain4j.agent.tool.P

class PortfolioTools {

    @Tool("Returns the total number of active projects in the portfolio database.")
    fun getActiveProjectCount(): Int {
        // In reality, this executes a read-only query to your pgvector database
        println("Executing getActiveProjectCount()...")
        return 12
    }

    @Tool("Retrieves technical details about a specific project by its name.")
    fun getProjectDetails(
        @P("The exact name of the project to search for, e.g., 'PocoEdit'") projectName: String
    ): String {
        println("Executing getProjectDetails for: $projectName")

        // Query database for the specific project
        return if (projectName.equals("PocoEdit", ignoreCase = true)) {
            "PocoEdit is a GitHub hosted project focusing on advanced text editing."
        } else {
            "Project not found."
        }
    }
}