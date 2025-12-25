package com.teacherevan.reposync.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.teacherevan.reposync.R
import com.teacherevan.reposync.data.SyncRecord
import java.text.SimpleDateFormat
import java.util.*

/**
 * Adapter for displaying sync history
 */
class SyncHistoryAdapter : ListAdapter<SyncRecord, SyncHistoryAdapter.ViewHolder>(DiffCallback()) {
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_sync_history, parent, false)
        return ViewHolder(view)
    }
    
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
    
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val githubUrlText: TextView = itemView.findViewById(R.id.githubUrlText)
        private val hfRepoIdText: TextView = itemView.findViewById(R.id.hfRepoIdText)
        private val statusText: TextView = itemView.findViewById(R.id.statusText)
        private val timestampText: TextView = itemView.findViewById(R.id.timestampText)
        
        fun bind(record: SyncRecord) {
            githubUrlText.text = record.githubUrl
            hfRepoIdText.text = "→ ${record.hfRepoId} (${record.repoType})"
            statusText.text = record.status.name
            
            val dateFormat = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault())
            timestampText.text = dateFormat.format(Date(record.startTime))
            
            // Color code status
            val color = when (record.status) {
                SyncRecord.SyncStatus.COMPLETED -> android.graphics.Color.GREEN
                SyncRecord.SyncStatus.FAILED -> android.graphics.Color.RED
                SyncRecord.SyncStatus.IN_PROGRESS -> android.graphics.Color.BLUE
                SyncRecord.SyncStatus.PENDING -> android.graphics.Color.GRAY
            }
            statusText.setTextColor(color)
        }
    }
    
    class DiffCallback : DiffUtil.ItemCallback<SyncRecord>() {
        override fun areItemsTheSame(oldItem: SyncRecord, newItem: SyncRecord): Boolean {
            return oldItem.id == newItem.id
        }
        
        override fun areContentsTheSame(oldItem: SyncRecord, newItem: SyncRecord): Boolean {
            return oldItem == newItem
        }
    }
}
