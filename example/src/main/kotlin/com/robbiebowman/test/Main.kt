package com.robbiebowman.test

import com.robbiebowman.claude.ClaudeClientBuilder
import com.robbiebowman.claude.Image
import com.robbiebowman.claude.Message
import com.robbiebowman.claude.Role

fun main() {
    val claudeClient = ClaudeClientBuilder().withApiKey(System.getenv("CLAUDE_KEY")).build()
    val result =
        claudeClient.getChatCompletion(
            listOf(
                Message(
                    Role.User,
                    "What is this a picture of?",
                    listOf(Image("https://media.npr.org/images/stations/nprone_logos/wnyc.png"))
                )
            )
        )
    println(result.content.first().text)
}
