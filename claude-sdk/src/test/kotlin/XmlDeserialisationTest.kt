import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.MapperFeature
import com.fasterxml.jackson.dataformat.xml.JacksonXmlModule
import com.fasterxml.jackson.dataformat.xml.XmlMapper
import org.junit.Test
import kotlin.test.assertNotNull
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.robbiebowman.claude.xml.InvokeRequest
import kotlin.test.assertEquals

class XmlDeserialisationTest {
    @Test
    fun `test`() {
        val functionCallResponse = """  
        <invoke>
        <tool_name>get_ticker_symbol</tool_name>
        <parameters>
        <company_name>General Motors</company_name>
        <market_cap>150 B</market_cap>
        </parameters>
        </invoke>
        """.trimIndent()

        val mapper = XmlMapper(JacksonXmlModule().apply { setDefaultUseWrapper(false) })
            .registerKotlinModule()
            .configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true)
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true)

        val functionCall = mapper.readValue(functionCallResponse, InvokeRequest::class.java)

        assertEquals(2, functionCall.arguments.size)
        assertEquals("get_ticker_symbol", functionCall.toolName)
    }
}
