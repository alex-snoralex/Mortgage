package com.mortgage.tests

import com.example.mortgage.getBigDecimal
import com.example.mortgage.isAprValid
import com.example.mortgage.isMortgageAmountValid
import com.example.mortgage.setDollarFormat
import org.junit.Test
import java.math.BigDecimal

class CalculationTests {

    @Test
    fun homePrice_Control(){
       assert(isMortgageAmountValid("123"))
    }

    @Test
    fun homePrice_BlankShouldFail(){
        assert(!isMortgageAmountValid(""))
    }

    @Test
    fun apr_Control(){
        assert(isAprValid("4.5"))
    }

    @Test
    fun apr_BlankShouldFail(){
        assert(!isAprValid(""))
    }

    @Test
    fun apr_ZeroShouldFail(){
        assert(!isAprValid("0"))
    }

    @Test
    fun setDollarFormat_Control(){
        assert(setDollarFormat(BigDecimal(123456)) == "$123,456.00")
    }

    @Test
    fun getBigDecimal_Control(){
        val returnedBigDecimal = getBigDecimal("$123,456.78%")
        val control = BigDecimal(123456.78).setScale(2, 4)
        assert(returnedBigDecimal.compareTo(control) == 0)
    }

}