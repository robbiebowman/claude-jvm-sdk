package com.robbiebowman.claude

import com.google.gson.annotations.SerializedName
import okhttp3.OkHttpClient
import okhttp3.Request

enum class Role {
    @SerializedName("user")
    User,

    @SerializedName("assistant")
    Assistant
}

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

class Message {
    internal val role: Role
    internal val message: String
    internal val images: List<Image>

    constructor(role: Role, message: String) {
        this.role = role
        this.message = message
        this.images = emptyList()
    }

    constructor(role: Role, message: String, images: List<Image>) {
        this.role = role
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

data class ResolvedImageContent(
    val data: String,
    @SerializedName("media_type") val mediaType: String
) {
    val type: String = "base64"
}