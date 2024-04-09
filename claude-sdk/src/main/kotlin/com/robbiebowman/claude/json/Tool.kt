package com.robbiebowman.claude.json

class Tool(
    val toolName: String,
    val description: String,
    val parameters: List<Parameter>
) {

    data class Parameter(
        val name: String,
        val type: String,
        val description: String
    )

    internal fun toJson(): String {
        return ""
    }
}
