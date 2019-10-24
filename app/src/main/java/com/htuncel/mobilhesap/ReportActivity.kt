package com.htuncel.mobilhesap

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import androidx.room.Room
import java.util.*
import android.R.array
import java.util.Arrays.asList
import androidx.core.app.ComponentActivity.ExtraData
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T



class ReportActivity : AppCompatActivity() {
    private lateinit var listView: ListView
    private lateinit var db: AppDB
    private lateinit var items: Array<ItemEntity>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_report)

        db = Room.databaseBuilder(applicationContext, AppDB::class.java, "ItemDB")
            .build()

        listView = findViewById(R.id.items_list_view)

        // Create new activity and redirect user to that activity
        Toast.makeText(this, "inside report activity", Toast.LENGTH_SHORT).show()


        Thread {
            // val query = "SELECT * FROM Item_Entity ORDER BY item_date ASC"
            // val items = db.itemDAO().runtimeQuery(SimpleSQLiteQuery(query))

            items = db.itemDAO().readAllItems()
            val adapter = RecipeAdapter(this, items)
            listView.adapter = adapter

        }.start()
    }
}
