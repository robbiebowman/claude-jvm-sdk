package com.robbiebowman.test

import com.robbiebowman.claude.*

fun main() {
    val claudeClient = ClaudeClientBuilder().withApiKey(System.getenv("CLAUDE_KEY")).withModel(ClaudeModel.Haiku)
        .withTool(::getCurrentMayor).build()
    val messages = mutableListOf(SerializableMessage(Role.User, listOf(MessageContent.TextContent("Who is the mayor of New York??"))))
    val claudeResponse = claudeClient.getChatCompletion(messages)
    val content = claudeResponse.content.first()
    when (content) {
        is MessageContent.TextContent -> println("Claude: ${content.text}")
        is MessageContent.ToolUse -> {

            val city = content.input["city"]
            val country = content.input["country"]
            val functionResult = getCurrentMayor(city, country, SomeNesting(1, Location.London))

            // Tell Claude our function's result
            messages.add(SerializableMessage(Role.User, listOf(MessageContent.ToolResult(content.id, functionResult))))

            val newResponse = claudeClient.getChatCompletion(messages)

            println(newResponse)
        }

        else -> throw Exception("Test")
    }
}

@ToolDescription("Gets the current mayor of a given city")
fun getCurrentMayor(
    @ToolDescription("The city whose mayor to find") city: String?,
    @ToolDescription("The country of the city, in case there's ambiguity of the city's name") country: String?,
    misc: SomeNesting
): String {
    return "Eric Adams"
}

data class SomeNesting(
    val temp: Int,
    val location: Location
)

enum class Location {
    Paris, NewYork, London;
}