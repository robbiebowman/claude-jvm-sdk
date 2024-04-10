package com.robbiebowman.claude

import com.fasterxml.jackson.databind.ObjectMapper
import com.google.gson.Gson
import com.robbiebowman.claude.json.ChatRequestBody
import com.robbiebowman.claude.json.ChatResponse
import com.robbiebowman.claude.json.ContentItem
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.util.*

class ClaudeClient internal constructor(
    private val apiKey: String,
    private val model: String,
    private val okHttpClient: OkHttpClient,
    private val maxTokens: Int,
    private val gson: Gson,
    private val systemPrompt: String?,
    private val stopSequences: Set<String>,
    private val tools: List<String>,
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
            system = systemPrompt,
            stopSequence = stopSequences,
            tools = tools
        )
        val requestJson = gson.toJson(requestBody)
        val request = defaultRequest.post(requestJson.toRequestBody()).build()
        val rawResponse = okHttpClient.newCall(request).execute()
        val mapper = ObjectMapper()
        val response = mapper.readValue(rawResponse.body?.string(), SerializableMessage::class.java)
        return response
    }

//    private fun getSerializableMessage(message: SerializableMessage, okHttpClient: OkHttpClient): SerializableMessage {
//        return SerializableMessage(message.role, message.images.map { i ->
//            if (i.imageContents != null && i.mediaType != null) {
//                MessageContent.ImageContent(ResolvedImageContent(String(i.imageContents), i.mediaType))
//            } else {
//                val response = okHttpClient.newCall(Request.Builder().url(i.imageUrl!!).build()).execute()
//                val mediaType = response.headers("content-type").first()
//                val base64 = Base64.getEncoder().encodeToString(response.body?.bytes())
//                MessageContent.ImageContent(ResolvedImageContent(base64, mediaType))
//            }
//        }.plus(MessageContent.TextContent(message.message)))
//    }
}
