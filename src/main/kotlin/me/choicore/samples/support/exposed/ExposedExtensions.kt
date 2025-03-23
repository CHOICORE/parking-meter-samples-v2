package me.choicore.samples.support.exposed

import org.jetbrains.exposed.sql.LiteralOp
import org.jetbrains.exposed.sql.Op
import org.jetbrains.exposed.sql.Query
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SqlExpressionBuilder
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.Table.Dual
import org.jetbrains.exposed.sql.exists
import org.jetbrains.exposed.sql.intLiteral

/**
 * 1을 나타내는 리터럴
 */
val SELECT_ONE: LiteralOp<Int> = intLiteral(1)

/**
 * 주어진 조건이 존재하는지 확인하는 확장 함수
 * @param where 확인할 조건
 * @return 조건을 만족하는 레코드가 존재하면 true, 아니면 false
 */
fun <T : Table> T.exists(where: SqlExpressionBuilder.() -> Op<Boolean>): Boolean =
    this
        .select(SELECT_ONE)
        .where { SqlExpressionBuilder.where() }
        .exists()

/**
 * 주어진 쿼리가 존재하는지 확인하는 확장 함수
 * @return 쿼리 결과가 존재하면 true, 아니면 false
 */
internal fun Query.exists(): Boolean {
    val resultRow: ResultRow = Dual.select(exists(this)).first()
    return resultRow[exists(this)]
}
