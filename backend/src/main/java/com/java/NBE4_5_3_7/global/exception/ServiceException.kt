package com.java.NBE4_5_3_7.global.exception

import com.java.NBE4_5_3_7.global.dto.RsData

class ServiceException(
    code: String,
    message: String
) : RuntimeException(message) {

    private val rsData: RsData<Any?> = RsData(code, message)

    fun getCode(): String = rsData.code
    fun getMsg(): String = rsData.msg
    fun getStatusCode(): Int = rsData.statusCode
}
