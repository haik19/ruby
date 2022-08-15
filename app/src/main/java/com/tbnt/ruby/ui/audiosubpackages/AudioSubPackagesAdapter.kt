package com.tbnt.ruby.ui.audiosubpackages

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.tbnt.ruby.entity.SubAudioEntity
import com.squareup.picasso.Picasso
import com.tbnt.ruby.databinding.SubAudioItemLayoutBinding
import com.tbnt.ruby.setRoundedCorner
import com.tbnt.ruby.toPx

class AudioSubPackagesAdapter(private val onClick: (id: String, index: Int) -> Unit) :
    ListAdapter<SubAudioEntity, AudioSubPackagesAdapter.SubAudioViewHolder>(object :
        DiffUtil.ItemCallback<SubAudioEntity>() {
        override fun areItemsTheSame(oldItem: SubAudioEntity, newItem: SubAudioEntity) =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: SubAudioEntity, newItem: SubAudioEntity) =
            oldItem.imageUrl == newItem.imageUrl
    }) {


    class SubAudioViewHolder(
        private val binding: SubAudioItemLayoutBinding,
        val onClick: (id: String, index: Int) -> Unit
    ) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            binding.previewImage.setRoundedCorner(20.toPx)
        }

        fun bind(subAudioEntity: SubAudioEntity) {
            binding.run {
                itemView.setOnClickListener {
                    onClick(subAudioEntity.id, adapterPosition)
                }
                audioTitle.text = subAudioEntity.title
                audioDuration.text = subAudioEntity.duration
                Picasso.get().load(subAudioEntity.imageUrl).into(previewImage)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubAudioViewHolder {
        return SubAudioViewHolder(
            SubAudioItemLayoutBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ),
            onClick
        )
    }

    override fun onBindViewHolder(holder: SubAudioViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}