package com.java.NBE4_5_3_7.standard;

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import java.util.*
import javax.crypto.SecretKey

class Ut {

    object Jwt {

        @JvmStatic
        fun createToken(keyString: String, expireSeconds: Int, claims: Map<String, Any>): String {
            val secretKey: SecretKey = Keys.hmacShaKeyFor(keyString.toByteArray())
            val issuedAt = Date()
            val expiration = Date(issuedAt.time + 1000L * expireSeconds)

            return Jwts.builder()
                .claims(claims)
                .issuedAt(issuedAt)
                .expiration(expiration)
                .signWith(secretKey)
                .compact()
        }

        @JvmStatic
        fun isValidToken(keyString: String, token: String?): Boolean {
            try {
                val secretKey = Keys.hmacShaKeyFor(keyString.toByteArray())

                Jwts
                    .parser()
                    .verifyWith(secretKey)
                    .build()
                    .parse(token)
            } catch (e: Exception) {
                e.printStackTrace()
                return false
            }

            return true
        }

        @JvmStatic
        fun getPayload(keyString: String, jwtStr: String?): Map<String, Any> {
            val secretKey = Keys.hmacShaKeyFor(keyString.toByteArray())

            return Jwts
                .parser()
                .verifyWith(secretKey)
                .build()
                .parse(jwtStr)
                .payload as Map<String, Any>
        }
    }
}
