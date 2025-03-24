package me.choicore.samples.support.jackson.mixin

import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import me.choicore.samples.meter.domain.MeteringStrategy
import org.springframework.boot.jackson.JsonMixin

@JsonDeserialize(using = MeteringStrategyDeserializer::class)
@JsonMixin(MeteringStrategy::class)
abstract class MeteringStrategyMixin
