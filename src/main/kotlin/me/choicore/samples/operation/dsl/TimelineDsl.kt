package me.choicore.samples.operation.dsl

import me.choicore.samples.operation.Timeline
import me.choicore.samples.operation.Timeline.Builder

@DslMarker
annotation class TimelineDsl

fun Timeline(block: Builder.() -> Unit): Timeline {
    val builder: Builder = Timeline.builder()
    builder.block()
    return builder.build()
}
