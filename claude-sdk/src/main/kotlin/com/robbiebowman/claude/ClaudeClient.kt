package com.robbiebowman.claude

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.MapperFeature
import com.fasterxml.jackson.dataformat.xml.JacksonXmlModule
import com.fasterxml.jackson.dataformat.xml.XmlMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.google.gson.Gson
import com.robbiebowman.claude.json.ChatRequestBody
import com.robbiebowman.claude.json.ChatResponse
import com.robbiebowman.claude.json.ContentItem
import com.robbiebowman.claude.xml.InvokeRequest
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

    private val xmlMapper = XmlMapper(JacksonXmlModule()
        .apply { setDefaultUseWrapper(false) })
        .registerKotlinModule()
        .configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true)
        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)

    private val defaultRequest = Request.Builder()
        .url("https://api.anthropic.com/v1/messages")
        .header("x-api-key", apiKey)
        .header("anthropic-version", "2023-06-01")
        .header("content-type", "application/json")

    fun getChatCompletion(messages: List<Message>): ClaudeResponse {
        val serializableMessages = messages.map { getSerializableMessage(it, okHttpClient) }
        val requestBody = ChatRequestBody(
            model = model,
            maxTokens = maxTokens,
            messages = serializableMessages,
            system = systemPrompt,
            stopSequence = stopSequences,
            tools = tools
        )
        val requestJson = gson.toJson(requestBody)
        val request = defaultRequest.post(requestJson.toRequestBody()).build()
        val rawResponse = okHttpClient.newCall(request).execute()
        val response = gson.fromJson(rawResponse.body?.string(), ChatResponse::class.java)
        return getClaudeResponse(response)
    }

    private fun getClaudeResponse(response: ChatResponse): ClaudeResponse {
        val toolCall = response.content.firstOrNull { it.type == ContentItem.Type.ToolUse.jsonText }
        return if (toolCall != null) {
            ClaudeResponse.ToolCall(
                id = toolCall.id!!,
                toolName = toolCall.name!!,
                arguments = toolCall.input!!,
            )
        } else ClaudeResponse.ChatResponse(response.content.single().text!!)
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
