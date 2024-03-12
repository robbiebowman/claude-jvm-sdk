package com.robbiebowman.claude

sealed class ClaudeResponse {
    class ChatResponse(val message: String) : ClaudeResponse() {

    }
    class ToolCall(val toolName: String, val arguments: List<Argument>) : ClaudeResponse() {

        data class Argument(val parameter: String, val argumentValue: String)
    }
}