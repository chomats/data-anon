package com.github.dataanon.utils

import com.github.dataanon.model.NullValue
import com.github.dataanon.strategy.AnonymizationStrategy
import com.github.dataanon.strategy.CopyAnonymizationStrategy
import com.github.dataanon.strategy.bool.RandomBooleanTrueFalse
import com.github.dataanon.strategy.number.RandomDouble
import com.github.dataanon.strategy.number.RandomFloat
import com.github.dataanon.strategy.number.RandomInt
import com.github.dataanon.strategy.string.RandomAlphabetic
import kotlin.reflect.KClass

object DefaultAnonymizationStrategies {

    private val defaultStrategies = mutableMapOf<KClass<*>, AnonymizationStrategy<*>>()

    init {
        defaultStrategies[String::class] = RandomAlphabetic()
        defaultStrategies[Boolean::class] = RandomBooleanTrueFalse()
        defaultStrategies[Int::class] = RandomInt()
        defaultStrategies[Float::class] = RandomFloat()
        defaultStrategies[Double::class] = RandomDouble()
        defaultStrategies[NullValue::class] = CopyAnonymizationStrategy<NullValue>()
    }

    fun getAnonymizationStrategy(kClass: KClass<*>) = if (defaultStrategies[kClass] != null) defaultStrategies[kClass]
                                                      else CopyAnonymizationStrategy<Any>()
}
