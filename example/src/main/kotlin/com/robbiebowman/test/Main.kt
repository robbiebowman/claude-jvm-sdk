package com.robbiebowman.test

import com.robbiebowman.claude.*

fun main() {
    val claudeClient = ClaudeClientBuilder().withApiKey(System.getenv("CLAUDE_KEY")).withModel(ClaudeModel.Haiku)
        .withTool(::getCurrentMayor).build()
    val messages = mutableListOf(Message(Role.User, "Who is the mayor of New York??"))
    val claudeResponse = claudeClient.getChatCompletion(messages)
    when (claudeResponse) {
        is ClaudeResponse.ChatResponse -> println("Claude: ${claudeResponse.message}")
        is ClaudeResponse.ToolCall -> {
            // Add Claude's response
            messages.add(claudeResponse.toMessage())

            val city = claudeResponse.arguments["city"]
            val country = claudeResponse.arguments["country"]
            val functionResult = getCurrentMayor(city, country)

            // Tell Claude our function's result
            messages.add(claudeResponse.toResult(functionResult))

            val newResponse = claudeClient.getChatCompletion(messages) as ClaudeResponse.ChatResponse

            println(newResponse.message)
        }
    }
}

@ToolDescription("Gets the current mayor of a given city")
fun getCurrentMayor(
    @ToolDescription("The city whose mayor to find") city: String?,
    @ToolDescription("The country of the city, in case there's ambiguity of the city's name") country: String?
): String {
    return "Eric Adams"
}