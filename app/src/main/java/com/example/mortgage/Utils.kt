package com.example.mortgage

import java.math.BigDecimal

internal fun isAprValid(aprValue: String): Boolean {
    if (aprValue == "" || aprValue == "." ||
        aprValue.toBigDecimal().compareTo(BigDecimal.ZERO) == 0)
        return false
    return true
}

internal fun setDollarFormat(amount: BigDecimal): String {
    return java.text.NumberFormat.getCurrencyInstance().format(amount)
}

internal fun getBigDecimal(rawValue: String): BigDecimal {
    return rawValue.replace("$", "")
        .replace(",", "").replace("%","").toBigDecimal()
}