package com.htuncel.mobilhesap

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.marginBottom
import java.text.SimpleDateFormat
import java.util.*

class RecipeAdapter(
    private val context: Context,
    private val dataSource: Array<ItemEntity>
) : BaseAdapter() {

    private val inflater: LayoutInflater =
        context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun getCount(): Int {
        return dataSource.size
    }


    override fun getItem(position: Int): Any {
        return dataSource[position]
    }


    override fun getItemId(position: Int): Long {
        return position.toLong()
    }


    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        // Get view for row item
        val rowView = inflater.inflate(R.layout.list_item, parent, false)

        val myFormat = "dd-MM-yyyy" // "EEE, d MMM yyyy"
        val sdf = SimpleDateFormat(myFormat, Locale("tr"))


        val nameTextView = rowView.findViewById<TextView>(R.id.name_list_text)
        val typeTextView = rowView.findViewById<TextView>(R.id.type_list_text)
        val costTextView = rowView.findViewById<TextView>(R.id.cost_list_text)
        val dateTextView = rowView.findViewById<TextView>(R.id.date_list_text)
        val listItemLayout = rowView.findViewById<LinearLayout>(R.id.list_item)

        val item = getItem(position) as ItemEntity

        if (item.type == "INCOME") {
            listItemLayout.setBackgroundResource(R.drawable.custom_button_6)
            nameTextView.setTextColor(Color.parseColor("#4CAF50"))
            typeTextView.setTextColor(Color.parseColor("#4CAF50"))
            costTextView.setTextColor(Color.parseColor("#4CAF50"))
            dateTextView.setTextColor(Color.parseColor("#4CAF50"))

        } else if (item.type == "SPENDING") {
            listItemLayout.setBackgroundResource(R.drawable.custom_button_2)
            nameTextView.setTextColor(Color.parseColor("#F44336"))
            typeTextView.setTextColor(Color.parseColor("#F44336"))
            costTextView.setTextColor(Color.parseColor("#F44336"))
            dateTextView.setTextColor(Color.parseColor("#F44336"))
        }

        val nameText = "İsim: " + item.name
        val costText = "Tutar: " + item.cost
        val dateText = "Tarih: " + sdf.format(Date(item.date))

        var typeText = ""

        when {
            item.type == "INCOME" -> typeText = "GELİR"
            item.type == "SPENDING" -> typeText = "HARCAMA"
        }

        nameTextView.text = nameText
        typeTextView.text = typeText
        costTextView.text = costText
        dateTextView.text = dateText

        return rowView
    }

}