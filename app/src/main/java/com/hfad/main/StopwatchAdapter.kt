package com.hfad.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.hfad.main.databinding.TimerLayoutBinding

class StopwatchAdapter(private val listener: StopwatchListener)
    : ListAdapter<Stopwatch, StopwatchViewHolder>(itemComparator) {

    private companion object {

        private val itemComparator = object : DiffUtil.ItemCallback<Stopwatch>() {

            override fun areItemsTheSame(oldItem: Stopwatch, newItem: Stopwatch): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Stopwatch, newItem: Stopwatch): Boolean {
                return oldItem.msLeft == newItem.msLeft &&
                        oldItem.isStarted == newItem.isStarted
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StopwatchViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = TimerLayoutBinding.inflate(layoutInflater, parent, false)


        return StopwatchViewHolder(binding,  listener, binding.root.context.resources)
    }

    override fun onBindViewHolder(holder: StopwatchViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}