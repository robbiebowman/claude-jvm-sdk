package com.robbiebowman.claude

import com.fasterxml.jackson.databind.ObjectMapper
import com.robbiebowman.claude.json.ChatRequestBody
import com.robbiebowman.claude.json.JsonSchemaTool
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody

class ClaudeClient internal constructor(
    private val apiKey: String,
    private val model: String,
    private val okHttpClient: OkHttpClient,
    private val maxTokens: Int,
    private val mapper: ObjectMapper,
    private val systemPrompt: String?,
    private val stopSequences: Set<String>,
    private val tools: List<JsonSchemaTool>,
) {

    private val defaultRequest = Request.Builder()
        .url("https://api.anthropic.com/v1/messages")
        .header("content-type", "application/json")
        .header("x-api-key", apiKey)
        .header("anthropic-version", "2023-06-01")
        .header("anthropic-beta", "tools-2024-04-04")

    fun getChatCompletion(messages: List<SerializableMessage>): SerializableMessage {
        val requestBody = ChatRequestBody(
            model = model,
            maxTokens = maxTokens,
            messages = messages,
            system = systemPrompt ?: "",
            stopSequence = stopSequences,
            tools = tools
        )
        val requestJson = mapper.writeValueAsString(requestBody)
        val request = defaultRequest.post(requestJson.toRequestBody()).build()
        val rawResponse = okHttpClient.newCall(request).execute()
        val responseString = rawResponse.body?.string()
        val response = mapper.readValue(responseString, SerializableMessage::class.java)
        return response
    }
}
