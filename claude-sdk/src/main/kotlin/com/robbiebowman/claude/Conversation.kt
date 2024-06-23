package com.robbiebowman.claude

import com.fasterxml.jackson.annotation.*
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.robbiebowman.claude.json.FlexibleStringDeserializer

/**
 * Role represents the author of a message. Claude's messages and tool calls will be Assistant. User responses and tool
 * results will be User.
 *
 * The messages API will reject completion requests with messages from the same Role twice in a row.
 */
enum class Role {
    @JsonProperty("user")
    User,

    @JsonProperty("assistant")
    Assistant
}

/**
 * An image to be included in a Message. Can be either the base64 encoded bytes or a publicly accessible url to an image.
 * The OkHttpClient in the ClaudeClient will resolve image urls before sending to Claude.
 *
 */
class Image {
    internal val imageUrl: String?
    internal val imageContents: ByteArray?
    internal val mediaType: String?

    constructor(imageUrl: String) {
        this.imageUrl = imageUrl
        this.imageContents = null
        this.mediaType = null
    }

    constructor(imageContent: ByteArray, mediaType: String) {
        this.imageUrl = null
        this.imageContents = imageContent
        this.mediaType = mediaType
    }
}

data class SerializableMessage(
    val role: Role,
    val content: List<MessageContent>
)

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes(
    JsonSubTypes.Type(value = MessageContent.TextContent::class, name = "text"),
    JsonSubTypes.Type(value = MessageContent.ImageContent::class, name = "image"),
    JsonSubTypes.Type(value = MessageContent.ToolUse::class, name = "tool_use"),
    JsonSubTypes.Type(value = MessageContent.ToolResult::class, name = "tool_result")
)
sealed class MessageContent {
    @JsonTypeName("text")
    class TextContent(val text: String) : MessageContent()

    @JsonTypeName("image")
    class ImageContent(val source: ResolvedImageContent) : MessageContent()

    @JsonTypeName("tool_use")
    class ToolUse(
        val id: String,
        val name: String,
        @JsonDeserialize(using = FlexibleStringDeserializer::class) val input: Map<String, String>
    ) : MessageContent()

    @JsonTypeName("tool_result")
    class ToolResult(
        @JsonProperty("tool_use_id") val toolUseId: String,
        val content: String?
    ) : MessageContent()
}

data class ResolvedImageContent(
    val data: String,
    @JsonProperty("media_type") val mediaType: String
) {
    val type: String = "base64"
}
