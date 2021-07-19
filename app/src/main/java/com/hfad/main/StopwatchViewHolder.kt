package com.hfad.main

import android.content.res.Resources
import android.graphics.drawable.AnimationDrawable
import android.os.CountDownTimer
import androidx.core.view.isInvisible
import androidx.recyclerview.widget.RecyclerView
import com.hfad.main.databinding.TimerLayoutBinding

class StopwatchViewHolder(private val binding: TimerLayoutBinding,
                          private val listener: StopwatchListener,
                          private val resources: Resources
) :
    RecyclerView.ViewHolder(binding.root) {

    private var timer: CountDownTimer? = null
    private var startTime = 0L

    fun bind(stopwatch: Stopwatch) {
        binding.stopwatchTimer.text = stopwatch.currentMs.displayTime()
        binding.customView.setWholeMs(stopwatch.wholeMs)
        binding.customView.setCurrent(stopwatch.currentMs)

        if (stopwatch.isStarted)
            startTimer(stopwatch)
        else
            stopTimer(stopwatch)

        initButtonsListeners(stopwatch)
    }

    private fun startTimer(stopwatch: Stopwatch) {
        binding.startStopButton.setText(R.string.stop_button_text)
        timer?.cancel()
        timer = getCountDownTimer(stopwatch)
        timer?.start()

        binding.blinkingIndicator.isInvisible = false
        (binding.blinkingIndicator.background as? AnimationDrawable)?.start()
    }

    private fun stopTimer(stopwatch: Stopwatch) {
        binding.startStopButton.setText(R.string.start_button_text)

        timer?.cancel()

        binding.blinkingIndicator.isInvisible = true
        (binding.blinkingIndicator.background as? AnimationDrawable)?.stop()
    }

    private fun initButtonsListeners(stopwatch: Stopwatch) {
        binding.startStopButton.setOnClickListener {
            if (stopwatch.isStarted) {
                listener.stop(stopwatch.id, stopwatch.currentMs)
            } else {
                listener.start(stopwatch.id)
            }
        }

        binding.deleteButton.setOnClickListener { listener.delete(stopwatch.id) }
    }

    private fun getCountDownTimer(stopwatch: Stopwatch) : CountDownTimer {
        return object : CountDownTimer(PERIOD, INTERVAL_MS) {

            val interval = INTERVAL_MS

            override fun onTick(millisUntilFinished: Long) {
                stopwatch.currentMs -= interval
                binding.stopwatchTimer.text = stopwatch.currentMs.displayTime()

                binding.customView.setCurrent(stopwatch.currentMs)
            }

            override fun onFinish() {
                binding.stopwatchTimer.text = stopwatch.currentMs.displayTime()

                binding.customView.setCurrent(stopwatch.currentMs)
            }

        }
    }

    private fun Long.displayTime(): String {
        if (this <= 0L) {
            return START_TIME
        }
        val h = this / 1000 / 3600
        val m = this / 1000 % 3600 / 60
        val s = this / 1000 % 60

        return "${displaySlot(h)}:${displaySlot(m)}:${displaySlot(s)}"
    }

    private fun displaySlot(count: Long): String {
        return if (count / 10L > 0) {
            "$count"
        } else {
            "0$count"
        }
    }

    private companion object {

        private const val START_TIME = "00:00:00"
        private const val INTERVAL_MS = 500L
        private const val PERIOD  = 1000L * 60L * 60L * 24L // Day

    }

}