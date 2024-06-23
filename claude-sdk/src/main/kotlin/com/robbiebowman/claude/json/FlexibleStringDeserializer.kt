package com.robbiebowman.claude.json

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import java.io.IOException


class FlexibleStringDeserializer : JsonDeserializer<Map<String, String>>() {
    @Throws(IOException::class)
    override fun deserialize(p: JsonParser, ctxt: DeserializationContext): Map<String, String> {
        val node = p.codec.readTree<JsonNode>(p)
        val result: MutableMap<String, String> = HashMap()
        val objectMapper = ObjectMapper()

        if (node.isObject) {
            val fields = node.fields()
            while (fields.hasNext()) {
                val entry = fields.next()
                val key = entry.key
                val value = entry.value
                result[key] = objectMapper.writeValueAsString(value)
            }
        } else if (node.isTextual) {
            // If it's a simple string, we'll use an empty key
            result[""] = node.asText()
        } else {
            // For other types, we'll use an empty key and the string representation
            result[""] = objectMapper.writeValueAsString(node)
        }

        return result
    }
}