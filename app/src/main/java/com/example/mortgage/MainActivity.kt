package com.example.mortgage

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.View.OnFocusChangeListener
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main.*
import java.math.BigDecimal
import java.math.RoundingMode


class MainActivity : AppCompatActivity() {

    /**
     * TODO: Plus button to add HOA, Mortgage insurance, Etc.
     * TODO: Email messaging
     * TODO: Do something with 'Settings' in heading (text resizing)
     * TODO: Add commas and $ to Home Price
     * TODO: Put math calcs on a seperate thread:
     * https://stackoverflow.com/questions/14678593/the-application-may-be-doing-too-much-work-on-its-main-thread/21126690
     * TODO: Fix landscape mode
     * TODO: Work on backup preferences in AndroidManifest.xml
     */

    private var homePrice: EditText? = null
    private var downPaymentSpinner: Spinner? = null
    private var downPaymentAmount: TextView? = null
    private var loanLengthRadioGroup: RadioGroup? = null
    private var loanLength = 30
    private var apr: EditText? = null
    private var mortgageCalculation: TextView? = null
    private var adView: AdView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        homePrice = findViewById(R.id.homePrice)
        setOnFocusChangeListener(homePrice)
        downPaymentSpinner = findViewById(R.id.downPaymentSpinner)
        downPaymentAmount = findViewById(R.id.downPaymentAmount)
        loanLengthRadioGroup = findViewById(R.id.loanLengthRadioGroup)
        apr = findViewById(R.id.apr)
        setOnFocusChangeListener(apr)
        mortgageCalculation = findViewById(R.id.mortgageCalculation)

        createListeners()
        createAd()
    }

    private fun createListeners(){
        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }

        ArrayAdapter.createFromResource(this, R.array.downPaymentOptions, android.R.layout.simple_spinner_item).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) // Specify the layout to use when the list of choices appears
            downPaymentSpinner?.adapter = adapter // Apply the adapter to the spinner
            downPaymentSpinner?.setSelection(adapter.getPosition("20%"))
        }

        homePrice?.addTextChangedListener( object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {}
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                calculateDownPayment()
                calculateMortgagePayment()

                // TODO: try to put this in the afterText change. The problem is cursor goes to end
                // of line when coded as is
//                if(!s.toString().equals(current)){
//                    homePrice?.removeTextChangedListener(this)
//
//                    val formatted = setDollarFormat(getBigDecimal(s.toString()))
//
////                    current = formatted
//                    homePrice?.setText(formatted)
//                    homePrice?.setSelection(formatted.length)
//                    homePrice?.addTextChangedListener(this)
//                }

            }
        })

        downPaymentSpinner?.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {}
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                calculateDownPayment()
                calculateMortgagePayment()
            }
        }

        loanLengthRadioGroup?.setOnCheckedChangeListener { _, checkedId ->
            loanLength = when (findViewById<RadioButton>(checkedId).tag.toString() == "15"){
                true -> 15
                false -> 30
            }
            calculateMortgagePayment()
        }

        apr?.addTextChangedListener( object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {}
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                calculateMortgagePayment()
            }
        })
    }

    private fun setOnFocusChangeListener(editText: EditText?) {
        // Automatically hide keyboard on focus loss
        editText?.onFocusChangeListener = OnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(editText?.windowToken, 0)
            }
        }
    }

    private fun calculateDownPayment(){
        if( !isMortgageAmountValid(homePrice?.text.toString())) {
            homePrice?.error = "Enter non-zero mortgage"
            return
        }

        val homePriceAValue = getBigDecimal(homePrice?.text.toString())
        val percentDown = getBigDecimal(downPaymentSpinner?.selectedItem.toString())
        val downPayment = percentDown * homePriceAValue / BigDecimal(100)

        downPaymentAmount?.text = setDollarFormat(downPayment)
    }

    private fun calculateMortgagePayment(){
        /** Formula =(B2-B8) * (D12*(1+D12)^(D11)) / (((1+D12)^D11)-1)
         * where D11 = total payments in months, D12 = APR in months
        */
        if (!isAprValid(apr?.text.toString())){
            apr?.error = "Enter non-zero APR"
            return
        }
        if( !isMortgageAmountValid(homePrice?.text.toString())) {
            homePrice?.error = "Enter non-zero mortgage"
            return
        }

        val homePriceAValue = getBigDecimal(homePrice?.text.toString())
        val downPayment = getBigDecimal(downPaymentAmount?.text.toString())
        val loanLengthInMonths = loanLength * 12
        val aprInMonths = apr?.text.toString().toBigDecimal().divide(1200.toBigDecimal(), 5, RoundingMode.HALF_EVEN)

        val mortgagePayment = (homePriceAValue-downPayment) *
                (aprInMonths * ( BigDecimal.ONE + aprInMonths).pow(loanLengthInMonths)) /
                ((BigDecimal.ONE + aprInMonths).pow(loanLengthInMonths)- BigDecimal.ONE)

        mortgageCalculation?.text = setDollarFormat(mortgagePayment)
    }

    private fun createAd(){
        adView = findViewById(R.id.adView)

        val adRequest = AdRequest.Builder()
            .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
            .build()

        adView?.loadAd(adRequest)
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
