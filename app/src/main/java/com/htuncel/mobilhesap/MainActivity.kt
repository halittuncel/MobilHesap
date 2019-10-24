package com.htuncel.mobilhesap

import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import kotlinx.android.synthetic.main.add_item_modal.view.*
import java.util.*
import android.widget.EditText
import androidx.room.Room
import java.lang.Exception
import java.math.BigDecimal
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.text.ParseException
import java.util.UUID.randomUUID
import androidx.core.app.ComponentActivity.ExtraData
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T


class MainActivity : AppCompatActivity() {
    private lateinit var newSpendingButton: Button
    private lateinit var newIncomeButton: Button
    private lateinit var reportButton: Button
    private lateinit var totalCostText: TextView
    private lateinit var myCalendar: Calendar
    private lateinit var db: AppDB

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        db = Room.databaseBuilder(applicationContext, AppDB::class.java, "ItemDB")
            .build()

        newIncomeButton = findViewById(R.id.new_income_button)
        newSpendingButton = findViewById(R.id.new_spending_button)
        reportButton = findViewById(R.id.report_button)
        totalCostText = findViewById(R.id.total_cost_text)
        myCalendar = Calendar.getInstance()

        calculateCost()

        newIncomeButton.setOnClickListener {
            onOpenModal("INCOME")
        }

        newSpendingButton.setOnClickListener {
            onOpenModal("SPENDING")
        }

