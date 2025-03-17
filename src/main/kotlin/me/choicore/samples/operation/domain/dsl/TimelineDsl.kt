package me.choicore.samples.operation.domain.dsl

import me.choicore.samples.operation.domain.Timeline
import me.choicore.samples.operation.domain.Timeline.Builder

@DslMarker
annotation class TimelineDsl

internal fun Timeline(block: Builder.() -> Unit): Timeline = Timeline.builder().apply(block).build()
