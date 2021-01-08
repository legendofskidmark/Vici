package com.vici.vici.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Filter
import android.widget.TextView
import com.vici.vici.models.Product


class HPSearchAdapter(context: Context, resource: Int, textViewResourceId: Int, items: MutableList<Product>) :
    ArrayAdapter<Product>(context, resource, textViewResourceId, items) {

    var mContext: Context
    var resource: Int
    var textViewResourceId: Int
    var items: MutableList<Product>
    var tempItems: MutableList<Product>
    var suggestions: MutableList<Product>

    init {
        this.mContext = context
        this.resource = resource
        this.textViewResourceId = textViewResourceId
        this.items = items
        tempItems = ArrayList<Product>(items) // this makes the difference.
        suggestions = ArrayList<Product>()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var view = convertView
        if (convertView == null) {
            val inflater = mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
//            view = inflater.inflate(R.layout.autocomplete_item, parent, false)
            view = inflater.inflate(android.R.layout.simple_list_item_1, parent, false)
        }
        val product : Product = items[position]
        if (product  != null) {
            val lblName = view?.findViewById(android.R.id.text1) as TextView
            lblName?.setText(product.title)
        }
        return view!!
    }


    override fun getFilter(): Filter {
        return nameFilter
    }

    /**
     * Custom Filter implementation for custom suggestions we provide.
     */
    var nameFilter: Filter = object : Filter() {


        override fun convertResultToString(resultValue: Any?): CharSequence {
            return (resultValue as Product).title
        }

        override fun performFiltering(constraint: CharSequence?): FilterResults {
            return if (constraint != null) {
                suggestions.clear()
                for (product in tempItems) {
                    if (product.title.toLowerCase().contains(constraint.toString().toLowerCase())) {
                        suggestions.add(product)
                    }
                }
                val filterResults = FilterResults()
                filterResults.values = suggestions
                filterResults.count = suggestions.size
                filterResults
            } else {
                FilterResults()
            }
        }

        override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
            if (results!!.count > 0) {
                val filterList = results!!.values as ArrayList<Product>
                if (results != null && results.count > 0) {
                    clear()
                    for (product in filterList) {
                        add(product)
                        notifyDataSetChanged()
                    }
                }
            }
        }
    }
}