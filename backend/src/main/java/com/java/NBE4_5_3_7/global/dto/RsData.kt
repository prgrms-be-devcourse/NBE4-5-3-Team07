package com.java.NBE4_5_3_7.global.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
data class RsData<T>(
    val code: String,
    val msg: String,
    val data: T
) {
    constructor(code: String, msg: String) : this(code, msg, Empty() as T)

    @get:JsonIgnore
    val statusCode: Int
        get() = code.split("-")[0].toInt()
}