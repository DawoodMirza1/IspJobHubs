package com.crossbugJOBHUB.commons


import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.crossbugJOBHUB.R


/**************************************************************************
 ** Recycler Adaptor
 **************************************************************************/
class SimpleRecyclerAdaptor<T> private constructor(private val context: Context, private var dataList: MutableList<T>)
    : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        const val ITEM = 0
        const val LOADING = -1
        const val ERROR = -2
    }

    private var isLoading = false
    private var isError = false

    private var onDataBindListener: OnDataBindListener<T>? = null
    private var onMultiViewDataBindListener: OnMultiViewDataBindListener<T>? = null
    private var bindItemViewType: BindItemViewType<T>? = null
    private var onItemClickListener: OnItemClickListener<T>? = null
    private var onItemLongClickListener: OnItemLongClickListener<T>? = null
    private var onOptionMenuClickListener: OnOptionMenuClickListener<T>? = null
    private var onItemChildClickListener: OnItemChildClickListener<T>? = null
    private var onRetryClickListener: OnRetryClickListener? = null

    private var mOnDataBindListener: ((view: View, item: T, position: Int, viewMap: Map<Int, View>) -> Unit)? = null
    private var mOnMultiViewDataBindListener:((view: View, item: T, position: Int, typedViewMap: Map<Int, Map<Int, View>>) -> Unit)? = null
    private var mBindItemViewType: ((item: T) -> Int)? = null
    private var mOnItemClickListener: ((item: T, position: Int) -> Unit)? = null
    private var mOnItemLongClickListener: ((item: T, position: Int) -> Unit)? = null
    private var mOnOptionMenuClickListener: ((popUpMenu: PopupMenu, item: T, position: Int) -> Unit)? = null
    private var mOnItemChildClickListener: ((item: T, position: Int, view: View) -> Unit)? = null
    private var mOnRetryClickListener: (() -> Unit)? = null

    private var layout: Int = -1
    private var viewsList: MutableList<Int> = mutableListOf()

    private var layouts: Map<Int, Int>? = null
    private var mapViewsList: Map<Int, MutableList<Int>> = mutableMapOf()
    private var multiViewLayout = false

    private var clickableItem: Boolean = false
    private var longClickableItem: Boolean = false
    private var hasOptionMenu: Boolean = false
    private var optionMenu: Int = 0
    private var optionMenuViewId: Int = 0

    private constructor(context: Context, dataList: MutableList<T>, layout: Int, viewsList: MutableList<Int>):
            this(context, dataList) {
        this.layout = layout
        this.viewsList = viewsList
    }

    private constructor(context: Context, dataList: MutableList<T>, layouts: Map<Int, Int>, mapViewsList: Map<Int, MutableList<Int>>):
            this(context, dataList) {
        this.layouts = layouts
        this.mapViewsList = mapViewsList
        multiViewLayout = true
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(context)
        when(viewType) {
            LOADING -> {
                return LoadingViewHolder(context, inflater.inflate(R.layout.paginated_item_progress, parent, false))
            }
            ERROR -> {
                return ErrorViewHolder(context, inflater.inflate(R.layout.paginated_item_error, parent, false))
            }
        }
        return if (multiViewLayout) {
            ViewHolder(context, inflater.inflate(layouts!![viewType]!!, parent, false))
        } else {
            ViewHolder(context, inflater.inflate(layout, parent, false))
        }
    }

    override fun getItemCount() = dataList.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(getItemViewType(position)) {
            LOADING -> { }
            ERROR -> { }
            else -> {
                val itemHolder = holder as SimpleRecyclerAdaptor<T>.ViewHolder
                itemHolder.bind(dataList[position], position)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        if(position == dataList.size - 1) {
            if(isLoading) return LOADING else if(isError) return ERROR
        }

        return if(multiViewLayout) {
            if (mBindItemViewType != null)
                mBindItemViewType?.invoke(dataList[position])!!
            else
                bindItemViewType?.bindItemViewType(dataList[position])!!
        } else {
            super.getItemViewType(position)
        }
    }

    fun changeDataList(list: MutableList<T>) {
        dataList = list
        notifyDataSetChanged()
    }

    fun addDataList(list: MutableList<T>) {
        val size = dataList.size
        dataList.addAll( size - 1, list)
        notifyItemRangeInserted(size - 1, list.size)
    }

    fun addDataListInOrder(list: MutableList<T>) {
        val size = dataList.size
        list.forEach { dataList.add(it) }
        notifyItemRangeInserted( size - 1, list.size)
    }

    fun addItem(item: T) {
        dataList.add(item)
        notifyItemInserted(dataList.size - 1)
    }

    fun addItem(position: Int, item: T) {
        dataList.add(position, item)
        notifyItemInserted(position)
    }

    fun removeItemAtPosition(position: Int) {
        dataList.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, dataList.size)
    }

    fun getItem(position: Int) = dataList[position]

    fun updateItem(data: T, position: Int) {
        dataList[position] = data
        notifyItemChanged(position)
    }

    fun updateItemState(position: Int, changes: T.() -> Unit) {
        dataList[position].changes()
        notifyItemChanged(position)
    }

    fun addLoading(item: T) {
        removeError()
        if(!isLoading) {
            isLoading = true
            dataList.add(item)
            notifyItemInserted(dataList.size - 1)
        }
    }

    fun removeLoading() {
        if(isLoading) {
            isLoading = false
            dataList.removeAt(dataList.size - 1)
            notifyItemRemoved(dataList.size - 1)
        }
    }

    fun addError(item: T) {
        removeLoading()
        if(!isError) {
            isError = true
            dataList.add(item)
            notifyItemInserted(dataList.size - 1)
        }
    }

    fun removeError() {
        if(isError) {
            isError = false
            dataList.removeAt(dataList.size - 1)
            notifyItemRemoved(dataList.size - 1)
        }
    }

    fun setDataBindListener(onDataBindListener: OnDataBindListener<T>) =
        apply { this.onDataBindListener = onDataBindListener }

    fun setDataBindListener(onDataBindListener: (view: View, item: T, position: Int, viewMap: Map<Int, View>) -> Unit) =
        apply { this.mOnDataBindListener = onDataBindListener }

    fun setMultiViewDataBindListener(onMultiViewDataBindListener: OnMultiViewDataBindListener<T>) =
        apply { this.onMultiViewDataBindListener = onMultiViewDataBindListener }

    fun setMultiViewDataBindListener(onMultiViewDataBindListener:(view: View, item: T, position: Int, typedViewMap: Map<Int, Map<Int, View>>) -> Unit) =
        apply { this.mOnMultiViewDataBindListener = onMultiViewDataBindListener }

    fun setBindViewType(bindItemViewType: BindItemViewType<T>) =
        apply { this.bindItemViewType = bindItemViewType }

    fun setBindViewType(mBindItemViewType: (item: T) -> Int) =
        apply { this.mBindItemViewType = mBindItemViewType }

    fun setItemClickListener(onItemClickListener: OnItemClickListener<T>) =
        apply { this.clickableItem = true; this.onItemClickListener = onItemClickListener }

    fun setItemClickListener(onItemClickListener: (item: T, position: Int) -> Unit) =
        apply { this.clickableItem = true; this.mOnItemClickListener = onItemClickListener }

    fun setItemLongClickListener(onItemLongClickListener: OnItemLongClickListener<T>) =
        apply { this.longClickableItem = true; this.onItemLongClickListener = onItemLongClickListener }

    fun setItemLongClickListener(onItemLongClickListener: (item: T, position: Int) -> Unit) =
        apply { this.longClickableItem = true; this.mOnItemLongClickListener = onItemLongClickListener }

    fun setItemChildClickListener(onItemChildClickListener: OnItemChildClickListener<T>) =
        apply { this.onItemChildClickListener = onItemChildClickListener }

    fun setItemChildClickListener(onItemChildClickListener: (item: T, position: Int, view: View) -> Unit) =
        apply { this.mOnItemChildClickListener = onItemChildClickListener }

    fun setOptionMenuClickListener(onOptionMenuClickListener: OnOptionMenuClickListener<T>) =
        apply { this.hasOptionMenu = true; this.onOptionMenuClickListener = onOptionMenuClickListener }

    fun setOptionMenuClickListener(onOptionMenuClickListener: (popUpMenu: PopupMenu, item: T, position: Int) -> Unit) =
        apply { this.hasOptionMenu = true; this.mOnOptionMenuClickListener = onOptionMenuClickListener }

    fun setOptionMenu(optionMenuViewId: Int, menu: Int) =
        apply { this.optionMenuViewId = optionMenuViewId; this.optionMenu = menu }

    fun setOnRetryClickListener(onRetryClickListener: OnRetryClickListener) =
        apply { this.onRetryClickListener = onRetryClickListener }

    fun setOnRetryClickListener(onRetryClickListener: () -> Unit) =
        apply { this.mOnRetryClickListener = onRetryClickListener }

    private fun onDataBind(view: View, item: T, position: Int, viewMap: Map<Int, View>) {
        onDataBindListener?.onDataBind(view, item, position, viewMap)
    }

    private fun onMultiViewDataBind(view: View, item: T, position: Int, typedViewMap: Map<Int, Map<Int, View>>) {
        onMultiViewDataBindListener?.onMultiViewDataBind(view, item, position, typedViewMap)
    }

    private fun onItemClick(data: T, position: Int) {
        onItemClickListener?.onItemClick(data, position)
    }

    private fun onItemLongClick(data: T, position: Int) {
        onItemLongClickListener?.onItemLongClick(data, position)
    }

    private fun onOptionMenuClickListener(popUpMenu: PopupMenu, item: T, position: Int) {
        onOptionMenuClickListener?.onOptionMenuClick(popUpMenu, item, position)
    }

    private fun onItemChildClick(data: T, position: Int, view: View) {
        onItemChildClickListener?.onItemChildClick(data, position, view)
    }

    /**************************************************************************
     ** Builder Pattern
     **************************************************************************/
    class Builder<T>(private val context: Context) {

        private var dataList: MutableList<T> = mutableListOf()

        private var layout = 0
        private var viewsList: MutableList<Int> = mutableListOf()

        private var layouts: Map<Int, Int> = emptyMap()
        private var mapViewsList: Map<Int, MutableList<Int>> = emptyMap()

        private var optionMenu = 0
        private var optionMenuViewId = 0

        private var onDataBindListener: OnDataBindListener<T>? = null
        private var onMultiViewDataBindListener: OnMultiViewDataBindListener<T>? = null
        private var bindItemViewType: BindItemViewType<T>? = null
        private var onItemClickListener: OnItemClickListener<T>? = null
        private var onItemLongClickListener: OnItemLongClickListener<T>? = null
        private var onOptionMenuClickListener: OnOptionMenuClickListener<T>? = null
        private var onItemChildClickListener: OnItemChildClickListener<T>? = null
        private var onRetryClickListener: OnRetryClickListener? = null

        private var mOnDataBindListener: ((view: View, item: T, position: Int, viewMap: Map<Int, View>) -> Unit)? = null
        private var mOnMultiViewDataBindListener:((view: View, item: T, position: Int, typedViewMap: Map<Int, Map<Int, View>>) -> Unit)? = null
        private var mBindItemViewType: ((item: T) -> Int)? = null
        private var mOnItemClickListener: ((item: T, position: Int) -> Unit)? = null
        private var mOnItemLongClickListener: ((item: T, position: Int) -> Unit)? = null
        private var mOnOptionMenuClickListener: ((popUpMenu: PopupMenu, item: T, position: Int) -> Unit)? = null
        private var mOnItemChildClickListener: ((item: T, position: Int, view: View) -> Unit)? = null
        private var mOnRetryClickListener: (() -> Unit)? = null

        fun setDataList(dataList: MutableList<T>) = apply { this.dataList = dataList }

        fun setLayout(layout: Int) = apply { this.layout = layout }
        fun setLayouts(layouts: Map<Int, Int>) = apply { this.layouts = layouts }

        fun addView(resId: Int) = apply { viewsList.add(resId) }
        fun addViews(vararg resId: Int) = apply { resId.forEach { viewsList.add(it) } }
        fun addViews(list: List<Int>) = apply { viewsList = list as MutableList<Int> }

        fun addViewForType(viewType: Int, resId: Int) = apply {
            if (mapViewsList.containsKey(viewType)) {
                mapViewsList[viewType]?.add(resId)
            } else {
                mapViewsList = mapOf(viewType to mutableListOf(resId))
            }
        }

        fun addViewsForType(viewType: Int, vararg resId: Int) = apply {
            resId.forEach {
                if (mapViewsList.containsKey(viewType)) {
                    mapViewsList[viewType]?.add(it)
                } else {
                    mapViewsList = mapOf(viewType to mutableListOf(it))
                }
            }
        }

        fun addViewsForType(viewType: Int, list: List<Int>) = apply {
            if (mapViewsList.containsKey(viewType)) {
                mapViewsList[viewType]?.addAll(list)
            } else {
                mapViewsList = mapOf(viewType to list as MutableList<Int>)
            }
        }

        fun setOptionMenu(optionMenuViewId: Int, menu: Int) =
            apply { this.optionMenuViewId = optionMenuViewId; this.optionMenu = menu }

        fun setBindViewListener(onDataBindListener: OnDataBindListener<T>) =
            apply { this.onDataBindListener = onDataBindListener }

        fun setBindViewListener(onDataBindListener: (view: View, item: T, position: Int, viewMap: Map<Int, View>) -> Unit) =
            apply { this.mOnDataBindListener = onDataBindListener }

        fun setMultiViewDataBindListener(onMultiViewDataBindListener: OnMultiViewDataBindListener<T>) =
            apply { this.onMultiViewDataBindListener = onMultiViewDataBindListener }

        fun setMultiViewDataBindListener(onMultiViewDataBindListener: (view: View, item: T, position: Int, typedViewMap: Map<Int, Map<Int, View>>) -> Unit) =
            apply { this.mOnMultiViewDataBindListener = onMultiViewDataBindListener }

        fun setBindViewType(bindItemViewType: BindItemViewType<T>) =
            apply { this.bindItemViewType = bindItemViewType }

        fun setBindViewType(mBindItemViewType: (item: T) -> Int) =
            apply { this.mBindItemViewType = mBindItemViewType }

        fun setItemClickListener(onItemClickListener: OnItemClickListener<T>) =
            apply { this.onItemClickListener = onItemClickListener }

        fun setItemClickListener(onItemClickListener: (item: T, position: Int) -> Unit) =
            apply { this.mOnItemClickListener = onItemClickListener }

        fun setItemLongClickListener(onItemLongClickListener: OnItemLongClickListener<T>) =
            apply { this.onItemLongClickListener = onItemLongClickListener }

        fun setItemLongClickListener(onItemLongClickListener: (item: T, position: Int) -> Unit) =
            apply { this.mOnItemLongClickListener = onItemLongClickListener }

        fun setItemChildClickListener(onItemChildClickListener: OnItemChildClickListener<T>) =
            apply { this.onItemChildClickListener = onItemChildClickListener }

        fun setItemChildClickListener(onItemChildClickListener: (item: T, position: Int, view: View) -> Unit) =
            apply { this.mOnItemChildClickListener = onItemChildClickListener }

        fun setOptionClickListener(onOptionMenuClickListener: OnOptionMenuClickListener<T>) =
            apply { this.onOptionMenuClickListener = onOptionMenuClickListener }

        fun setOptionMenuClickListener(onOptionMenuClickListener: (popUpMenu: PopupMenu, item: T, position: Int) -> Unit) =
            apply { this.mOnOptionMenuClickListener = onOptionMenuClickListener }

        fun setOnRetryClickListener(onRetryClickListener: OnRetryClickListener) =
            apply { this.onRetryClickListener = onRetryClickListener }

        fun setOnRetryClickListener(onRetryClickListener: () -> Unit) =
            apply { this.mOnRetryClickListener = onRetryClickListener }

        fun build(): SimpleRecyclerAdaptor<T> {
            return initAdaptor()
        }

        fun into(recyclerView: RecyclerView) {
            val adaptor = initAdaptor()
            recyclerView.adapter = adaptor
        }

        fun into(recyclerView: RecyclerView, block: RecyclerView.() -> Unit) {
            val adaptor = initAdaptor()
            recyclerView.block()
            recyclerView.adapter = adaptor
        }

        fun into(recyclerView: RecyclerView, layoutType: LayoutType = LayoutType.LIST, spanCount: Int = 2,
                 orientation: Int = Orientation.VERTICAL, divider: Boolean = false) {
            val adaptor = initAdaptor()

            if (divider && layoutType == LayoutType.LIST) {
                recyclerView.addItemDecoration(Divider(context))
            }

            when (layoutType) {
                LayoutType.LIST -> {
                    recyclerView.layoutManager = LinearLayoutManager(context)
                }
                LayoutType.GRID -> {
                    if (recyclerView.itemDecorationCount > 0) recyclerView.removeItemDecorationAt(0)
                    recyclerView.layoutManager = GridLayoutManager(context, spanCount, orientation, false)
                }
                LayoutType.STAGGERED -> {
                    if (recyclerView.itemDecorationCount > 0) recyclerView.removeItemDecorationAt(0)
                    recyclerView.layoutManager = StaggeredGridLayoutManager(spanCount, orientation)
                }
            }

            recyclerView.adapter = adaptor
        }

        private fun initAdaptor(): SimpleRecyclerAdaptor<T> {
            val adaptor = if (layouts.isNotEmpty() && mapViewsList.isNotEmpty()) {
                SimpleRecyclerAdaptor(context, dataList, layouts, mapViewsList)
            } else {
                SimpleRecyclerAdaptor(context, dataList, layout, viewsList)
            }

            adaptor.setOptionMenu(optionMenuViewId, optionMenu)

            if (mOnDataBindListener != null) adaptor.setDataBindListener(mOnDataBindListener!!)
            else if (onDataBindListener != null) adaptor.setDataBindListener(onDataBindListener!!)

            if (mOnMultiViewDataBindListener != null) adaptor.setMultiViewDataBindListener(mOnMultiViewDataBindListener!!)
            else if (onMultiViewDataBindListener != null) adaptor.setMultiViewDataBindListener(onMultiViewDataBindListener!!)

            if (bindItemViewType != null) adaptor.setBindViewType(bindItemViewType!!)
            else if (mBindItemViewType != null) adaptor.setBindViewType(mBindItemViewType!!)

            if (mOnItemClickListener != null) adaptor.setItemClickListener(mOnItemClickListener!!)
            else if (onItemClickListener != null) adaptor.setItemClickListener(onItemClickListener!!)

            if (mOnItemLongClickListener != null) adaptor.setItemLongClickListener(mOnItemLongClickListener!!)
            else if (onItemLongClickListener != null) adaptor.setItemLongClickListener(onItemLongClickListener!!)

            if (mOnItemChildClickListener != null) adaptor.setItemChildClickListener(mOnItemChildClickListener!!)
            else if (onItemChildClickListener != null) adaptor.setItemChildClickListener(onItemChildClickListener!!)

            if (mOnOptionMenuClickListener != null) adaptor.setOptionMenuClickListener(mOnOptionMenuClickListener!!)
            else if (onOptionMenuClickListener != null) adaptor.setOptionMenuClickListener(onOptionMenuClickListener!!)

            if (mOnRetryClickListener != null) adaptor.setOnRetryClickListener(mOnRetryClickListener!!)
            else if (onRetryClickListener != null) adaptor.setOnRetryClickListener(onRetryClickListener!!)

            return adaptor
        }

    }

    /**************************************************************************
     ** Item View Holder
     **************************************************************************/
    inner class ViewHolder(private val context: Context, private val view: View) :
        RecyclerView.ViewHolder(view), View.OnClickListener, View.OnLongClickListener {

        private var viewMap: MutableMap<Int, View> = mutableMapOf()
        private var typedViewMap: MutableMap<Int, MutableMap<Int, View>> = mutableMapOf()
        private var item: T? = null
        private var pos: Int = 0

        init {
            if (clickableItem) { view.setOnClickListener(this) }

            if (longClickableItem) { view.setOnLongClickListener(this) }

            if (hasOptionMenu && optionMenu != 0 && optionMenuViewId != 0) {

                val menu = view.findViewById<View>(optionMenuViewId)
                menu.visibility = View.VISIBLE
                menu.setOnClickListener { om ->
                    val popUpMenu = PopupMenu(context, om)
                    popUpMenu.inflate(optionMenu)

                    if (mOnOptionMenuClickListener != null)
                        mOnOptionMenuClickListener?.invoke(popUpMenu, item!!, pos)
                    else
                        onOptionMenuClickListener(popUpMenu, item!!, pos)
                }

            }

            if (multiViewLayout) {
                mapViewsList.forEach { (type, mutableList) ->
                    var map = mutableMapOf<Int, View>()
                    mutableList.forEach { id ->
                        map = mutableMapOf(id to view.findViewById(id))
                    }
                    typedViewMap[type] = map
                }
            } else {
                viewsList.forEach {
                    viewMap[it] = view.findViewById(it)
                }
            }
        }

        fun bind(t: T, p: Int) {
            item = t
            pos = p
            if (multiViewLayout) {
                if (mOnMultiViewDataBindListener != null) {
                    mOnMultiViewDataBindListener?.invoke(view, t, p, typedViewMap)
                } else {
                    onMultiViewDataBind(view, t, p, typedViewMap)
                }
            } else {
                if (mOnDataBindListener != null) {
                    mOnDataBindListener?.invoke(view, t, p, viewMap)
                } else {
                    onDataBind(view, t, p, viewMap)
                }
            }
        }

        override fun onClick(v: View?) {
            if (mOnItemClickListener != null) {
                mOnItemClickListener?.invoke(item!!, pos)
            } else {
                onItemClick(item!!, pos)
            }
        }

        override fun onLongClick(v: View?): Boolean {
            if (mOnItemLongClickListener != null) {
                mOnItemLongClickListener?.invoke(item!!, pos)
            } else {
                onItemLongClick(item!!, pos)
            }
            return true
        }
    }

    /**************************************************************************
     ** Loading Item View Holder
     **************************************************************************/
    inner class LoadingViewHolder(private val context: Context, private val view: View): RecyclerView.ViewHolder(view)

    /**************************************************************************
     ** Error Item View Holder
     **************************************************************************/
    inner class ErrorViewHolder(private val context: Context, private val view: View):
        RecyclerView.ViewHolder(view), View.OnClickListener {

        init {
            view.setOnClickListener(this)
        }

        override fun onClick(p0: View?) {
            if(mOnRetryClickListener != null) {
                mOnRetryClickListener?.invoke()
            } else {
                onRetryClickListener?.onRetry()
            }
        }

    }
}