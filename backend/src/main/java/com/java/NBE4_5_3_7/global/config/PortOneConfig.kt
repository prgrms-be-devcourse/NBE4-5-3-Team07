package com.java.NBE4_5_3_7.global.config

import com.siot.IamportRestClient.IamportClient
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class PortOneConfig {
    @Value("\${imp.key}")
    private val apiKey: String? = null

    @Value("\${imp.secret_key}")
    private val secretKey: String? = null

    @Bean
    fun iamportClient(): IamportClient {
        return IamportClient(apiKey, secretKey)
    }
}