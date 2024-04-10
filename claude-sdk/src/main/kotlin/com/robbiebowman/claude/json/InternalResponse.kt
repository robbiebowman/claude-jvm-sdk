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
    val text: String?,
    val name: String?,
    val id: String?,
    val input: Map<String, String>?
) {
    fun getType(): Type {
        return Type.valueOf(type)
    }

    enum class Type(val jsonText: String) {
        Text("text"), ToolUse("tool_use")
    }
}

internal data class Usage(
    val inputTokens: Int,
    val outputTokens: Int
)