package com.example.ui.components

import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object Formatters {

    private val currencyFormat = DecimalFormat("##,##,##0.00")
    private val wholeCurrencyFormat = DecimalFormat("##,##,##0")
    private val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    private val shortDateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    private val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())

    fun formatCurrency(amount: Double, showDecimals: Boolean = true): String {
        val formatted = if (showDecimals) currencyFormat.format(amount) else wholeCurrencyFormat.format(amount)
        return "₹ $formatted"
    }

    fun formatDate(timestamp: Long): String {
        return dateFormat.format(Date(timestamp))
    }

    fun formatShortDate(timestamp: Long): String {
        return shortDateFormat.format(Date(timestamp))
    }

    fun formatDateTime(timestamp: Long): String {
        return "${dateFormat.format(Date(timestamp))} at ${timeFormat.format(Date(timestamp))}"
    }

    fun numberToWordsIndian(amount: Double): String {
        val total = amount.toLong()
        if (total == 0L) return "Zero Rupees Only"

        fun convertLessThanOneThousand(number: Int): String {
            val units = arrayOf(
                "", "One", "Two", "Three", "Four", "Five", "Six", "Seven", "Eight", "Nine",
                "Ten", "Eleven", "Twelve", "Thirteen", "Fourteen", "Fifteen", "Sixteen",
                "Seventeen", "Eighteen", "Nineteen"
            )
            val tens = arrayOf(
                "", "", "Twenty", "Thirty", "Forty", "Fifty", "Sixty", "Seventy", "Eighty", "Ninety"
            )
            var current = ""
            var n = number
            if (n % 100 < 20) {
                current = units[n % 100]
                n /= 100
            } else {
                current = units[n % 10]
                n /= 10
                current = tens[n % 10] + if (current.isNotEmpty()) " $current" else ""
                n /= 10
            }
            if (n == 0) return current
            return units[n] + " Hundred" + if (current.isNotEmpty()) " $current" else ""
        }

        var num = total
        var result = ""
        val crore = (num / 10000000).toInt()
        num %= 10000000
        val lakh = (num / 100000).toInt()
        num %= 100000
        val thousand = (num / 1000).toInt()
        num %= 1000
        val remainder = num.toInt()

        if (crore > 0) result += "${convertLessThanOneThousand(crore)} Crore "
        if (lakh > 0) result += "${convertLessThanOneThousand(lakh)} Lakh "
        if (thousand > 0) result += "${convertLessThanOneThousand(thousand)} Thousand "
        if (remainder > 0) result += convertLessThanOneThousand(remainder)

        return "Rupees ${result.trim()} Only"
    }
}
