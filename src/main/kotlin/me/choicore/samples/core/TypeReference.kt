package me.choicore.samples.core

import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

abstract class TypeReference<T> protected constructor() : Comparable<TypeReference<T>> {
    val type: Type

    init {
        val superClass: Type = javaClass.genericSuperclass
        require(superClass !is Class<*>) { "Internal error: TypeReference constructed without actual type information" }
        type = (superClass as ParameterizedType).actualTypeArguments[0]
    }

    override fun compareTo(other: TypeReference<T>): Int = 0
}
