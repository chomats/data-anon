package com.github.dataanon.integration.blacklist

import com.github.dataanon.dsl.Blacklist
import com.github.dataanon.model.DbConfig
import com.github.dataanon.strategy.string.FixedString
import com.github.dataanon.support.MoviesTable
import io.kotlintest.specs.FunSpec
import org.awaitility.Awaitility.await
import java.sql.Date
import java.util.concurrent.TimeUnit
import java.util.regex.Pattern
import kotlin.test.assertEquals
import kotlin.test.assertTrue


class BlacklistMoviesSimplePrimaryKeyIntegrationTest : FunSpec() {

    init {
        test("should do blacklist anonymization for single record with simple primaryKey"){

            val (dbConfig, moviesTable) = prepareDataWithSingleMovie()
            val alphabeticPattern       = Pattern.compile("[a-zA-Z]+")

            Blacklist(dbConfig)
                    .table("MOVIES", listOf("MOVIE_ID")) {
                        anonymize("TITLE").using(FixedString("MY VALUE"))
                        anonymize("GENRE")
                    }.execute(progressBarEnabled = false)

            await().timeout(2, TimeUnit.SECONDS).until { moviesTable.findAll()[0]["TITLE"].toString().equals("MY VALUE") }
            val records = moviesTable.findAll()

            assertEquals(1,records.size)
            assertEquals(1, records[0]["MOVIE_ID"])
            assertEquals("MY VALUE", records[0]["TITLE"])
            assertEquals(Date(1999, 5, 2), records[0]["RELEASE_DATE"])
            assertTrue(alphabeticPattern.matcher(records[0]["GENRE"].toString()).matches())

            moviesTable.close()
        }

        test("should do blacklist anonymization for multiple records with simple primaryKey"){
            val (dbConfig, moviesTable) = prepareDataWith2Movies()
            val alphabeticPattern       = Pattern.compile("[a-zA-Z]+")

            Blacklist(dbConfig)
                    .table("MOVIES", listOf("MOVIE_ID")) {
                        anonymize("TITLE").using(FixedString("MY VALUE"))
                        anonymize("GENRE")
                    }.execute(progressBarEnabled = false)

            await().timeout(2, TimeUnit.SECONDS).until { moviesTable.findAll()[0]["TITLE"].toString().equals("MY VALUE") }
            val records = moviesTable.findAll()

            assertEquals(2,records.size)
            assertEquals(1, records[0]["MOVIE_ID"])
            assertEquals("MY VALUE", records[0]["TITLE"])
            assertTrue(alphabeticPattern.matcher(records[0]["GENRE"].toString()).matches())
            assertEquals(Date(1999, 5, 2), records[0]["RELEASE_DATE"])
            assertEquals(2, records[1]["MOVIE_ID"])
            assertEquals("MY VALUE", records[1]["TITLE"])
            assertTrue(alphabeticPattern.matcher(records[1]["GENRE"].toString()).matches())
            assertEquals(Date(2005, 5, 2), records[1]["RELEASE_DATE"])

            moviesTable.close()
        }
    }

    private fun prepareDataWith2Movies(): Pair<DbConfig, MoviesTable> {
        val dbConfig = DbConfig("jdbc:h2:mem:movies", "", "")
        val moviesTable = MoviesTable(dbConfig)
                .insert(1, "Movie 1", "Drama", Date(1999, 5, 2))
                .insert(2, "Movie 2", "Action", Date(2005, 5, 2))
        return Pair(dbConfig, moviesTable)
    }

    private fun prepareDataWithSingleMovie(): Pair<DbConfig, MoviesTable> {
        val dbConfig = DbConfig("jdbc:h2:mem:movies", "", "")
        val moviesTable = MoviesTable(dbConfig)
                .insert(1, "Movie 1", "Drama", Date(1999, 5, 2))
        return Pair(dbConfig, moviesTable)
    }
}