package com.robbiebowman.claude.json

import com.fasterxml.jackson.module.jsonSchema.types.ObjectSchema

data class JsonSchemaTool(
    val name: String,
    val description: String?,
    val input_schema: ObjectSchema
)
