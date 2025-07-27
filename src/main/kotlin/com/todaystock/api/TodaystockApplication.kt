package com.todaystock.api

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class TodaystockApplication

fun main(args: Array<String>) {
    runApplication<TodaystockApplication>(*args)
}
