package com.java.NBE4_5_3_7;

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaAuditing
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@EnableJpaAuditing
@EnableScheduling
class Nbe4537Application

fun main(args: Array<String>) {
	runApplication<Nbe4537Application>(*args)
}