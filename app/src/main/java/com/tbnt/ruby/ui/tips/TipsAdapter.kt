package com.tbnt.ruby.ui.tips

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.tbnt.ruby.databinding.TipsItemLayoutBinding
import com.tbnt.ruby.databinding.TipsTitleLayoutBinding
import com.tbnt.ruby.entity.Tips

const val TIPS_TITLE = 101
const val TIPS_CONTENT = 102

class TipsAdapter : ListAdapter<Tips, TipsAdapter.TipsViewHolder>(object :
    DiffUtil.ItemCallback<Tips>() {
    override fun areItemsTheSame(oldItem: Tips, newItem: Tips) = oldItem == newItem
    override fun areContentsTheSame(oldItem: Tips, newItem: Tips) = oldItem == newItem
}) {

    override fun getItemViewType(position: Int): Int {
        return when (currentList.getOrNull(position)) {
            is Tips.Title -> TIPS_TITLE
            is Tips.Content -> TIPS_CONTENT
            else -> TIPS_TITLE
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TipsViewHolder {
        return when (viewType) {
            TIPS_TITLE -> TipsTitleViewHolder(
                TipsTitleLayoutBinding.inflate(
                    LayoutInflater.from(
                        parent.context
                    ), parent, false
                )
            )
            else -> TipsContentViewHolder(
                TipsItemLayoutBinding.inflate(
                    LayoutInflater.from(
                        parent.context
                    ), parent, false
                )
            )
        }
    }

    override fun onBindViewHolder(holder: TipsViewHolder, position: Int) {
        holder.onBind(getItem(position))
    }

    abstract class TipsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        abstract fun onBind(data: Tips)
    }

    class TipsTitleViewHolder(private val itemBinding: TipsTitleLayoutBinding) :
        TipsViewHolder(itemBinding.root) {
        override fun onBind(data: Tips) {
            if (data is Tips.Title) itemBinding.tipsTitle.text = data.text
        }
    }

    class TipsContentViewHolder(private val itemBinding: TipsItemLayoutBinding) :
        TipsViewHolder(itemBinding.root) {
        override fun onBind(data: Tips) {
            if (data is Tips.Content) itemBinding.content.text = data.text
        }
    }
}