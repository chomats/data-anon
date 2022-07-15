package com.github.dataanon.strategy.email

import com.github.dataanon.model.Field
import com.github.dataanon.model.Record
import com.github.dataanon.strategy.AnonymizationStrategy
import com.github.dataanon.strategy.list.PickFromFile
import com.github.dataanon.strategy.name.RandomFirstName

class RandomEmail(sourceFilePath: String = "/data/first_names.dat",
                  private val host: String = "data-anonymization", private val tld: String = "com") : AnonymizationStrategy<String> {

    init {
        require(host.isNotBlank()) { "host can not be empty while using RandomEmail" }
        require(tld.isNotBlank()) { "tld can not be empty while using RandomEmail" }
    }

    private val pickFromFile = PickFromFile<String>(filePath = sourceFilePath)

    override  fun anonymize(field: Field<String>, record: Record): String = "${pickFromFile.anonymize(field, record)}${record.rowNum}@$host.$tld"
}
