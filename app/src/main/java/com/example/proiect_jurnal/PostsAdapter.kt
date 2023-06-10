package com.example.proiect_jurnal

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import java.util.*
import kotlin.collections.ArrayList
import android.content.Intent
import androidx.core.content.ContextCompat.startActivity

class PostsAdapter(private val posts: List<Post>) : RecyclerView.Adapter<PostsAdapter.ViewHolder>(),
    Filterable {
    var postFilterList = ArrayList<Post>()
    init {
        postFilterList = posts as ArrayList<Post>
    }
    override fun getItemCount() = postFilterList.size
    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charSearch = constraint.toString()
                if (charSearch.isEmpty()) {
                    postFilterList = posts as ArrayList<Post>
                } else {
                    val resultList = ArrayList<Post>()
                    for (row in posts) {
                        if (row.title?.lowercase(Locale.ROOT)!!.contains(charSearch.lowercase(Locale.ROOT))
                            || row.content?.lowercase(Locale.ROOT)!!.contains(charSearch.lowercase(Locale.ROOT))
                            ) {
                            resultList.add(row)
                        }
                    }
                    postFilterList = resultList
                }
                val filterResults = FilterResults()
                filterResults.values = postFilterList
                return filterResults
            }

            @Suppress("UNCHECKED_CAST")
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                postFilterList = results?.values as ArrayList<Post>
                notifyDataSetChanged()
            }
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_text_post, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val textPost = postFilterList.get(position)
        // Bind the data to the views in the item layout
        holder.titleTextView.text = textPost.title
        holder.contentTextView.text = textPost.content
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnLongClickListener {
        //TODO: CRASH
        init {
            itemView.setOnLongClickListener(this)
        }
        val titleTextView: TextView = itemView.findViewById(R.id.titleTextView)
        val contentTextView: TextView = itemView.findViewById(R.id.contentTextView)
        override fun onLongClick(view: View?): Boolean {
            val titleTextView = itemView.findViewById<TextView>(R.id.titleTextView)
            val contentTextView = itemView.findViewById<TextView>(R.id.contentTextView)
            val titleText = titleTextView.text.toString()
            val contentText = contentTextView.text.toString()
            val shareText = "$titleText\n\n$contentText"

            val sendIntent: Intent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, shareText)
                type = "text/plain"
            }
            val shareIntent = Intent.createChooser(sendIntent, null)
            itemView.context.startActivity(shareIntent)
            return true
        }

    }

    private class DiffCallback : DiffUtil.ItemCallback<Post>() {
        override fun areItemsTheSame(oldItem: Post, newItem: Post) = oldItem.postId == newItem.postId
        override fun areContentsTheSame(oldItem: Post, newItem: Post) = oldItem == newItem
    }
}
