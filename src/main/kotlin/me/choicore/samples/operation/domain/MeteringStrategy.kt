package me.choicore.samples.operation.domain

import java.time.LocalDate

interface MeteringStrategy : Meter {
    val lotId: Long

    fun applies(measuredOn: LocalDate): Boolean
}
