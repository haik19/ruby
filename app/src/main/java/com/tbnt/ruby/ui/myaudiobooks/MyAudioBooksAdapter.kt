package com.tbnt.ruby.ui.myaudiobooks

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import com.tbnt.ruby.databinding.MyAudioFileItemLayoutBinding
import com.tbnt.ruby.entity.MyAudioBook

class MyAudioBooksAdapter(private val itemClick: (title: String, id: String) -> Unit) :
    ListAdapter<MyAudioBook, MyAudioBooksAdapter.MyAudioBookViewHolder>(object :
        DiffUtil.ItemCallback<MyAudioBook>() {
        override fun areItemsTheSame(oldItem: MyAudioBook, newItem: MyAudioBook) =
            oldItem == newItem

        override fun areContentsTheSame(oldItem: MyAudioBook, newItem: MyAudioBook) =
            oldItem == newItem
    }) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyAudioBookViewHolder {
        return MyAudioBookViewHolder(
            MyAudioFileItemLayoutBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ), itemClick
        )
    }

    override fun onBindViewHolder(holder: MyAudioBookViewHolder, position: Int) {
        holder.onBind(currentList[position])
    }

    class MyAudioBookViewHolder(
        private val itemBinding: MyAudioFileItemLayoutBinding,
        val click: (title: String, id: String) -> Unit,
    ) :
        RecyclerView.ViewHolder(itemBinding.root) {

        fun onBind(auiBook: MyAudioBook) {
            itemView.setOnClickListener {
                click(auiBook.title, auiBook.id)
            }
            Picasso.get().load(auiBook.imageUrl).into(itemBinding.myAudioImagePreview)
            itemBinding.myAudioTitle.text = auiBook.title
            itemBinding.myAudioTime.text = auiBook.duration
            itemBinding.myAudioRatingBar.rating = auiBook.rating
        }
    }
}