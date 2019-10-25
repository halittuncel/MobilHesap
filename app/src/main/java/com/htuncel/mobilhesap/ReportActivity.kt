package com.htuncel.mobilhesap

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ListView
import androidx.room.Room


class ReportActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // init DB
        val db = Room.databaseBuilder(applicationContext, AppDB::class.java, "ItemDB")
            .build()

        // İf the db has no entry show empty list layout otherwise load the items
        Thread {
            var items = db.itemDAO().readAllItems()

            if (items.isEmpty()) {
                setContentView(R.layout.empty_list_layout)
            } else {
                setContentView(R.layout.activity_report)
                var listView = findViewById<ListView>(R.id.items_list_view)
                val adapter = RecipeAdapter(this, items)
                listView.adapter = adapter
            }

        }.start()


    }
}
