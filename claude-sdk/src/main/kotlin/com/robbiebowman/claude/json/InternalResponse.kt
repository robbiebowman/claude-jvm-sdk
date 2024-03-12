package com.robbiebowman.claude.json

import com.google.gson.annotations.SerializedName

internal data class ChatResponse(
    val id: String,
    val type: String,
    val role: String,
    val content: List<ContentItem>,
    val model: String,
    @SerializedName("stop_reason")
    val stopReason: String?,
    @SerializedName("stop_sequence")
    val stopSequence: String?,
    val usage: Usage
)

internal data class ContentItem(
    val type: String,
    val text: String
)

internal data class Usage(
    val inputTokens: Int,
    val outputTokens: Int
)