package com.crossbugJOBHUB.commons

import android.app.Activity
import android.app.AlertDialog
import android.text.Editable
import android.text.TextWatcher
import android.widget.*
import com.crossbugJOBHUB.R


class SingleSelectListDialog<T>(private val activity: Activity, private val items: List<T>, var title: String = "Select") {

    private var itemListener: ((item: T, data: String, position: Int) -> Unit)? = null
    private lateinit var alertDialog: AlertDialog

    fun setItemClickListener(singleItemListener: ((item: T, data: String, position: Int) -> Unit)): SingleSelectListDialog<T> {
        this.itemListener = singleItemListener
        return this
    }

    fun show() {
        val alertDialogBuilder = AlertDialog.Builder(activity)

        val dialogView = activity.layoutInflater.inflate(R.layout.dialog_list_view_layout, null)

        val rippleViewClose = dialogView.findViewById(R.id.close) as TextView
        val titleView = dialogView.findViewById(R.id.spinnerTitle) as TextView
        val listView = dialogView.findViewById(R.id.list) as ListView
        val searchBox = dialogView.findViewById(R.id.searchBox) as EditText

        titleView.text = title

        val adapter = ArrayAdapter<T>(activity, android.R.layout.simple_list_item_1, items)
        listView.adapter = adapter

        alertDialogBuilder.setView(dialogView)

        alertDialog = alertDialogBuilder.create()

        alertDialog.setCancelable(true)

        listView.onItemClickListener = AdapterView.OnItemClickListener { parent, view, pos, _ ->
            val t = view.findViewById(android.R.id.text1) as TextView
            val item = parent.getItemAtPosition(pos) as T
            if(itemListener != null)
                itemListener!!.invoke(item, t.text.toString(), pos)
            alertDialog.dismiss()
        }

        searchBox.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}

            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}

            override fun afterTextChanged(editable: Editable) {
                adapter.filter.filter(searchBox.text.toString())
            }
        })

        rippleViewClose.setOnClickListener { alertDialog.dismiss() }
        alertDialog.show()

    }

}
