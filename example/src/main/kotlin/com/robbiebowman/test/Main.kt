package com.robbiebowman.test

import com.robbiebowman.claude.*

fun main() {
    val claudeClient = ClaudeClientBuilder().withApiKey(System.getenv("CLAUDE_KEY"))
        .withTool(::getCurrentMayor)
        .build()
    val result =
        claudeClient.getChatCompletion(
            listOf(
                Message(
                    Role.User,
                    "Who is the mayor of New York??"
                )
            )
        )
    when (result) {
        is ClaudeResponse.ChatResponse -> println("Claude: ${result.message}")
        is ClaudeResponse.ToolCall -> println("${result.toolName}(${result.arguments.joinToString()})")
    }
}

fun getCurrentMayor(city: String, country: String): String {
    return "Eric Adams"
}