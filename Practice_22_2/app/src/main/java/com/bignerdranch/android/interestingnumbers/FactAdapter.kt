package com.bignerdranch.android.interestingnumbers

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

class FactAdapter : ListAdapter<Fact, FactAdapter.FactViewHolder>(DiffCallback()) {

    private var onDeleteClickListener: ((Fact) -> Unit)? = null
    private var onEditClickListener: ((Fact) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FactViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_fact, parent, false)
        return FactViewHolder(view)
    }

    override fun onBindViewHolder(holder: FactViewHolder, position: Int) {
        val fact = getItem(position)
        holder.bind(fact, onDeleteClickListener, onEditClickListener)
    }

    fun setOnDeleteClickListener(listener: (Fact) -> Unit) {
        onDeleteClickListener = listener
    }

    fun setOnEditClickListener(listener: (Fact) -> Unit) {
        onEditClickListener = listener
    }

    class FactViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val factTextView: TextView = itemView.findViewById(R.id.factText)
        private val deleteButton: Button = itemView.findViewById(R.id.deleteButton)
        private val editButton: Button = itemView.findViewById(R.id.editButton)

        fun bind(fact: Fact, onDeleteClickListener: ((Fact) -> Unit)?, onEditClickListener: ((Fact) -> Unit)?) {
            factTextView.text = "Факт: ${fact.text}"

            deleteButton.setOnClickListener {
                onDeleteClickListener?.invoke(fact)
            }

            editButton.setOnClickListener {
                onEditClickListener?.invoke(fact)
            }
        }
    }

    private class DiffCallback : DiffUtil.ItemCallback<Fact>() {
        override fun areItemsTheSame(oldItem: Fact, newItem: Fact): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Fact, newItem: Fact): Boolean {
            return oldItem == newItem
        }
    }

    fun updateFacts(facts: List<Fact>) {
        submitList(facts)
    }
}