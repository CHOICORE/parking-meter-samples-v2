package me.choicore.samples.support.jackson.mixin

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.JsonMappingException
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import me.choicore.samples.meter.domain.MeteringMode
import me.choicore.samples.meter.domain.MeteringMode.ONCE
import me.choicore.samples.meter.domain.MeteringMode.REPEAT
import me.choicore.samples.meter.domain.MeteringStrategy
import me.choicore.samples.meter.domain.TimelineMeter
import java.time.LocalDate

class MeteringStrategyDeserializer : JsonDeserializer<MeteringStrategy>() {
    override fun deserialize(
        jp: JsonParser,
        ctxt: DeserializationContext,
    ): MeteringStrategy {
        val objectMapper: ObjectMapper = jp.codec as ObjectMapper
        val jsonNode = objectMapper.readTree<JsonNode>(jp)
        val timelineMeterNode = readRequiredJsonNode(jsonNode, "timelineMeter", jp)
        val effectiveDateNode = readRequiredJsonNode(jsonNode, "effectiveDate", jp)
        val timelineMeter = objectMapper.treeToValue(timelineMeterNode, TimelineMeter::class.java)
        val effectiveDate = LocalDate.parse(effectiveDateNode.asText())
        val meteringMode = MeteringMode.valueOf(readRequiredJsonNode(jsonNode, "meteringMode", jp).asText())

        return when (meteringMode) {
            REPEAT ->
                MeteringStrategy.DayOfWeekBasedMeteringStrategy(
                    timelineMeter = timelineMeter,
                    effectiveDate = effectiveDate,
                )

            ONCE ->
                MeteringStrategy.SpecifiedDateBasedMeteringStrategy(
                    timelineMeter = timelineMeter,
                    effectiveDate = effectiveDate,
                )
        }
    }

    private fun readRequiredJsonNode(
        jsonNode: JsonNode,
        field: String,
        jp: JsonParser,
    ): JsonNode {
        if (!jsonNode.has(field) || jsonNode.get(field).isNull) {
            throw JsonMappingException.from(jp, "Missing required field: $field")
        }
        return jsonNode.get(field)
    }
}