        reportButton.setOnClickListener {
            val intent = Intent(this@MainActivity, ReportActivity::class.java)
            startActivity(intent)
        }
    }

    private fun calculateCost() {
        var income: BigDecimal = BigDecimal.ZERO
        var spending: BigDecimal = BigDecimal.ZERO

        Thread {
            // val query = "SELECT * FROM Item_Entity ORDER BY item_date ASC"
            // val items = db.itemDAO().runtimeQuery(SimpleSQLiteQuery(query))

            val items = db.itemDAO().readAllItems()

            items.forEach {
                when {
                    it.type == "INCOME" -> income += it.cost.toBigDecimal()
                    it.type == "SPENDING" -> spending += it.cost.toBigDecimal()
                }
            }
            var totalCost: BigDecimal = income - spending
            totalCostText.text = totalCost.toString()

        }.start()


    }

    private fun onOpenModal(type: String) {

        // Inflate the modal with custom layout
        val mDialogView = LayoutInflater.from(this).inflate(R.layout.add_item_modal, null)

        // AlertDialogBuilder
        val mBuilder = AlertDialog.Builder(this)
            .setView(mDialogView)
            .setTitle("Add Item")

        // Show Modal
        val mAlertDialog = mBuilder.show()
        mAlertDialog.setCanceledOnTouchOutside(false)

        when (type) {
            "INCOME" -> {
                mDialogView.item_name_edit_text.hint = "Gelir Adı"
                mAlertDialog.setTitle("Gelir Ekle")
            }
            "SPENDING" -> {
                mDialogView.item_name_edit_text.hint = "Gider Adı"
                mAlertDialog.setTitle("Gider Ekle")
            }
            else -> {
                // Dismiss the modal
                mAlertDialog.dismiss()
            }
        }

        val date = DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
            myCalendar.set(Calendar.YEAR, year)
            myCalendar.set(Calendar.MONTH, month)
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            updateLabel(mDialogView.item_date_edit_text)
        }

        mDialogView.item_date_edit_text.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                DatePickerDialog(
                    this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                    myCalendar.get(Calendar.DAY_OF_MONTH)
                ).show()
            }
        }

        // Buttons of modal
        mDialogView.add_item_button.setOnClickListener {
            // Get the values inside the modal
            val nameText = mDialogView.item_name_edit_text.text.toString()
            val costText = mDialogView.item_cost_edit_text.text.toString()
            val dateText = mDialogView.item_date_edit_text.text.toString()

            if (!validateName(nameText)) {
                mDialogView.item_date_edit_text.setBackgroundResource(R.drawable.custom_button_1)
                mDialogView.item_cost_edit_text.setBackgroundResource(R.drawable.custom_button_1)
                mDialogView.item_name_edit_text.setBackgroundResource(R.drawable.custom_button_2)
                mDialogView.hideKeyboard()
                Toast.makeText(
                    this,
                    "İsim boş olmamalıdır.",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            } else if (!validateCost(costText)) {
                mDialogView.item_name_edit_text.setBackgroundResource(R.drawable.custom_button_1)
                mDialogView.item_date_edit_text.setBackgroundResource(R.drawable.custom_button_1)
                mDialogView.item_cost_edit_text.setBackgroundResource(R.drawable.custom_button_2)
                mDialogView.hideKeyboard()
                Toast.makeText(
                    this,
                    "Tutar sayı olarak doğru bir şekilde girilmelidir.",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            } else if (!validateDate(dateText)) {
                mDialogView.item_cost_edit_text.setBackgroundResource(R.drawable.custom_button_1)
                mDialogView.item_name_edit_text.setBackgroundResource(R.drawable.custom_button_1)
                mDialogView.item_date_edit_text.setBackgroundResource(R.drawable.custom_button_2)
                mDialogView.hideKeyboard()
                Toast.makeText(
                    this,
                    "Tarih gün-ay-yıl formatında olmalıdır. Örnek (16-02-1999) ",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            } else when (type) {
                "INCOME" -> {
                    saveToDatabase(nameText, costText, type)
                }
                "SPENDING" -> {
                    saveToDatabase(nameText, costText, type)
                }
                else -> {
                }
            }

            // Dismiss the modal
            mAlertDialog.dismiss()
        }

        mDialogView.close_modal_button.setOnClickListener {
            // Dismiss the modal
            mAlertDialog.dismiss()
        }


    }

    private fun saveToDatabase(
        nameText: String,
        costText: String,
        type: String
    ) {

        val cost: BigDecimal = costText.toBigDecimal()

        var income: BigDecimal = BigDecimal.ZERO
        var spending: BigDecimal = BigDecimal.ZERO
        var totalCost: BigDecimal = BigDecimal.ZERO

        var items: Array<ItemEntity> = emptyArray()

        val uniqueID = randomUUID().toString()
        Thread {
            val item = ItemEntity(
                id = uniqueID,
                name = nameText,
                cost = cost.toString(),
                date = myCalendar.time.time,
                type = type
            )

            db.itemDAO().saveItem(item)

            items = db.itemDAO().readAllItems()

            items.forEach {
                when {
                    it.type == "INCOME" -> income += it.cost.toBigDecimal()
                    it.type == "SPENDING" -> spending += it.cost.toBigDecimal()
                }
            }
            totalCost = income - spending

            val totCostTextView = findViewById<TextView>(R.id.total_cost_text)



            runOnUiThread {
                totCostTextView.text = totalCost.toString()
            }

        }.start()

    }

    private fun updateLabel(itemDateEditText: EditText) {
        val myFormat = "dd-MM-yyyy" // "EEE, d MMM yyyy"
        val sdf = SimpleDateFormat(myFormat, Locale("tr"))

        itemDateEditText.setText(sdf.format(myCalendar.time))
    }

    private fun validateFields(
        nameText: String,
        costText: String,
        dateText: String
    ): Boolean {
        if (!validateCost(costText)) {
            return false
        }
        if (!validateName(nameText)) {
            return false
        }
        if (!validateDate(dateText)) {
            return false
        }
        return true
    }

    private fun validateCost(costText: String): Boolean {
        try {
            val cost = costText.toBigDecimal()
            return cost > BigDecimal.ZERO
        } catch (e: Exception) {
            Toast.makeText(this, "Miktar kısmı doğru girilmelidir.", Toast.LENGTH_SHORT).show()
        }
        return false
    }

    private fun validateName(nameText: String): Boolean {
        val validationText = nameText.trim()
        if (validationText.isBlank() && validationText.isEmpty()) {
            return false
        }
        return true
    }

    private fun validateDate(dateText: String): Boolean {
        val validationText = dateText.trim()
        if (validationText.isBlank() && validationText.isEmpty()) {
            return false
        }

        val format = SimpleDateFormat("dd-MM-yyyy")

        // Input to be parsed should strictly follow the defined date format
        // above.
        format.isLenient = false

        val date = "09-10-2019"
        return try {
            format.parse(date)
            true
        } catch (e: ParseException) {
            false
        }
    }

    fun View.hideKeyboard() {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(windowToken, 0)
    }

}
