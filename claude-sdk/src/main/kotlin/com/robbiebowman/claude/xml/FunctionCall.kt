package com.robbiebowman.claude.xml

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement

@JacksonXmlRootElement(localName = "invoke")
internal data class InvokeRequest(
    @JacksonXmlProperty(localName = "tool_name") val toolName: String,

    @JacksonXmlProperty(localName = "parameters")
    @JsonDeserialize(using = InfoDeserializer::class)
    val arguments: Map<String, String>
)

class InfoDeserializer : JsonDeserializer<Map<String, String>>() {
    override fun deserialize(p: JsonParser, ctxt: DeserializationContext): Map<String, String> {
        val currentNode = p.codec.readTree<ObjectNode>(p)
        val fieldName = currentNode.fieldNames().next()
        val props = currentNode.get(fieldName).properties()
        return props.associate { (key, value) -> key to value.asText() }
    }
}
