package me.choicore.samples.support.jackson.mixin

import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import me.choicore.samples.meter.domain.MeteringRule
import org.springframework.boot.jackson.JsonMixin

@JsonDeserialize(using = MeteringRuleDeserializer::class)
@JsonMixin(MeteringRule::class)
abstract class MeteringRuleMixin
