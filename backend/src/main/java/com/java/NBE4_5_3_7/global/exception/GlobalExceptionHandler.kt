package com.java.NBE4_5_3_7.global.exception

import com.java.NBE4_5_3_7.global.app.AppConfig
import com.java.NBE4_5_3_7.global.dto.RsData
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.AccessDeniedException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleMethodArgumentNotValidException(e: MethodArgumentNotValidException): ResponseEntity<RsData<Void>> {
        val message = e.bindingResult.fieldErrors
            .joinToString("\n") { "${it.field} : ${it.code} : ${it.defaultMessage}" }
            .lines().sorted().joinToString("\n")

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(RsData("400-1", message))
    }

    @ExceptionHandler(ServiceException::class)
    @ResponseStatus
    fun handleServiceException(ex: ServiceException): ResponseEntity<RsData<Void>> {
        if (AppConfig.isNotProd) ex.printStackTrace()

        return ResponseEntity.status(ex.getStatusCode())
            .body(RsData(ex.getCode(), ex.getMsg()))
    }

    @ExceptionHandler(AccessDeniedException::class)
    fun handleAccessDeniedException(ex: AccessDeniedException): ResponseEntity<String> {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ex.message)
    }
}
