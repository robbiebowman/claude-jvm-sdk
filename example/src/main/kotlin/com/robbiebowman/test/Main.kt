package com.robbiebowman.test

import com.robbiebowman.claude.*

fun main() {
    val claudeClient = ClaudeClientBuilder().withApiKey(System.getenv("CLAUDE_KEY")).withModel(ClaudeModel.Haiku)
        .withTool(::getCurrentMayor).build()
    val messages = mutableListOf(SerializableMessage(Role.User, listOf(MessageContent.TextContent("Who is the mayor of New York??"))))
    val claudeResponse = claudeClient.getChatCompletion(messages)
    messages.add(claudeResponse)
    val content = claudeResponse.content.first()
    when (content) {
        is MessageContent.TextContent -> println("Claude: ${content.text}")
        is MessageContent.ToolUse -> {
            val city = content.input["city"]
            val country = content.input["country"]
            val status = ServingStatus.valueOf(content.input["status"]!!)
            val functionResult = getCurrentMayor(city, country, status)

            // Tell Claude our function's result
            messages.add(SerializableMessage(Role.User, listOf(MessageContent.ToolResult(content.id, functionResult))))

            val newResponse = claudeClient.getChatCompletion(messages)

            println(newResponse)
        }

        else -> throw Exception("Test")
    }
}

@ToolDescription("Gets the mayor of a given city")
fun getCurrentMayor(
    @ToolDescription("The city whose mayor to find")
    city: String?,
    @ToolDescription("The country of the city, in case there's ambiguity of the city's name")
    country: String?,
    @ToolDescription("Whether this mayor is serving or retired")
    status: ServingStatus
): String {
    return "Eric Adams"
}

enum class ServingStatus {
    Serving, Retired
}