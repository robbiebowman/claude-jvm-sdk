package com.robbiebowman.test

import com.robbiebowman.claude.*

fun main() {
    val claudeClient = ClaudeClientBuilder()
        .withApiKey(System.getenv("CLAUDE_KEY"))
        .withModel(ClaudeModel.Haiku)
        .withTool(::getCurrentMayor)
        .build()
    val messages = mutableListOf(Message(Role.User, "Who is the mayor of New York??"),)
    val claudeResponse = claudeClient.getChatCompletion(messages)
    when (claudeResponse) {
        is ClaudeResponse.ChatResponse -> println("Claude: ${claudeResponse.message}")
        is ClaudeResponse.ToolCall -> {
            // Add Claude's response
            messages.add(claudeResponse.toMessage())

            val city = claudeResponse.arguments.first{ it.parameter == "city" }.argumentValue
            val country = claudeResponse.arguments.first{ it.parameter == "country" }.argumentValue
            val functionResult = getCurrentMayor(city, country)

            // Tell Claude our function's result
            messages.add(claudeResponse.toResult(functionResult))

            val newResponse = claudeClient.getChatCompletion(messages) as ClaudeResponse.ChatResponse

            println(newResponse.message)
        }
    }
}

fun getCurrentMayor(city: String, country: String): String {
    return "Eric Adams"
}