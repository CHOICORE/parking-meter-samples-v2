package me.choicore.samples.operation.infrastructure.exposed

import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class MeteringStrategyTableTests {
    @Test
    fun t1() {
        transaction {
            MeteringStrategyTable
                .select(MeteringStrategyTable.columns)
                .where {
                    MeteringStrategyTable.lotId eq 1L
                }
        }
    }
}
