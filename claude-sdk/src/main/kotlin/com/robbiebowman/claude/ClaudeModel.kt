package com.robbiebowman.claude

enum class ClaudeModel(val spec: String) {
    Opus("claude-3-opus-20240229"),
    Haiku("claude-3-haiku-20240307"),
    Sonnet("claude-3-sonnet-20240229"),
    Old2dot1("claude-2.1"),
    Old2dot0("claude-2.0"),
    OldClaudeInstant("claude-instant-1.2")
}