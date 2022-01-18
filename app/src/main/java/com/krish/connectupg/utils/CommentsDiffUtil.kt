package com.krish.connectupg.utils

import androidx.recyclerview.widget.DiffUtil
import com.krish.connectupg.model.Comment

class CommentsDiffUtil(
    private val oldList: List<Comment>,
    private val newList: List<Comment>
) : DiffUtil.Callback() {
    override fun getOldListSize(): Int {
        return oldList.size
    }

    override fun getNewListSize(): Int {
        return newList.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] === newList[newItemPosition]
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] == newList[newItemPosition]
    }
}