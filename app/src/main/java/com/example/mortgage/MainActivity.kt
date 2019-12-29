package com.example.mortgage

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main.*
import java.math.BigDecimal
import java.math.RoundingMode


class MainActivity : AppCompatActivity() {

    /**
     * @TODO: Change Loan length to radio button select of 15 or 30 years
     * @TODO: Make 30 year loan length default
     * @TODO: Set default Downpayment to 20%
     * @TODO: Update color scheme
     * @TODO: Update Icons
     * @TODO: Add commas and $ to Home Price
     * @TODO: Remove 'Mortgage Calculator' text and add to heading
     * @TODO: Do something with 'Settings'
     * @TODO: Plus button to add HOA, Mortgage insurance, Etc.
     * @TODO: Fix landscape mode
     * @TODO: Add email messaging
     */

    private var homePrice: EditText? = null
    private var downPaymentSpinner: Spinner? = null
    private var downPaymentAmount: TextView? = null
    private var loanLengthSpinner: Spinner? = null
    private var apr: EditText? = null
    private var mortgageCalculation: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        homePrice = findViewById(R.id.homePrice)
        downPaymentSpinner = findViewById(R.id.downPaymentSpinner)
        downPaymentAmount = findViewById(R.id.downPaymentAmount)
        loanLengthSpinner = findViewById(R.id.loanLengthSpinner)
        apr = findViewById(R.id.apr)
        mortgageCalculation = findViewById(R.id.mortgageCalculation)


        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }

        ArrayAdapter.createFromResource(this, R.array.downPaymentOptions, android.R.layout.simple_spinner_item).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) // Specify the layout to use when the list of choices appears
            downPaymentSpinner?.adapter = adapter // Apply the adapter to the spinner
        }

        ArrayAdapter.createFromResource(this, R.array.loanLengthSpinner, android.R.layout.simple_spinner_item).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            loanLengthSpinner?.adapter = adapter
        }

        homePrice?.addTextChangedListener( object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {}
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                calculateDownPayment()
                calculateMortgagePayment()
            }
        })

        downPaymentSpinner?.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {}
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                calculateDownPayment()
                calculateMortgagePayment()
            }
        }

        loanLengthSpinner?.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {}
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                calculateMortgagePayment()
            }
        }

        apr?.addTextChangedListener( object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {}
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                calculateMortgagePayment()
            }
        })

    }

    fun calculateDownPayment(){
        val homePriceAValue = homePrice?.text.toString().toBigDecimal()
        val percentDown = getBigDecimal(downPaymentSpinner?.selectedItem.toString())
        val downPayment = percentDown * homePriceAValue / BigDecimal(100)
        downPaymentAmount?.text = setDollarFormat(downPayment)
    }

    fun calculateMortgagePayment(){
        /** Formula =(B2-B8) * (D12*(1+D12)^(D11)) / (((1+D12)^D11)-1)
         * where D11 = total payments in months, D12 = APR in months
        */
        if (!isAprValid()){
            apr?.error = "Cannot have zero APR (Unfortunately)"
            return
        }

        val homePriceAValue = homePrice?.text.toString().toBigDecimal()
        val downPayment = getBigDecimal(downPaymentAmount?.text.toString())
        val loanLengthInMonths = loanLengthSpinner?.selectedItem.toString().toInt()* 12
        val aprInMonths = apr?.text.toString().toBigDecimal().divide(1200.toBigDecimal(), 5, RoundingMode.HALF_EVEN)

        val mortgagePayment = (homePriceAValue-downPayment) *
                (aprInMonths * ( BigDecimal.ONE + aprInMonths).pow(loanLengthInMonths)) /
                ((BigDecimal.ONE + aprInMonths).pow(loanLengthInMonths)- BigDecimal.ONE)

        mortgageCalculation?.text = setDollarFormat(mortgagePayment)
    }

    private fun isAprValid(): Boolean {
        val aprValue = apr?.text.toString()
        if (aprValue == "" || aprValue == "." ||
            aprValue.toBigDecimal().compareTo(BigDecimal.ZERO) == 0)
            return false

        return true
    }

    private fun setDollarFormat(amount: BigDecimal): String {
        return java.text.NumberFormat.getCurrencyInstance().format(amount)
    }

    private fun getBigDecimal(rawValue: String): BigDecimal{
        return rawValue.replace("$", "")
            .replace(",", "").replace("%","").toBigDecimal()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }
}
