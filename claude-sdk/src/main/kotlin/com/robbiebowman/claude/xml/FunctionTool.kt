package com.robbiebowman.claude.xml

class ToolDescription(
    val toolName: String,
    val description: String,
    val parameters: List<Parameter>
) {
    fun toXml(): String {
        val parameterDefinitions = parameters.joinToString("\n") { param ->
            """|<parameter>
               |<name>${param.name}</name>
               |<type>${param.type}</type>
               |<description>${param.description}</description>
               |</parameter>""".trimMargin()
        }
        return """|<tool_description>
               |<tool_name>$toolName</tool_name>
               |<description>$description</description>
               |<parameters>
               |$parameterDefinitions
               |</parameters>
               |</tool_description>""".trimMargin()
    }
}

data class Parameter(
    val name: String,
    val type: String,
    val description: String
)