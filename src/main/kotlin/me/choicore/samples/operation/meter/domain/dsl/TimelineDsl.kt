package me.choicore.samples.operation.meter.domain.dsl

import me.choicore.samples.operation.meter.domain.Timeline
import me.choicore.samples.operation.meter.domain.Timeline.Builder

@DslMarker
annotation class TimelineDsl

internal fun Timeline(block: Builder.() -> Unit): Timeline = Timeline.builder().apply(block).build()
