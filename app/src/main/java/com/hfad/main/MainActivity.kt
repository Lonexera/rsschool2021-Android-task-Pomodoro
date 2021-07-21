package com.hfad.main

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.recyclerview.widget.LinearLayoutManager
import com.hfad.main.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(), StopwatchListener, LifecycleObserver {

    private lateinit var binding: ActivityMainBinding
    private val stopwatches = mutableListOf<Stopwatch>()
    private val stopwatchAdapter = StopwatchAdapter(this)
    private var nextId = 0
    private var isAnyTimerOn: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ProcessLifecycleOwner.get().lifecycle.addObserver(this)

        binding.recycler.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = stopwatchAdapter
        }

        with (binding) {
            addButton.setOnClickListener {
                if (isInputValid()) {
                    inputMinutes.error = null
                    val msToTimer: Long = Integer
                        .parseInt(inputMinutes.text.toString())
                        .toLong() * 60 * 1000
                    stopwatches.add(Stopwatch(nextId++, msToTimer, msToTimer,0,false))
                    stopwatchAdapter.submitList(stopwatches.toList())
                }
                else {
                    inputMinutes.error = getString(R.string.input_error_text)
                }
            }

            with (binding) {
                inputMinutes.setOnKeyListener { _, _, _ ->
                    if (isInputValid()) {
                        // Clear the error.
                        inputMinutes.error = null
                    }
                    false
                }
            }
        }

    }

    override fun start(id: Int) {
        if (isAnyTimerOn) {
            val timerId: Int = findStartedTimerId()
            if (timerId != NOT_FOUND) {
                val msPassed = System.currentTimeMillis() - stopwatches[timerId].startTime
                stop(timerId, stopwatches[timerId].msLeft - msPassed)
            }
        }

        changeStopwatch(id, null, true)
        isAnyTimerOn = true
    }

    override fun stop(id: Int, msLeft: Long) {
        changeStopwatch(id, msLeft, false)
        isAnyTimerOn = false
    }

    override fun delete(id: Int) {
        stopwatches.remove(stopwatches.find { it.id == id })
        stopwatchAdapter.submitList(stopwatches.toList())
        stopwatchAdapter.notifyDataSetChanged()
    }

    private fun changeStopwatch(id: Int, msLeft: Long?, isStarted: Boolean) {

        stopwatches.forEach { stopwatch ->
            if (stopwatch.id == id) {
                stopwatch.msLeft = msLeft ?: stopwatch.msLeft
                stopwatch.isStarted = isStarted
            }
        }
        stopwatchAdapter.submitList(stopwatches)
        stopwatchAdapter.notifyDataSetChanged()
    }

    private fun isInputValid() : Boolean {
        val inputText = binding.inputMinutes.text.toString()
        return inputText.matches("""[0-9]{1,3}""".toRegex())
    }

    private fun findStartedTimerId() : Int {
        stopwatches.forEachIndexed { index, stopwatch ->
            if (stopwatch.isStarted) {
                return index
            }
        }

        return NOT_FOUND
    }



    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onAppBackgrounded() {
        if (isAnyTimerOn) {
            val startedTimerId = findStartedTimerId()

            println(stopwatches[startedTimerId].startTime)
            val startIntent = Intent(this, ForegroundService::class.java)
            startIntent.putExtra(COMMAND_ID, COMMAND_START)
            startIntent.putExtra(STARTED_TIMER_TIME_MS, stopwatches[startedTimerId].startTime)
            startIntent.putExtra(TIME_LEFT, stopwatches[startedTimerId].msLeft)
            startService(startIntent)
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onAppForegrounded() {

        val stopIntent = Intent(this, ForegroundService::class.java)
        stopIntent.putExtra(COMMAND_ID, COMMAND_STOP)
        startService(stopIntent)
    }
}