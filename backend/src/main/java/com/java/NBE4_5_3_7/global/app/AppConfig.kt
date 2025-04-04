package com.java.NBE4_5_3_7.global.app

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Configuration

@Configuration
class AppConfig {

    @Autowired
    fun setObjectMapper(objectMapper: ObjectMapper) {
        Companion.objectMapper = objectMapper
    }

    companion object {
        private lateinit var objectMapper: ObjectMapper

        @JvmStatic
        fun getObjectMapper(): ObjectMapper = objectMapper

        @JvmStatic
        val isNotProd: Boolean
            get() = true

        @JvmStatic
        val siteFrontUrl: String
            get() = "http://localhost:3000"
    }
}