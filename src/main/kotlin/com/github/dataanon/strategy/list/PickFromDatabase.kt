package com.github.dataanon.strategy.list

import com.github.dataanon.model.DbConfig
import com.github.dataanon.model.Field
import com.github.dataanon.model.Record
import com.github.dataanon.strategy.AnonymizationStrategy
import com.github.dataanon.utils.RandomSampling

class PickFromDatabase<T : Any>(dbConfig: DbConfig, selectQuery: String) : AnonymizationStrategy<T>, RandomSampling {

    private val values = initializeValues(dbConfig, selectQuery)

    private fun initializeValues(dbConfig: DbConfig, selectQuery: String): List<T> {
        val conn   = dbConfig.connection()
        val stmt   = conn.createStatement()
        val result = stmt.executeQuery(selectQuery)

        val list = generateSequence {
            if (result.next()) result.getObject(1) as T else null
        }.toList()

        stmt.close()
        conn.close()

        require(list.isNotEmpty()) { "values cannot be empty while using PickFromDatabase" }
        return list
    }

    override fun anonymize(field: Field<T>, record: Record): T = sample(values)
}
