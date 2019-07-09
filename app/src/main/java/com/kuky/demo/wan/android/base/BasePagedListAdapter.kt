package com.kuky.demo.wan.android.base

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil

/**
 * @author kuky.
 * @description paging adapter 基类
 */
abstract class BasePagedListAdapter<T, VB : ViewDataBinding>(callback: DiffUtil.ItemCallback<T>) :
    PagedListAdapter<T, BaseViewHolder<VB>>(callback) {

    var itemListener: PagingItemClickListener? = null

    fun setOnItemListener(listener: PagingItemClickListener?) {
        this.itemListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<VB> {
        return BaseViewHolder(
            DataBindingUtil.inflate(LayoutInflater.from(parent.context), getLayoutId(), parent, false)
        )
    }

    override fun onBindViewHolder(holder: BaseViewHolder<VB>, position: Int) {
        val data = getItem(position) ?: return
        setVariable(data, position, holder)
        holder.binding.executePendingBindings()
        holder.binding.root.setOnClickListener { v -> itemListener?.onPagingItemClick(position, v) }
    }

    fun getItemData(position: Int): T? = getItem(position)

    abstract fun getLayoutId(): Int

    abstract fun setVariable(data: T, position: Int, holder: BaseViewHolder<VB>)
}