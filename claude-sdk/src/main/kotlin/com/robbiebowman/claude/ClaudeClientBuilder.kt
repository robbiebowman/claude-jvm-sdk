package com.robbiebowman.claude

import com.google.gson.Gson
import com.robbiebowman.claude.xml.Parameter
import com.robbiebowman.claude.xml.ToolDescription
import jdk.jfr.Description
import okhttp3.OkHttpClient
import kotlin.reflect.KFunction
import kotlin.reflect.KType
import kotlin.reflect.full.isSupertypeOf
import kotlin.reflect.typeOf

class ClaudeClientBuilder {

    private var apiKey: String? = null
    val toolDefinitions = mutableListOf<String>()
    val stopSequences = mutableListOf<String>()
    private var model: String = "claude-3-opus-20240229"
    private var gson: Gson = Gson()
    private var maxTokens: Int = 2048
    private var systemPrompt: String? = null

    fun withApiKey(apiKey: String): ClaudeClientBuilder {
        this.apiKey = apiKey
        return this
    }

    fun withModel(model: String): ClaudeClientBuilder {
        this.model = model
        return this
    }

    fun withMaxTokens(maxTokens: Int): ClaudeClientBuilder {
        this.maxTokens = maxTokens
        return this
    }

    private var okHttpClient: OkHttpClient = OkHttpClient()

    fun withOkHttpClient(okHttpClient: OkHttpClient): ClaudeClientBuilder {
        this.okHttpClient = okHttpClient
        return this
    }

    fun withGson(gson: Gson): ClaudeClientBuilder {
        this.gson = gson
        return this
    }

    fun withSystemPrompt(systemPrompt: String): ClaudeClientBuilder {
        this.systemPrompt = systemPrompt
        return this
    }

    inline fun <reified R> withTool(function: KFunction<R>): ClaudeClientBuilder {
        val definition =
            ToolDescription(
                toolName = function.name,
                description = "",
                parameters = function.parameters.map {
                    val claudeType = when(it.type) {
                        typeOf<Int>()::isSupertypeOf -> "integer"
                        typeOf<Number>()::isSupertypeOf -> "number"
                        typeOf<Boolean>()::isSupertypeOf -> "boolean"
                        typeOf<String>()::isSupertypeOf -> "string"
                        else -> "string"
                    }
                    Parameter(
                        name = it.name!!,
                        type = claudeType,
                        description = ""
                    )
                })
        toolDefinitions.add(definition.toXml())
        return this
    }

    fun withStopSequence(stopSequence: String): ClaudeClientBuilder {
        stopSequences.add(stopSequence)
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
            val systemPromptAndTools = toolsToSystemPrompt(systemPrompt, toolDefinitions)
            return apiKey?.let {
                ClaudeClient(
                    apiKey = it,
                    model = model,
                    okHttpClient = okHttpClient,
                    maxTokens = maxTokens,
                    gson = gson,
                    systemPrompt = systemPromptAndTools,
                    stopSequences = stopSequences
                )
            } ?: throw Exception("No API key provided")
        } else throw Exception(errors.joinToString())
    }

    private fun toolsToSystemPrompt(startingPrompt: String?, tools: List<String>): String? {
        return if (tools.isEmpty()) startingPrompt
        else {
            startingPrompt.orEmpty().plus(
                """
                In this environment you have access to a set of tools you can use to answer the user's question.

                You may call them like this:
                <function_calls>
                <invoke>
                <tool_name>${'$'}TOOL_NAME</tool_name>
                <parameters>
                <${'$'}PARAMETER_NAME>${'$'}PARAMETER_VALUE</${'$'}PARAMETER_NAME>
                ...
                </parameters>
                </invoke>
                </function_calls>

                Here are the tools available:
                
                """.trimIndent().plus(tools.joinToString("\n\n"))
            )
        }
    }

}