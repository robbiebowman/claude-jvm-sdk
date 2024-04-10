package com.robbiebowman.claude

sealed class ClaudeResponse {

    abstract fun toMessage(): Message

    class ChatResponse internal constructor(val message: String) : ClaudeResponse() {
        override fun toMessage(): Message {
            return Message(Role.Assistant, message)
        }
    }

    class ToolCall internal constructor(
        private val id: String,
        val toolName: String,
        val arguments: Map<String, String>,
    ) : ClaudeResponse() {

        override fun toMessage(): Message {
            return Message(Role.Assistant)
        }

        data class Argument internal constructor(val parameter: String, val argumentValue: String)

        fun toResult(result: String): Message {
            val xml = """
            <function_results>
            <result>
            <tool_name>${toolName}</tool_name>
            <stdout>
            $result
            </stdout>
            </result>
            </function_results>
        """.trimIndent()
            return Message(Role.User, xml)
        }
    }
}
