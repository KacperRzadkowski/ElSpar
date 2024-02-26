package com.team12.ElSpar.model

enum class PricePeriod(val days: Int, val text: String) {
    DAY(1, "Dag"),
    WEEK(7, "Uke"),
    MONTH(30, "Måned"),
}