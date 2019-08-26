package com.crossbugJOBHUB.commons


import android.view.View
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView


@FunctionalInterface
interface OnDataBindListener<T> {
    fun onDataBind(view: View, item: T, position: Int, viewMap: Map<Int, View>)
}

@FunctionalInterface
interface OnMultiViewDataBindListener<T> {
    fun onMultiViewDataBind(view: View, item: T, position: Int, typedViewMap: Map<Int, Map<Int, View>>)
}

@FunctionalInterface
interface BindItemViewType<T> {
    fun bindItemViewType(item: T): Int
}

@FunctionalInterface
interface OnItemClickListener<T> {
    fun onItemClick(item: T, position: Int)
}

@FunctionalInterface
interface OnItemLongClickListener<T> {
    fun onItemLongClick(item: T, position: Int)
}

@FunctionalInterface
interface OnOptionMenuClickListener<T> {
    fun onOptionMenuClick(popUpMenu: PopupMenu, item: T, position: Int)
}

@FunctionalInterface
interface OnItemChildClickListener<T> {
    fun onItemChildClick(item: T, position: Int, view: View)
}

@FunctionalInterface
interface OnRetryClickListener {
    fun onRetry()
}

enum class LayoutType {
    LIST, GRID, STAGGERED
}

object Orientation {
    const val VERTICAL = RecyclerView.VERTICAL
    const val HORIZONTAL = RecyclerView.HORIZONTAL
}