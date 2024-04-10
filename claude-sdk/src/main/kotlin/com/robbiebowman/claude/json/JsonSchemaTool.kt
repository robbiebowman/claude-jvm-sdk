package com.robbiebowman.claude.json

import com.fasterxml.jackson.module.jsonSchema.JsonSchema

data class JsonSchemaTool(
    val name: String,
    val description: String?,
    val parameters: Map<String?, JsonSchema>
)
