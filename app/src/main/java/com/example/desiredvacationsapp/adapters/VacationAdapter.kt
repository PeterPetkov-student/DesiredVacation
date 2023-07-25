package com.example.desiredvacationsapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.desiredvacationsapp.databinding.VacationListVacationBinding
import com.example.desiredvacationsapp.models.Vacation
import java.io.File

class VacationAdapter(private val onItemClicked: (Vacation) -> Unit) :
    ListAdapter<Vacation, VacationAdapter.VacationViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VacationViewHolder {
        return VacationViewHolder(
            VacationListVacationBinding.inflate(
                LayoutInflater.from(
                    parent.context
                )
            )
        )
    }

    override fun onBindViewHolder(holder: VacationViewHolder, position: Int) {
        val current = getItem(position)
        holder.itemView.setOnClickListener {
            onItemClicked(current)
        }
        holder.bind(current)
    }

    class VacationViewHolder(private var binding: VacationListVacationBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Vacation) {
            binding.name.text = item.name
            binding.vacationLocation.text = item.location

            Glide.with(binding.root)
                .load(item.imageName?.let { File(it) }) // loading from a local file
                .into(binding.imageName)  // Assuming there's an ImageView with id vacationImage in your layout

        }
    }

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<Vacation>() {
            override fun areItemsTheSame(oldItem: Vacation, newItem: Vacation): Boolean {
                return oldItem === newItem
            }

            override fun areContentsTheSame(oldItem: Vacation, newItem: Vacation): Boolean {
                return oldItem.name == newItem.name
            }
        }
    }
}