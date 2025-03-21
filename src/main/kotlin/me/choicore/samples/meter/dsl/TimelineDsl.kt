package me.choicore.samples.meter.dsl

import me.choicore.samples.meter.Timeline
import me.choicore.samples.meter.Timeline.Builder

@DslMarker
annotation class TimelineDsl

internal fun Timeline(block: Builder.() -> Unit): Timeline = Timeline.builder().apply(block).build()
