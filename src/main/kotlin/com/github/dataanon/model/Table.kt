package com.github.dataanon.model

abstract class Table(val name: String) {
    val columnsToBeAnonymized = mutableMapOf<String,Any>()

    fun anonymize(columnName: String): com.github.dataanon.model.Table.Column {
        val column = Column(columnName)
        columnsToBeAnonymized[columnName] = com.github.dataanon.strategy.string.FixedString("DEFAULT VALUE")
        return column
    }

    // TODO: remove from DSL
    fun generateSelectQuery(limit: Long): String {

        val selectClause = "SELECT "
        val columnSelectionClause = allColumns().joinToString(",")
        val fromClause = " FROM $name "
        val limitClause = if(limit > 0) " LIMIT $limit " else ""

        return selectClause + columnSelectionClause + fromClause + limitClause
    }

    abstract fun generateWriteQuery(): String

    abstract fun allColumns(): List<String>

    inner class Column(private val name: String) {
        fun <T: Any> using(strategy: com.github.dataanon.strategy.AnonymizationStrategy<T>) {
            columnsToBeAnonymized[name] = strategy
        }
    }
}