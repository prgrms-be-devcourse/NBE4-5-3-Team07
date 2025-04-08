package com.java.NBE4_5_3_7.global.app

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration

@Configuration
class AppConfig (
    @Value("\${front.url}")
    val siteFrontUrl: String
){

    init {
        _siteFrontUrl = siteFrontUrl
    }

    @Autowired
    fun setObjectMapper(objectMapper: ObjectMapper) {
        Companion.objectMapper = objectMapper
    }

    companion object {
        private lateinit var objectMapper: ObjectMapper
        private lateinit var _siteFrontUrl: String

        @JvmStatic
        fun getObjectMapper(): ObjectMapper = objectMapper

        @JvmStatic
        val isNotProd: Boolean
            get() = true

        @JvmStatic
        val SITE_FRONT_URL: String
            get() = _siteFrontUrl
    }
}