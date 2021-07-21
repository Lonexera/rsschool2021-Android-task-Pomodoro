package com.hfad.main

data class Stopwatch(
    val id: Int,
    var msLeft: Long,
    val wholeMs: Long,
    var startTime: Long,
    var isStarted: Boolean
)
