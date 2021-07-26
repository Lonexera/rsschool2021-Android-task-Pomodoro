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
import java.util.ArrayList

class MainActivity : AppCompatActivity(), TimerListener, LifecycleObserver {

    private lateinit var binding: ActivityMainBinding
    private val timers = mutableListOf<Timer>()
    private val timerAdapter = TimerAdapter(this)
    private var nextId = 0
    private var isAnyTimerOn: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ProcessLifecycleOwner.get().lifecycle.addObserver(this)

        binding.recycler.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = timerAdapter
        }

        with (binding) {
            addButton.setOnClickListener {
                if (isInputValid()) {
                    inputMinutes.error = null
                    val msToTimer: Long = Integer
                        .parseInt(inputMinutes.text.toString())
                        .toLong() * 60 * 1000
                    timers.add(Timer(nextId++, msToTimer, msToTimer,0,false))
                    timerAdapter.submitList(timers.toList())
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

        if (savedInstanceState != null) {
            nextId = savedInstanceState.getInt(NEXT_ID)
            isAnyTimerOn = savedInstanceState.getBoolean(IS_ANY_TIMER_ON)
            val timersArray = savedInstanceState.getParcelableArrayList<Timer>(TIMERS_ARRAY)
            if (timersArray != null) {
                timers.clear()
                timers.addAll(timersArray)
                timerAdapter.submitList(timers.toList())
            }
        }

    }

    override fun onDestroy() {
        onAppForegrounded()
        super.onDestroy()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.putInt(NEXT_ID, nextId)
        outState.putBoolean(IS_ANY_TIMER_ON, isAnyTimerOn)

        outState.putParcelableArrayList(TIMERS_ARRAY, timers as ArrayList<Timer>)
    }

    override fun start(id: Int) {
        if (isAnyTimerOn) {
            val startedTimer = timers.find { it.isStarted }
            if (startedTimer != null) {
                val msPassed = System.currentTimeMillis() - startedTimer.startTime
                stop(startedTimer.id, startedTimer.msLeft - msPassed)
            } else
                isAnyTimerOn = false
        }

        changeTimer(id, null, true)
        isAnyTimerOn = true
    }

    override fun stop(id: Int, msLeft: Long) {
        changeTimer(id, msLeft, false)
        isAnyTimerOn = false
    }

    override fun delete(id: Int) {
        timers.remove(timers.find { it.id == id })
        timerAdapter.submitList(timers.toList())
    }

    private fun changeTimer(id: Int, msLeft: Long?, isStarted: Boolean) {

        timers.forEach { timer ->
            if (timer.id == id) {
                timer.msLeft = msLeft ?: timer.msLeft
                timer.isStarted = isStarted
            }
        }
        timerAdapter.submitList(timers.toList())
        timerAdapter.notifyDataSetChanged()
    }

    private fun isInputValid() : Boolean {
        val inputText = binding.inputMinutes.text.toString()
        return inputText.matches("""[0-9]{1,4}""".toRegex())
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onAppBackgrounded() {
        if (isAnyTimerOn) {
            val startedTimer = timers.find { it.isStarted }

            if (startedTimer != null) {
                val startIntent = Intent(this, ForegroundService::class.java)
                startIntent.putExtra(COMMAND_ID, COMMAND_START)
                startIntent.putExtra(STARTED_TIMER_TIME_MS, startedTimer.startTime)
                startIntent.putExtra(TIME_LEFT, startedTimer.msLeft)

                startService(startIntent)
            }
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onAppForegrounded() {
        
        val stopIntent = Intent(this, ForegroundService::class.java)
        stopIntent.putExtra(COMMAND_ID, COMMAND_STOP)
        startService(stopIntent)
    }

    companion object {
        private const val NEXT_ID = "NEXT ID"
        private const val IS_ANY_TIMER_ON = "IS ANY TIMER ON"
        private const val TIMERS_ARRAY = "TIMERS ARRAY"
    }
}