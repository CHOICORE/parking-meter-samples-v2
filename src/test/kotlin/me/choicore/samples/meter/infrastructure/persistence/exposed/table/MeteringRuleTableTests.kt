package me.choicore.samples.meter.infrastructure.persistence.exposed.table

import me.choicore.samples.meter.domain.MeteringMode
import me.choicore.samples.support.exposed.exists
import org.jetbrains.exposed.sql.Case
import org.jetbrains.exposed.sql.SortOrder.DESC
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.alias
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.exists
import org.jetbrains.exposed.sql.intLiteral
import org.jetbrains.exposed.sql.notExists
import org.jetbrains.exposed.sql.or
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.unionAll
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate

@SpringBootTest
@Transactional
class MeteringRuleTableTests {
    @Test
    fun t1() {
        val lotId = 1L
        val effectiveDate = LocalDate.now().minusMonths(2)

        val once =
            MeteringRuleTable
                .selectAll()
                .where {
                    (MeteringRuleTable.lotId eq lotId) and
                        (MeteringRuleTable.effectiveDate eq effectiveDate) and
                        (MeteringRuleTable.meteringMode eq MeteringMode.ONCE)
                }

        val repeat =
            MeteringRuleTable
                .selectAll()
                .where {
                    (MeteringRuleTable.lotId eq lotId) and
                        (MeteringRuleTable.effectiveDate lessEq effectiveDate) and
                        (MeteringRuleTable.meteringMode eq MeteringMode.REPEAT) and
                        notExists(
                            MeteringRuleTable
                                .select(intLiteral(1))
                                .where {
                                    (MeteringRuleTable.lotId eq lotId) and
                                        (MeteringRuleTable.meteringMode eq MeteringMode.ONCE) and
                                        (MeteringRuleTable.effectiveDate eq effectiveDate)
                                },
                        )
                }.orderBy(MeteringRuleTable.effectiveDate, DESC)
                .limit(1)

        val single =
            once.unionAll(repeat).singleOrNull()?.let {
            }
    }

    @Test
    fun t4() {
        val exists =
            MeteringRuleTable.exists {
                (MeteringRuleTable.lotId eq 1) and
                    (MeteringRuleTable.effectiveDate eq LocalDate.now().minusMonths(2)) and
                    (MeteringRuleTable.meteringMode inList MeteringMode.entries) and
                    (MeteringRuleTable.deletedAt.isNull())
            }

        println(exists)
    }

    @Test
    fun t3() {
        val empty =
            MeteringRuleTable
                .select(
                    exists(
                        MeteringRuleTable
                            .select(intLiteral(1))
                            .where {
                                (MeteringRuleTable.lotId eq 1) and
                                    (MeteringRuleTable.effectiveDate eq LocalDate.now().minusMonths(1)) and
                                    (MeteringRuleTable.meteringMode inList MeteringMode.entries) and
                                    (MeteringRuleTable.deletedAt.isNull())
                            },
                    ),
                )

        val exists =
            exists(
                MeteringRuleTable
                    .select(intLiteral(1))
                    .where {
                        (MeteringRuleTable.lotId eq 1) and
                            (MeteringRuleTable.effectiveDate eq LocalDate.now()) and
                            (MeteringRuleTable.meteringMode inList MeteringMode.entries) and
                            (MeteringRuleTable.deletedAt.isNull())
                    },
            )

        val first = Table.Dual.select(exists).first()
        val b = first[exists]
        println(b)
    }

    @Test
    fun t2() {
        val priority =
            Case()
                .When(
                    MeteringRuleTable.meteringMode eq MeteringMode.ONCE,
                    intLiteral(0),
                ).Else(
                    intLiteral(1),
                ).alias("priority")

        MeteringRuleTable
            .select(MeteringRuleTable.columns + priority)
            .where {
                (MeteringRuleTable.lotId eq 1L)
                ((MeteringRuleTable.meteringMode eq MeteringMode.ONCE) and (MeteringRuleTable.effectiveDate eq LocalDate.now())) or
                    (
                        (MeteringRuleTable.meteringMode eq MeteringMode.REPEAT) and
                            (MeteringRuleTable.effectiveDate lessEq LocalDate.now())
                    )
            }.orderBy(priority)
            .map {
            }
    }
}
