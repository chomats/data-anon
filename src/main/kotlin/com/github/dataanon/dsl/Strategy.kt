package com.github.dataanon.dsl

import com.github.dataanon.jdbc.TableReader
import com.github.dataanon.jdbc.TableWriter
import com.github.dataanon.model.DbConfig
import com.github.dataanon.model.Table
import com.github.dataanon.utils.ProgressBarGenerator
import reactor.core.publisher.Flux
import reactor.core.scheduler.Schedulers
import java.util.concurrent.CountDownLatch
import java.util.logging.Level
import java.util.logging.LogManager
import java.util.logging.Logger


abstract class Strategy {
    private val logger = Logger.getLogger(Strategy::class.java.name)
    protected val tables = mutableListOf<Table>()

    @JvmOverloads
    fun execute(progressBarEnabled: Boolean = true) {
        val inputStream = Strategy::class.java.classLoader.getResourceAsStream("logging.properties")
        LogManager.getLogManager().readConfiguration(inputStream)
        val latch = CountDownLatch(tables.size)

        Flux.fromIterable(tables)
                .parallel(tables.size)
                .runOn(Schedulers.parallel())
                .subscribe { table -> executeOnTable(table, progressBarEnabled, latch) }

        latch.await()
    }

    private fun executeOnTable(table: Table, progressBarEnabled: Boolean, latch: CountDownLatch) {
        try {
            val reader = TableReader(sourceDbConfig(), table)
            val progressBar = ProgressBarGenerator(progressBarEnabled, table.name) { reader.totalNoOfRecords() }
            val writer = TableWriter(destDbConfig(), table, progressBar)

            Flux.fromIterable(Iterable { reader }).map { table.execute(it) }.subscribe(writer)
        } catch (t: Throwable) {
            logger.log(Level.SEVERE,"Error processing table '${table.name}': ${t.message}",t)
        } finally {
            latch.countDown()
        }
    }

    protected abstract fun sourceDbConfig(): DbConfig
    protected abstract fun destDbConfig():   DbConfig
}
