package com.robbiebowman.claude.json

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.module.jsonSchema.JsonSchema
import com.fasterxml.jackson.module.jsonSchema.types.ObjectSchema
import com.google.gson.annotations.SerializedName

data class JsonSchemaTool(
    val name: String,
    val description: String?,
    val input_schema: ObjectSchema
)
