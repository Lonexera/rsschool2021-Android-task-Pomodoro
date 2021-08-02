package com.hfad.main

import android.content.res.Resources
import android.graphics.Color
import android.graphics.drawable.AnimationDrawable
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.lifecycle.*
import androidx.recyclerview.widget.RecyclerView
import com.hfad.main.databinding.TimerLayoutBinding
import kotlinx.coroutines.*

class TimerViewHolder(private val binding: TimerLayoutBinding,
                      private val listener: TimerListener,
                      private val resources: Resources
) :
    RecyclerView.ViewHolder(binding.root), LifecycleObserver {

    private var job: Job? = null

    fun bind(timer: Timer) {
        binding.stopwatchTimer.text = timer.msLeft.displayTime()
        binding.pieView.setWholeMs(timer.wholeMs)
        binding.pieView.setCurrent(timer.msLeft)

        if (timer.msLeft <= 0L) {
            disableView(timer.wholeMs)
        } else {
            enableView()
        }

        if (timer.isStarted)
            startTimer(timer)
        else
            stopTimer()

        initButtonsListeners(timer)
    }

    private fun startTimer(timer: Timer) {
        binding.startStopButton.setText(R.string.stop_button_text)

        job?.cancel()

        continueTimer(timer)

        binding.blinkingIndicator.isInvisible = false
        (binding.blinkingIndicator.background as? AnimationDrawable)?.start()
    }

    private fun stopTimer() {
        binding.startStopButton.setText(R.string.start_button_text)

        job?.cancel()

        binding.blinkingIndicator.isInvisible = true
        (binding.blinkingIndicator.background as? AnimationDrawable)?.stop()
    }

    private fun initButtonsListeners(timer: Timer) {
        binding.startStopButton.setOnClickListener {
            if (timer.isStarted) {
                timer.msLeft -= (System.currentTimeMillis() - timer.startTime)
                listener.stop(timer.id, timer.msLeft)
            } else {
                timer.startTime = System.currentTimeMillis()
                listener.start(timer.id)
            }
        }

        binding.deleteButton.setOnClickListener {
            stopTimer()
            listener.delete(timer.id)
        }
    }


    private fun continueTimer(timer: Timer)  {
        job = GlobalScope.launch(Dispatchers.Main) {
            while (true) {
                val timeLeft = timer.msLeft -
                        (System.currentTimeMillis() - timer.startTime)

                binding.stopwatchTimer.text = timeLeft.displayTime()
                binding.pieView.setCurrent(timeLeft)

                if (timeLeft <= 0) {
                    disableView(timer.wholeMs)
                    listener.stop(timer.id, timeLeft)
                    stopTimer()
                }

                delay(INTERVAL_MS)
            }
        }
    }

    private fun disableView(wholeMs: Long) {
        with(binding) {
            stopwatchTimer.text = wholeMs.displayTime()

            cardView.setBackgroundColor(Color.RED)
            stopwatchTimer.setTextColor(Color.WHITE)

            pieView.changeColor(Color.TRANSPARENT)
            startStopButton.isEnabled = false
            deleteButton.setColorFilter(Color.WHITE)
            deleteButton.setBackgroundColor(Color.RED)
        }
    }

    private fun enableView() {
        with(binding) {
            cardView.setBackgroundColor(Color.WHITE)
            stopwatchTimer.setTextColor(Color.GRAY)

            startStopButton.isEnabled = true
            deleteButton.setColorFilter(Color.RED)
            deleteButton.setBackgroundColor(Color.WHITE)
        }
    }


    private companion object {
        private const val INTERVAL_MS = 500L
    }

}