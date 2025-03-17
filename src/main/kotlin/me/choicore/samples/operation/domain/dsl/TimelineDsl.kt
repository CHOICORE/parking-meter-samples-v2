package me.choicore.samples.operation.domain.dsl

import me.choicore.samples.operation.domain.Timeline
import me.choicore.samples.operation.domain.Timeline.Builder

@DslMarker
annotation class TimelineDsl

internal fun Timeline(block: Builder.() -> Unit): Timeline {
    val builder: Builder = Timeline.builder()
    builder.block()
    return builder.build()
}
