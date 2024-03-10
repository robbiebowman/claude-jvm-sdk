package com.robbiebowman.claude

import com.google.gson.Gson
import com.robbiebowman.claude.json.ChatRequestBody
import com.robbiebowman.claude.json.ChatResponse
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
    private val tools: List<String>
) {

    fun getChatCompletion(messages: List<Message>): ChatResponse {
        val serializableMessages = messages.map { getSerializableMessage(it, okHttpClient) }
        val requestBody = ChatRequestBody(model, maxTokens, serializableMessages, systemPrompt)
        val requestJson = gson.toJson(requestBody)
        val request = Request.Builder()
            .post(requestJson.toRequestBody())
            .url("https://api.anthropic.com/v1/messages")
            .header("x-api-key", apiKey)
            .header("anthropic-version", "2023-06-01")
            .header("content-type", "application/json")
        val response = okHttpClient.newCall(request.build()).execute()
        return gson.fromJson(response.body?.string(), ChatResponse::class.java)
    }

    private fun getSerializableMessage(message: Message, okHttpClient: OkHttpClient): SerializableMessage {
        return SerializableMessage(message.role, message.images.map { i ->
            if (i.imageContents != null && i.mediaType != null) {
                MessageContent.ImageContent(ResolvedImageContent(String(i.imageContents), i.mediaType))
            } else {
                val response = okHttpClient.newCall(Request.Builder().url(i.imageUrl!!).build()).execute()
                val mediaType = response.headers("content-type").first()
                val base64 = Base64.getEncoder().encodeToString(response.body?.bytes())
                MessageContent.ImageContent(ResolvedImageContent(base64, mediaType))
            }
        }.plus(MessageContent.TextContent(message.message)))
    }
}
