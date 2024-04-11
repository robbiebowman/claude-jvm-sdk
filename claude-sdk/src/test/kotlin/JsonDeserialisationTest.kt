import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.MapperFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.xml.JacksonXmlModule
import com.fasterxml.jackson.dataformat.xml.XmlMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.junit.Test
import kotlin.test.assertNotNull
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.robbiebowman.claude.SerializableMessage
import com.robbiebowman.claude.xml.InvokeRequest
import kotlin.test.assertEquals

class JsonDeserialisationTest {
    @Test
    fun `test`() {
        val json = """
            {
              "id": "msg_01CzYT3567hLi3HEgJWfJRbZ",
              "type": "message",
              "role": "assistant",
              "content": [
                {
                  "type": "tool_use",
                  "id": "toolu_01EVMgCsbXiP9jBUfM7zhxUc",
                  "name": "getCurrentMayor",
                  "input": {
                    "city": "New York",
                    "country": "United States"
                  }
                }
              ],
              "model": "claude-3-haiku-20240307",
              "stop_reason": "tool_use",
              "stop_sequence": null,
              "usage": {
                "input_tokens": 421,
                "output_tokens": 73
              }
            }
        """.trimIndent()
        val mapper = jacksonObjectMapper().apply {
            configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        }

        val result = mapper.readValue(json, SerializableMessage::class.java)

        assertNotNull(result)
    }
}
