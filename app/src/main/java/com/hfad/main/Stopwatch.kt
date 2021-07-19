package com.hfad.main

data class Stopwatch(
    val id: Int,
    var currentMs: Long,
    val wholeMs: Long,
    var isStarted: Boolean
)
