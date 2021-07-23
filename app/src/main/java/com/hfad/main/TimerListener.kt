package com.hfad.main

interface TimerListener {

    fun start(id: Int)
    fun stop(id: Int, msLeft: Long)
    fun delete(id: Int)
}