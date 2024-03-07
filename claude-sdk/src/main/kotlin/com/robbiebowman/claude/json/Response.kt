package com.robbiebowman.claude.json

data class ChatResponse(
    val id: String,
    val type: String,
    val role: String,
    val content: List<ContentItem>,
    val model: String,
    val stopReason: String,
    val stopSequence: String?,
    val usage: Usage
)

data class ContentItem(
    val type: String,
    val text: String
)

data class Usage(
    val inputTokens: Int,
    val outputTokens: Int
)