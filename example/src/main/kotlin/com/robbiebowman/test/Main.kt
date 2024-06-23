package com.robbiebowman.test

import com.robbiebowman.claude.*

fun main() {
    val claudeClient = ClaudeClientBuilder().withApiKey(System.getenv("CLAUDE_KEY")).withModel(ClaudeModel.Haiku)
        .withTool(::getCurrentMayor).build()
    val messages = mutableListOf(
        SerializableMessage(
            Role.User,
            listOf(MessageContent.TextContent("Who is the mayor of New York??"))
        )
    )
    val claudeResponse = claudeClient.getChatCompletion(messages)
    messages.add(claudeResponse)
    val content = claudeResponse.content.first()
    when (content) {
        is MessageContent.TextContent -> println("Claude: ${content.text}")
        is MessageContent.ToolUse -> {
            val location = claudeClient.derserializeToolUse(content.input["location"]!!, Location::class.java)
            val status =
                ServingStatus.valueOf(claudeClient.derserializeToolUse(content.input["status"]!!, String::class.java))
            val functionResult = getCurrentMayor(location, status)

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
    @ToolDescription("The city and country whose mayor to find")
    location: Location,
    @ToolDescription("Whether this mayor is serving or retired")
    status: ServingStatus
): String {
    return "Eric Adams"
}

data class Location(val city: String, val country: String)

enum class ServingStatus {
    Serving, Retired
}