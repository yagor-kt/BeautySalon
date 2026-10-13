package com.example.beautysalon.util

import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

object MoneyUtils {
    fun format(amount: Int): String {
        val symbols = DecimalFormatSymbols(Locale("ru", "RU")).apply {
            groupingSeparator = ' '
            decimalSeparator = ','
        }
        val formatter = DecimalFormat("#,##0", symbols)
        return "${formatter.format(amount)} ₽"
    }
}