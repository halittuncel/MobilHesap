package com.htuncel.mobilhesap

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import java.lang.Exception
import java.math.BigDecimal
import java.text.ParseException
import java.text.SimpleDateFormat

// Validation for cost field if user enter negative or zero as value it fails
fun validateCost(costText: String): Boolean {
    return try {
        val cost = costText.toBigDecimal()
        cost > BigDecimal.ZERO
    } catch (e: Exception) {
        false
    }
}

// Validation for name field
fun validateName(nameText: String): Boolean {
    // trim from whitespaces
    val validationText = nameText.trim()
    if (validationText.isBlank() && validationText.isEmpty()) {
        return false
    }
    return true
}


// Validation for date field
fun validateDate(dateText: String): Boolean {
    val validationText = dateText.trim()
    if (validationText.isBlank() && validationText.isEmpty()) {
        return false
    }

    // Same format as when user enters a date from date dialog
    val format = SimpleDateFormat("dd-MM-yyyy")

    // Input to be parsed should strictly follow the defined date format
    // above.
    format.isLenient = false

    return try {
        format.parse(dateText)
        true
    } catch (e: ParseException) {
        false
    }
}


// Hides keyboard when focus changed to date dialog or other actions similar to it
fun View.hideKeyboard() {
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(windowToken, 0)
}