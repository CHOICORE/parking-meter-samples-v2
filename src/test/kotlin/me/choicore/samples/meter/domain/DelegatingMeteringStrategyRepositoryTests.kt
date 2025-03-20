package me.choicore.samples.meter.domain

import me.choicore.samples.context.entity.ForeignKey
import me.choicore.samples.context.entity.PrimaryKey
import me.choicore.samples.meter.domain.MeteringStrategy.DayOfWeekMeteringStrategy
import me.choicore.samples.meter.domain.MeteringStrategy.SpecifiedDateMeteringStrategy
import me.choicore.samples.meter.domain.MeteringStrategy.TimeSlotMeteringStrategy
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.TestConstructor
import org.springframework.test.context.TestConstructor.AutowireMode.ALL
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import java.time.LocalDateTime

@SpringBootTest
@TestConstructor(autowireMode = ALL)
@Transactional
class DelegatingMeteringStrategyRepositoryTests(
    private val delegatingMeteringStrategyRepository: DelegatingMeteringStrategyRepository,
) {
    @Test
    fun t1() {
        val lotId = ForeignKey(1L)
        val dayOfWeekMeteringStrategyEntity =
            DayOfWeekMeteringStrategyEntity(
                id = PrimaryKey.UNINITIALIZED,
                lotId = lotId,
                strategy =
                    DayOfWeekMeteringStrategy(
                        timeSlotMeter = TimeSlotMeter(),
                        effectiveDate = LocalDate.now(),
                    ),
                registeredAt = LocalDateTime.now(),
                registeredBy = "test",
            )
        val savedDayOfWeekMeteringStrategyEntity =
            delegatingMeteringStrategyRepository.save(dayOfWeekMeteringStrategyEntity) as DayOfWeekMeteringStrategyEntity

        assertThat(savedDayOfWeekMeteringStrategyEntity).isNotNull
        assertThat(savedDayOfWeekMeteringStrategyEntity.id.value).isGreaterThan(0)

        val specifiedDateMeteringStrategyEntity =
            SpecifiedDateMeteringStrategyEntity(
                id = PrimaryKey.UNINITIALIZED,
                lotId = lotId,
                strategy =
                    SpecifiedDateMeteringStrategy(
                        timeSlotMeter = TimeSlotMeter(),
                        specifiedDate = LocalDate.now(),
                    ),
                registeredAt = LocalDateTime.now(),
                registeredBy = "test",
            )
        val savedSpecifiedDateMeteringStrategyEntity =
            delegatingMeteringStrategyRepository.save(specifiedDateMeteringStrategyEntity) as SpecifiedDateMeteringStrategyEntity

        assertThat(savedSpecifiedDateMeteringStrategyEntity).isNotNull
        assertThat(savedSpecifiedDateMeteringStrategyEntity.id.value).isGreaterThan(0)
    }

    @Test
    fun t2() {
        val found: List<TimeSlotMeteringStrategy> = delegatingMeteringStrategyRepository.findByLotId(ForeignKey(1L))
    }
}
