package com.robbiebowman.claude.xml

import com.google.gson.annotations.SerializedName

class ToolDescription (
    val toolName: String,

    val description: String,
    val parameters: List<Parameter>
) {
    fun toXml(): String {
        return """
            <tool_description>
            <tool_name>$toolName</tool_name>
            <description>
            $description
            </description>
            <parameters>
            ${parameters.joinToString("\n") { param ->
            """
                <parameter>
                <name>${param.name}</name>
                <type>${param.type}</type>
                <description>${param.description}</description>
                </parameter>
            """.trimIndent() }}
            </parameters>
            </tool_description>
        """.trimIndent()
    }
}

data class Parameter (
    val name: String,
    val type: String,
    val description: String
)