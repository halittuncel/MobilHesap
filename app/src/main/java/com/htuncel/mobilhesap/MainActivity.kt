package com.htuncel.mobilhesap

import android.app.DatePickerDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import kotlinx.android.synthetic.main.add_item_modal.view.*
import java.util.*
import android.widget.EditText
import androidx.room.Room
import java.math.BigDecimal
import java.text.SimpleDateFormat
import java.util.UUID.randomUUID


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

        // This build db with given AppDb class and db named ItemDB
        db = Room.databaseBuilder(applicationContext, AppDB::class.java, "ItemDB")
            .build()

        newIncomeButton = findViewById(R.id.new_income_button)
        newSpendingButton = findViewById(R.id.new_spending_button)
        reportButton = findViewById(R.id.report_button)
        totalCostText = findViewById(R.id.total_cost_text)
        myCalendar = Calendar.getInstance()

        // When app starts calcutae cost and change text view accordingly
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

    // Calculates the total cost
    private fun calculateCost() {
        var income: BigDecimal = BigDecimal.ZERO
        var spending: BigDecimal = BigDecimal.ZERO

        Thread {
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

        // Inflate the Alert Dialog with custom layout
        val mDialogView = LayoutInflater.from(this).inflate(R.layout.add_item_modal, null)

        // AlertDialogBuilder
        val mBuilder = AlertDialog.Builder(this)
            .setView(mDialogView)
            .setTitle("Add Item")

        // Show Modal
        val mAlertDialog = mBuilder.show()
        mAlertDialog.setCanceledOnTouchOutside(false)

        // Change alert title according to the action
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

        // Date Dialog Field for Date Edit Text
        val date = DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
            myCalendar.set(Calendar.YEAR, year)
            myCalendar.set(Calendar.MONTH, month)
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            updateLabel(mDialogView.item_date_edit_text)
        }

        // When user clicks the date edit text open up date dialog for user to choose date
        mDialogView.item_date_edit_text.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                mDialogView.hideKeyboard()
                DatePickerDialog(
                    this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                    myCalendar.get(Calendar.DAY_OF_MONTH)
                ).show()
            }
        }

        // On Save Button clicked check fields for validation and show approtiate warnings
        mDialogView.add_item_button.setOnClickListener {
            // Get the values inside the modal
            val nameText = mDialogView.item_name_edit_text.text.toString()
            val costText = mDialogView.item_cost_edit_text.text.toString()
            val dateText = mDialogView.item_date_edit_text.text.toString()

            // if name field is empty or blank change background to red
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
                // if cost field is empty or blank change background to red
                mDialogView.item_name_edit_text.setBackgroundResource(R.drawable.custom_button_1)
                mDialogView.item_date_edit_text.setBackgroundResource(R.drawable.custom_button_1)
                mDialogView.item_cost_edit_text.setBackgroundResource(R.drawable.custom_button_2)
                mDialogView.hideKeyboard()
                Toast.makeText(
                    this,
                    "Tutar sayı olarak doğru bir şekilde girilmelidir ve 0' ın üzerinde bir sayı olmalıdır.",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            } else if (!validateDate(dateText)) {
                // if date field is empty or blank change background to red
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

        // Close the alert dialog
        mDialogView.close_modal_button.setOnClickListener {
            // Dismiss the modal
            mAlertDialog.dismiss()
        }


    }

    // Save income, spending info to db
    private fun saveToDatabase(
        nameText: String,
        costText: String,
        type: String
    ) {

        // Convert cost edit text value to bigdecimal
        val cost: BigDecimal = costText.toBigDecimal()

        var income: BigDecimal = BigDecimal.ZERO
        var spending: BigDecimal = BigDecimal.ZERO
        var totalCost: BigDecimal

        var items: Array<ItemEntity>

        // assign unique id for every entry to db
        val uniqueID = randomUUID().toString()

        // run on different threat for db actions
        Thread {
            val item = ItemEntity(
                id = uniqueID,
                name = nameText,
                cost = cost.toString(),
                date = myCalendar.time.time,
                type = type
            )

            // save item to db
            db.itemDAO().saveItem(item)

            // get all items in db then calculate total cost value and set it to total cost text view
            items = db.itemDAO().readAllItems()
            items.forEach {
                when {
                    it.type == "INCOME" -> income += it.cost.toBigDecimal()
                    it.type == "SPENDING" -> spending += it.cost.toBigDecimal()
                }
            }
            totalCost = income - spending


            // Need this to run on ui thread for updating view items otherwise app crashes
            runOnUiThread {
                totalCostText.text = totalCost.toString()
            }

        }.start()

    }

    // This is for date edit text when user chooses a date from date dialog takes the result and sets it to the edit text
    // otherwise edit text is unchangeable by user
    private fun updateLabel(itemDateEditText: EditText) {
        val myFormat = "dd-MM-yyyy" // "EEE, d MMM yyyy"
        val sdf = SimpleDateFormat(myFormat, Locale("tr"))

        itemDateEditText.setText(sdf.format(myCalendar.time))
    }

}
