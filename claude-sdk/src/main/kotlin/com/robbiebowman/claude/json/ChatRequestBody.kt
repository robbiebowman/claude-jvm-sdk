package com.robbiebowman.claude.json

import com.fasterxml.jackson.annotation.JsonProperty
import com.robbiebowman.claude.SerializableMessage

internal data class ChatRequestBody(
    val model: String,
    @get:JsonProperty("max_tokens")
    val maxTokens: Int,
    val messages: List<SerializableMessage>,
    val system: String,
    val tools: List<JsonSchemaTool>,
    @get:JsonProperty("stop_sequences")
    val stopSequence: Set<String>
)