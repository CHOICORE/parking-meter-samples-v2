package me.choicore.samples.operation

data class Measurement(
    val measurand: Measurand,
    val measurer: Measurer,
    val measurement: Long,
    val factor: Double,
) {
    val cost: Long = TODO()
}
