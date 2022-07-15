package com.github.dataanon.strategy.list

import com.github.dataanon.Matchers
import com.github.dataanon.model.Field
import com.github.dataanon.model.Record
import io.kotlintest.should
import io.kotlintest.shouldThrow
import io.kotlintest.specs.FunSpec

class PickFromListUnitTest : FunSpec(), Matchers {

    val emptyRecord: Record = Record(listOf(), 0)

    init {
        test("should return a random value from list") {
            val countries = listOf("India", "US", "UK")
            val field     = Field("country", "India")
            val strategy  = PickFromList(countries)

            val anonymized = strategy.anonymize(field, emptyRecord)

            anonymized should beIn(countries)
        }

        test("should throw IllegalArgumentException given empty list") {
            val countries = emptyList<String>()
            shouldThrow<IllegalArgumentException> {
                PickFromList(countries)
            }
        }
    }
}
