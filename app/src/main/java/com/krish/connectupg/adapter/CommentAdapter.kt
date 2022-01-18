package com.krish.connectupg.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.krish.connectupg.R
import com.krish.connectupg.model.Comment
import com.krish.connectupg.ui.detailFragment.DetailFragment
import com.krish.connectupg.utils.PostsDiffUtil

private const val TAG = "DetailFragment"
class CommentAdapter() :
    RecyclerView.Adapter<CommentAdapter.CommentViewHolder>() {

    private var comments = emptyList<Comment>()

    inner class CommentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val username: TextView = itemView.findViewById(R.id.username)
        val comment: TextView = itemView.findViewById(R.id.tvComment)
     }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.comment_row_layout, parent, false)
        return CommentViewHolder(view)
    }

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        holder.apply {
            val currentComment = comments[position]
            username.text = currentComment.username
            comment.text = currentComment.content
            Log.d(TAG, "$position: $currentComment")
        }
    }

    override fun getItemCount(): Int = comments.size

    fun setData(newData: ArrayList<Comment>) {
        Log.d(TAG, "setData: we are inside this")
        comments = newData
        notifyDataSetChanged()
    }
}

