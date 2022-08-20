package com.tbnt.ruby.ui.auidiobooks

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import com.tbnt.ruby.databinding.AudioFileItemLayoutBinding
import com.tbnt.ruby.entity.AudioBook

class AudioBooksAdapter(private val itemClick: (id: String, imageView: ImageView) -> Unit) :
    ListAdapter<AudioBook, AudioBooksAdapter.AudioBookViewHolder>(object :
        DiffUtil.ItemCallback<AudioBook>() {
        override fun areItemsTheSame(oldItem: AudioBook, newItem: AudioBook) =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: AudioBook, newItem: AudioBook) =
            oldItem.isPurchased == newItem.isPurchased
    }) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AudioBookViewHolder {
        return AudioBookViewHolder(
            itemClick,
            AudioFileItemLayoutBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: AudioBookViewHolder, position: Int) {
        holder.onBind(currentList[holder.adapterPosition])
    }

    class AudioBookViewHolder(
        val click: (id: String, imageView: ImageView) -> Unit,
        private val itemBinding: AudioFileItemLayoutBinding
    ) :
        RecyclerView.ViewHolder(itemBinding.root) {

        fun onBind(auiBook: AudioBook) {
            if (auiBook.isPurchased) {
                itemBinding.audioPrice.visibility = View.GONE
                itemBinding.audioPurchasedText.visibility = View.VISIBLE
            } else {
                itemBinding.audioPrice.visibility = View.VISIBLE
                itemBinding.audioPurchasedText.visibility = View.GONE
            }
            itemBinding.audioImagePreview.transitionName = auiBook.imageUrl
            itemView.setOnClickListener {
                click(auiBook.id, itemBinding.audioImagePreview)
            }
            Picasso.get().load(auiBook.imageUrl).into(itemBinding.audioImagePreview)
            itemBinding.audioTitle.text = auiBook.title
            itemBinding.audioPrice.text = auiBook.price
            itemBinding.ratingBar.rating = auiBook.rating
        }
    }
}