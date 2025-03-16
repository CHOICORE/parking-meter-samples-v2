package me.choicore.samples.operation

class MeteringHours(
    periods: List<MeteringPeriod> = emptyList(),
) {
    val periods: List<MeteringPeriod> = MeteringPeriodFactory.fullest(periods)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is MeteringHours) return false

        if (periods != other.periods) return false

        return true
    }

    override fun hashCode(): Int = periods.hashCode()

    override fun toString(): String = "MeteringHours(periods=$periods)"
}
