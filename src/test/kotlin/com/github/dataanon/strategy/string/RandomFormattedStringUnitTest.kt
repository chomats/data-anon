package com.github.dataanon.strategy.string

import com.github.dataanon.Matchers
import com.github.dataanon.model.Field
import com.github.dataanon.model.Record
import io.kotlintest.should
import io.kotlintest.shouldBe
import io.kotlintest.specs.FunSpec
import java.util.regex.Pattern

class RandomFormattedStringUnitTest : FunSpec(), Matchers {

    val emptyRecord: Record = Record(listOf(), 0)

    init {
        test("should return a formatted string with length based on field value replacing numeric digit with a random number"){
            val randomFormattedString = RandomFormattedString()
            val field                 = Field("card", "9090-1232-0876-3409")

            val anonymized = randomFormattedString.anonymize(field, emptyRecord)
            anonymized.length shouldBe 19
        }

        test("should return a formatted string replacing numeric digit with a random number"){
            val randomFormattedString = RandomFormattedString()
            val field                 = Field("card", "9090-1232-0876-3409")

            val anonymized = randomFormattedString.anonymize(field, emptyRecord)
            anonymized should notMatches(Regex.fromLiteral("[a-zA-Z]+"))
        }

        test("should return a formatted string with length based on field value replacing character with a random character"){
            val randomFormattedString = RandomFormattedString()
            val field                 = Field("card", "ab-pp-QA")

            val anonymized = randomFormattedString.anonymize(field, emptyRecord)
            anonymized.length shouldBe 8
        }

        test("should return a formatted string replacing character with a random character"){
            val randomFormattedString = RandomFormattedString()
            val field                 = Field("card", "ab-pp-QA")

            val anonymized = randomFormattedString.anonymize(field, emptyRecord)
            anonymized should notMatches(Regex.fromLiteral("[0-9]+"))
        }

        test("should return a formatted string with length based on field value replacing character with a random character and a digit with a random digit"){
            val randomFormattedString = RandomFormattedString()
            val field                 = Field("card", "PQAST9812-F")

            val anonymized = randomFormattedString.anonymize(field, emptyRecord)

            anonymized.length shouldBe 11
        }

        test("should return a formatted string replacing character with a random character and a digit with a random digit"){
            val randomFormattedString = RandomFormattedString()
            val field                 = Field("card", "PQAST9812-F")

            val anonymized = randomFormattedString.anonymize(field, emptyRecord)

            val matcher    = Pattern.compile("([A-Z]{5})(\\d{4})-([A-Z])").matcher(anonymized)
            matcher.matches() shouldBe true
        }
    }
}
