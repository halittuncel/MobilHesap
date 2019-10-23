package com.htuncel.mobilhesap

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast

class MainActivity : AppCompatActivity() {
    private lateinit var newSpendingButton: Button
    private lateinit var newIncomeButton: Button
    private lateinit var reportButton: Button
    private lateinit var totalCostText: TextView

    // TODO
    // Create SQLite database hold 2 table; income, spending

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        newIncomeButton = findViewById(R.id.new_income_button)
        newSpendingButton = findViewById(R.id.new_spending_button)
        reportButton = findViewById(R.id.report_button)
        totalCostText = findViewById(R.id.total_cost_text)

        newIncomeButton.setOnClickListener {
            openModal("INCOME")
        }

        newSpendingButton.setOnClickListener {
            openModal("SPENDING")
        }

        reportButton.setOnClickListener {
            // TODO
            // Create new activity and redirect user to that activity
            Toast.makeText(this, "report", Toast.LENGTH_SHORT).show()
        }
    }

    private fun openModal(type: String) {
        // TODO
        // Create modal and add new spending info or new income info to database
        when (type) {
            "INCOME" -> Toast.makeText(this, "income", Toast.LENGTH_SHORT).show()
            "SPENDING" -> Toast.makeText(this, "spending", Toast.LENGTH_SHORT).show()
            else -> return
        }
    }
}
