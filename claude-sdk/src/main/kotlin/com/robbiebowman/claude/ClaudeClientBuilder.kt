package com.robbiebowman.claude

import com.google.gson.Gson
import okhttp3.OkHttpClient

class ClaudeClientBuilder {

    private var apiKey: String? = null

    fun withApiKey(apiKey: String): ClaudeClientBuilder {
        this.apiKey = apiKey
        return this
    }

    private var model: String = "claude-3-opus-20240229"

    fun withModel(model: String): ClaudeClientBuilder {
        this.model = model
        return this
    }

    private var maxTokens: Int = 2048

    fun withMaxTokens(maxTokens: Int): ClaudeClientBuilder {
        this.maxTokens = maxTokens
        return this
    }

    private var okHttpClient: OkHttpClient = OkHttpClient()

    fun withOkHttpClient(okHttpClient: OkHttpClient): ClaudeClientBuilder {
        this.okHttpClient = okHttpClient
        return this
    }

    private var gson: Gson = Gson()

    fun withGson(gson: Gson): ClaudeClientBuilder {
        this.gson = gson
        return this
    }

    private var systemPrompt: String? = null

    fun withSystemPrompt(systemPrompt: String): ClaudeClientBuilder {
        this.systemPrompt = systemPrompt
        return this
    }

    private fun validate(): List<String> {
        val errors = mutableListOf<String>()
        if (apiKey == null) {
            errors.add("API key is not set.")
        }
        return errors
    }

    fun build(): ClaudeClient {
        val errors = validate()
        if (errors.isEmpty()) {
            return apiKey?.let {
                ClaudeClient(
                    apiKey = it,
                    model = model,
                    okHttpClient = okHttpClient,
                    maxTokens = maxTokens,
                    gson = gson,
                    systemPrompt = systemPrompt
                )
            } ?: throw Exception("No API key provided")
        } else throw Exception(errors.joinToString())
    }

}