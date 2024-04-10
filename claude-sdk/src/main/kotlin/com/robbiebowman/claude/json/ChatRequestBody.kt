package com.robbiebowman.claude.json

import com.google.gson.annotations.SerializedName
import com.robbiebowman.claude.SerializableMessage

internal data class ChatRequestBody(
    val model: String,
    @SerializedName("max_tokens")
    val maxTokens: Int,
    val messages: List<SerializableMessage>,
    val system: String?,
    val tools: List<String>,
    @SerializedName("stop_sequences")
    val stopSequence: Set<String>
)