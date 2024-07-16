package com.robbiebowman.claude

import com.fasterxml.jackson.annotation.JsonFilter
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider
import com.fasterxml.jackson.module.jsonSchema.JsonSchema
import com.fasterxml.jackson.module.jsonSchema.JsonSchemaGenerator
import com.fasterxml.jackson.module.jsonSchema.types.ObjectSchema
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.robbiebowman.claude.json.IgnoreRequiredFieldFilter
import com.robbiebowman.claude.json.JsonSchemaTool
import okhttp3.OkHttpClient
import java.time.Duration
import java.time.temporal.ChronoUnit
import kotlin.reflect.KAnnotatedElement
import kotlin.reflect.KFunction
import kotlin.reflect.javaType

/**
 * Claude client builder
 *
 * @constructor Create empty Claude client builder
 */
class ClaudeClientBuilder {

    private var okHttpClient: OkHttpClient = OkHttpClient().newBuilder().apply {
        callTimeout(Duration.of(180, ChronoUnit.MINUTES))
        readTimeout(Duration.of(180, ChronoUnit.MINUTES))
        writeTimeout(Duration.of(180, ChronoUnit.MINUTES))
    }.build()
    val toolDefinitions = mutableListOf<JsonSchemaTool>()
    private var mapper = jacksonObjectMapper().apply {
        propertyNamingStrategy = PropertyNamingStrategies.SNAKE_CASE
        setSerializationInclusion(JsonInclude.Include.NON_NULL)
        configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
        configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        addMixIn(JsonSchema::class.java, IgnoreRequiredFieldFilter::class.java)
        setFilterProvider(IgnoreRequiredFieldFilter.provider)
    }
    private var schemaGenerator = JsonSchemaGenerator(mapper)
    private val stopSequences = mutableSetOf<String>()
    private var model: String = "claude-3-opus-20240229"
    private var apiKey: String? = null
    private var systemPrompt: String? = null
    private var maxTokens: Int = 2048

    /**
     * Required to be set. The API key for communicating with the Claude API.
     *
     * @param apiKey
     * @return This instance of the client build with the new value
     */
    fun withApiKey(apiKey: String): ClaudeClientBuilder {
        this.apiKey = apiKey
        return this
    }

    /**
     * Sets the Claude model from free text. Used for future compatibility.
     *
     * @param model
     * @return This instance of the client build with the new value
     */
    fun withModel(model: String): ClaudeClientBuilder {
        this.model = model
        return this
    }

    /**
     * Sets the Claude model from the enumerated values
     *
     * @param model
     * @return This instance of the client build with the new value
     */
    fun withModel(model: ClaudeModel): ClaudeClientBuilder {
        this.model = model.spec
        return this
    }

    /**
     * Sets the max tokens for the requests
     *
     * @param maxTokens
     * @return This instance of the client build with the new value
     */
    fun withMaxTokens(maxTokens: Int): ClaudeClientBuilder {
        this.maxTokens = maxTokens
        return this
    }

    /**
     * Sets a custom configured OkHttpClient
     *
     * @param okHttpClient
     * @return This instance of the client build with the new value
     */
    fun withOkHttpClient(okHttpClient: OkHttpClient): ClaudeClientBuilder {
        this.okHttpClient = okHttpClient
        return this
    }

    /**
     * Sets a custom jackson parser.
     *
     * @param gson
     * @return This instance of the client build with the new value
     */
    fun withMapper(mapper: ObjectMapper): ClaudeClientBuilder {
        this.mapper = mapper
        schemaGenerator = JsonSchemaGenerator(mapper)
        return this
    }

    /**
     * Sets the client's system prompt
     *
     * @param systemPrompt
     * @return This instance of the client build with the new value
     */
    fun withSystemPrompt(systemPrompt: String): ClaudeClientBuilder {
        this.systemPrompt = systemPrompt
        return this
    }

    /**
     * Adds a tool based on the supplied function, including its parameters' names, types, and description annotations.
     *
     * @param R
     * @param function
     * @return This instance of the client build with the new value
     */
    @OptIn(ExperimentalStdlibApi::class)
    fun withTool(function: KFunction<*>): ClaudeClientBuilder {
        val paramSchema = function.parameters.associate {
            val toolDescription = getToolDescription(it)
            val type = mapper.typeFactory.constructType(it.type.javaType)
            val schema = schemaGenerator.generateSchema(type).apply {
                id = null
                description = toolDescription
            }
            it.name!! to schema
        }
        val definition = JsonSchemaTool(
            name = function.name,
            description = getToolDescription(function),
            input_schema = ObjectSchema().apply {
                properties = paramSchema
            }
        )
        toolDefinitions.add(definition)
        return this
    }

    /**
     * Adds a stop sequence to the client. This is a string which causes Claude to stop outputting tokens and return
     * immediately. The message response will not include the stop sequence itself.
     *
     * @param stopSequence
     * @return This instance of the client build with the new value
     */
    fun withStopSequence(stopSequence: String): ClaudeClientBuilder {
        stopSequences.add(stopSequence)
        return this
    }

    /**
     * Builds the client
     *
     * @return A client with the configured values
     */
    fun build(): ClaudeClient {
        val errors = validate()
        if (errors.isEmpty()) {
            return apiKey?.let {
                ClaudeClient(
                    apiKey = it,
                    model = model,
                    okHttpClient = okHttpClient,
                    maxTokens = maxTokens,
                    mapper = mapper,
                    systemPrompt = systemPrompt,
                    stopSequences = stopSequences,
                    tools = toolDefinitions
                )
            } ?: throw Exception("No API key provided")
        } else throw Exception(errors.joinToString())
    }

    private fun validate(): List<String> {
        val errors = mutableListOf<String>()
        if (apiKey == null) {
            errors.add("API key is not set.")
        }
        return errors
    }

    private fun getToolDescription(annotated: KAnnotatedElement): String? {
        val annotation =
            annotated.annotations.firstOrNull { it is ToolDescription } as ToolDescription?
        return annotation?.value
    }

}