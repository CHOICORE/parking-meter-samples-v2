package me.choicore.samples.operation.support.jackson.mixin

import com.fasterxml.jackson.annotation.JsonIgnore
import me.choicore.samples.operation.domain.TimeSlot
import org.springframework.boot.jackson.JsonMixin
import java.time.Duration

@JsonMixin(TimeSlot::class)
abstract class TimeSlotMixin {
    @get:JsonIgnore
    abstract val duration: Duration
}
