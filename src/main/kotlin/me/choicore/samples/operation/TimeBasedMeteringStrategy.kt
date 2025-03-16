package me.choicore.samples.operation

import java.time.LocalDate

interface TimeBasedMeteringStrategy {
    fun applies(measuredOn: LocalDate): Boolean
}
