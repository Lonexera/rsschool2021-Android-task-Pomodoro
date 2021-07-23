package com.hfad.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.hfad.main.databinding.TimerLayoutBinding

class TimerAdapter(private val listener: TimerListener)
    : ListAdapter<Timer, TimerViewHolder>(itemComparator) {

    private companion object {

        private val itemComparator = object : DiffUtil.ItemCallback<Timer>() {

            override fun areItemsTheSame(oldItem: Timer, newItem: Timer): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Timer, newItem: Timer): Boolean {
                return oldItem.msLeft == newItem.msLeft &&
                        oldItem.isStarted == newItem.isStarted
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimerViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = TimerLayoutBinding.inflate(layoutInflater, parent, false)


        return TimerViewHolder(binding,  listener, binding.root.context.resources)
    }

    override fun onBindViewHolder(holder: TimerViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}