package com.example.mortgage

import java.math.BigDecimal

internal fun isAprValid(aprValue: String): Boolean {
    if (aprValue == "" ||
        aprValue == "." ||
        aprValue.toBigDecimal().compareTo(BigDecimal.ZERO) == 0 ||
        aprValue.toBigDecimal() < BigDecimal(0.01))
        return false
    return true
}

internal fun isMortgageAmountValid(mortgageAmount : String): Boolean {
    if (mortgageAmount.isBlank())
        return false
    return true
}

internal fun setDollarFormat(amount: BigDecimal): String {
    return java.text.NumberFormat.getCurrencyInstance().format(amount)
}

internal fun getBigDecimal(rawValue: String): BigDecimal {
    return rawValue.replace(Regex("""[$,%]"""), "")
        .toBigDecimal().setScale(2,4)
}