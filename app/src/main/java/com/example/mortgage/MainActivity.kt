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


class MainActivity : AppCompatActivity() {

    private var homePrice: EditText? = null
    private var downPaymentAmount: TextView? = null
    private var downPaymentSpinner: Spinner? = null
    private var loanLengthSpinner: Spinner? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        homePrice = findViewById(R.id.homePrice)
        downPaymentAmount = findViewById(R.id.downPaymentAmount)
        downPaymentSpinner = findViewById(R.id.downPaymentSpinner)
        loanLengthSpinner = findViewById(R.id.loanLengthSpinner)


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

        downPaymentSpinner?.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {}
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                calculateDownPayment()
            }
        }

        loanLengthSpinner?.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {}
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {}
        }

        homePrice?.addTextChangedListener( object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {}
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                calculateDownPayment()
            }
        })
    }

    fun calculateDownPayment(){
        val percentDown = downPaymentSpinner?.selectedItem.toString().replace("%", "").toBigDecimal()
        val homePriceAValue = homePrice?.text.toString().toBigDecimal()
        val downPayment = percentDown * homePriceAValue / BigDecimal(100)
        downPaymentAmount?.text = downPayment.toString()
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
