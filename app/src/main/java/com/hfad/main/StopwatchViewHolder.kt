package com.hfad.main

import android.content.res.Resources
import android.graphics.drawable.AnimationDrawable
import android.util.Log
import androidx.core.view.isInvisible
import androidx.lifecycle.*
import androidx.recyclerview.widget.RecyclerView
import com.hfad.main.databinding.TimerLayoutBinding
import kotlinx.coroutines.*

class StopwatchViewHolder(private val binding: TimerLayoutBinding,
                          private val listener: StopwatchListener,
                          private val resources: Resources
) :
    RecyclerView.ViewHolder(binding.root), LifecycleObserver {

    private var job: Job? = null

    fun bind(stopwatch: Stopwatch) {
        binding.stopwatchTimer.text = stopwatch.msLeft.displayTime()
        binding.customView.setWholeMs(stopwatch.wholeMs)
        binding.customView.setCurrent(stopwatch.msLeft)

        if (stopwatch.isStarted)
            startTimer(stopwatch)
        else
            stopTimer(stopwatch)

        initButtonsListeners(stopwatch)
    }

    private fun startTimer(stopwatch: Stopwatch) {
        binding.startStopButton.setText(R.string.stop_button_text)

        job?.cancel()

        continueTimer(stopwatch)

        binding.blinkingIndicator.isInvisible = false
        (binding.blinkingIndicator.background as? AnimationDrawable)?.start()
    }

    private fun stopTimer(stopwatch: Stopwatch) {
        binding.startStopButton.setText(R.string.start_button_text)

        try {
            Log.e("EXCEPTION", "Job cancellation should be here")
/*            if (job == null)
                Log.e("EXCEPTION", "Job is null")*/
            job?.cancel()
        } catch (e:Exception) {
            Log.e("EXCEPTION", "Job cancellation failed")
        }

        binding.blinkingIndicator.isInvisible = true
        (binding.blinkingIndicator.background as? AnimationDrawable)?.stop()
    }

    private fun initButtonsListeners(stopwatch: Stopwatch) {
        binding.startStopButton.setOnClickListener {
            if (stopwatch.isStarted) {
                stopwatch.msLeft -= (System.currentTimeMillis() - stopwatch.startTime)
                listener.stop(stopwatch.id, stopwatch.msLeft)
            } else {
                stopwatch.startTime = System.currentTimeMillis()
                listener.start(stopwatch.id)
            }
        }

        binding.deleteButton.setOnClickListener {
            stopTimer(stopwatch)
            listener.delete(stopwatch.id)
        }
    }


    private fun continueTimer(stopwatch: Stopwatch)  {
        job = GlobalScope.launch(Dispatchers.Main) {
            while (true) {
                Log.i("TAG", "Coroutine is on.")
                val timePassed = stopwatch.msLeft -
                        (System.currentTimeMillis() - stopwatch.startTime)

                binding.stopwatchTimer.text = timePassed.displayTime()
                binding.customView.setCurrent(timePassed)

                delay(INTERVAL_MS)
            }
        }
    }

    private companion object {
        private const val INTERVAL_MS = 500L
        private const val PERIOD  = 1000L * 60L * 60L * 24L // Day

    }

}