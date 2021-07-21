package com.hfad.main

interface StopwatchListener {

    fun start(id: Int)
    fun stop(id: Int, msLeft: Long)
    fun delete(id: Int)
}