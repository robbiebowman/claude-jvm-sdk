package com.robbiebowman.claude

import com.google.gson.annotations.SerializedName
import okhttp3.OkHttpClient
import okhttp3.Request

/**
 * Role represents the author of a message. Claude's messages and tool calls will be Assistant. User responses and tool
 * results will be User.
 *
 * The messages API will reject completion requests with messages from the same Role twice in a row.
 */
enum class Role {
    @SerializedName("user")
    User,

    @SerializedName("assistant")
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

/**
 * Messages represent non-tool call responses, and user inputs for Claude.
 *
 * They can include an arbitrary number of images.
 */
class Message {
    internal val role: Role
    internal val message: String
    internal val images: List<Image>

    constructor(role: Role, message: String) {
        this.role = role
        this.message = message
        this.images = emptyList()
    }

    constructor(message: String, images: List<Image>) {
        this.role = Role.User
        this.message = message
        this.images = images
    }
}

internal data class SerializableMessage(
    val role: Role,
    val content: List<MessageContent>
)

internal sealed class MessageContent(val type: String) {
    class TextContent(val text: String) : MessageContent("text")

    class ImageContent(val source: ResolvedImageContent) : MessageContent("image")
}

internal data class ResolvedImageContent(
    val data: String,
    @SerializedName("media_type") val mediaType: String
) {
    val type: String = "base64"
}
