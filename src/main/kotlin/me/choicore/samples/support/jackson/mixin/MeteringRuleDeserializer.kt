package me.choicore.samples.support.jackson.mixin

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.JsonMappingException
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.MissingNode
import me.choicore.samples.context.entity.PrimaryKey
import me.choicore.samples.context.entity.SecondaryKey
import me.choicore.samples.meter.domain.MeteringRule
import me.choicore.samples.meter.domain.MeteringStrategy
import java.time.LocalDateTime

class MeteringRuleDeserializer : JsonDeserializer<MeteringRule>() {
    override fun deserialize(
        jp: JsonParser,
        ctxt: DeserializationContext,
    ): MeteringRule {
        val objectMapper: ObjectMapper = jp.codec as ObjectMapper
        val jsonNode: JsonNode = objectMapper.readTree<JsonNode>(jp)

        val id: Long = readRequiredJsonNode(jsonNode, "id", jp).asLong()
        val lotId: Long = readRequiredJsonNode(jsonNode, "lotId", jp).asLong()

        val meteringStrategy: MeteringStrategy =
            objectMapper.treeToValue(
                readRequiredJsonNode(jsonNode, "meteringStrategy", jp),
                MeteringStrategy::class.java,
            )
        val registeredAt = LocalDateTime.parse(readRequiredJsonNode(jsonNode, "registeredAt", jp).asText())
        val registeredBy = readRequiredJsonNode(jsonNode, "registeredBy", jp).asText()

        val meteringRule =
            MeteringRule(
                id = PrimaryKey(id),
                lotId = SecondaryKey(lotId),
                meteringStrategy = meteringStrategy,
                registeredAt = registeredAt,
                registeredBy = registeredBy,
            )
        val modifiedAtNode = readJsonNode(jsonNode, "modifiedAt")
        if (!modifiedAtNode.isMissingNode && !modifiedAtNode.isNull) {
            meteringRule.modifiedAt = LocalDateTime.parse(modifiedAtNode.asText())
        }

        val modifiedByNode = readJsonNode(jsonNode, "modifiedBy")
        if (!modifiedByNode.isMissingNode && !modifiedByNode.isNull) {
            meteringRule.modifiedBy = modifiedByNode.asText()
        }

        val deletedAtNode = readJsonNode(jsonNode, "deletedAt")
        if (!deletedAtNode.isMissingNode && !deletedAtNode.isNull) {
            meteringRule.deletedAt = LocalDateTime.parse(deletedAtNode.asText())
        }

        val deletedByNode = readJsonNode(jsonNode, "deletedBy")
        if (!deletedByNode.isMissingNode && !deletedByNode.isNull) {
            meteringRule.deletedBy = deletedByNode.asText()
        }

        return meteringRule
    }

    private fun readJsonNode(
        jsonNode: JsonNode,
        field: String,
    ): JsonNode = if (jsonNode.has(field)) jsonNode.get(field) else MissingNode.getInstance()

    private fun readRequiredJsonNode(
        jsonNode: JsonNode,
        field: String,
        jp: JsonParser,
    ): JsonNode {
        if (!jsonNode.has(field) || jsonNode.get(field).isNull) {
            throw JsonMappingException.from(
                jp,
                "Missing required field: $field",
            )
        }
        return jsonNode.get(field)
    }
}
