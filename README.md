# Installation

[![Maven Central](https://img.shields.io/maven-central/v/com.robbiebowman/claude-sdk?label=Maven%20Central)](https://search.maven.org/artifact/com.robbiebowman/claude-sdk)

```groovy
implementation 'com.robbiebowman:claude-sdk:0.0.3'
```

# About

This is a library for using Claude via the Anthropic API. It is built in Kotlin.

# Usage

Send `Message` objects via the `ClaudeClient.getChatCompletion` method.
```kotlin
val messages = mutableListOf(Message(Role.User, "What's the surface temperature on Venus? No yapping."),)
val claudeResponse = claudeClient.getChatCompletion(messages) as ClaudeResponse.ChatResponse
println(claudeResponse.message) // "The average surface temperature on Venus is about 864°F (462°C)."
```
Reponses are usually `ClaudeResponse.ChatResponse` unless you define
[tools/functions](https://docs.anthropic.com/claude/docs/functions-external-tools).
Then Claude may respond with a `ClaudeResponse.ToolCall`.

```kotlin
val claudeResponse = claudeClient.getChatCompletion(messages) as ClaudeResponse.ToolCall
if (claudeResponse.toolName == "getSurfaceTemp") {
    val planet = claudeResponse.arguments["planet"]
    val temp = myPlanetEncyclopedia.getPlanetSurfaceTemp(planet)
    messages.add(claudeResponse.toResult(temp))
    val newResponse = claudeClient.getChatCompletion(messages) as ClaudeResponse.ChatResponse
    println(newResponse.message) // "According to my encyclopedia, the temperature is 460°C."
}
```

You can define tools to the builder via `withTool` on `ClaudeClientBuilder`. It accepts either a Kotlin lambda reference
or a `ToolDescription` object.
```kotlin
val claudeClient = ClaudeClientBuilder()
    .withApiKey(System.getenv("CLAUDE_KEY"))
    .withModel(ClaudeModel.Haiku)
    .withTool(::getSurfaceTemp)
    .build()
```
