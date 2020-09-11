package com.example.tip_calculator

import android.animation.ArgbEvaluator
import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
//import android.view.View
import android.widget.SeekBar
import android.widget.Toast
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_main.*

private const val TAG = "MainActivity"
private const val INITIAL_TIP_PERCENT = 15
private const val INITIAL_SPLIT = 1

class MainActivity : AppCompatActivity() {

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        seekBar.progress = INITIAL_TIP_PERCENT
        seekBarSplit.progress = INITIAL_SPLIT
        tipPercent.text = "Tip $INITIAL_TIP_PERCENT%"
        //Implementing seekbar listener for tip percentage
        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            @SuppressLint("SetTextI18n")
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                Log.i(TAG, "On progress changed $progress")
                tipPercent.text = "Tip $progress%"
                updateTipPercent(progress)
                calculateTipTotal()
                updateSplitBy(seekBarSplit.progress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }
        })
        //Implementing seekbar listener for split bill
        seekBarSplit.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
            @SuppressLint("SetTextI18n")
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                textViewSplitBy.text = "Split by $progress"
                updateSplitBy(progress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }
        })
        //Implementing text listener for initial bill amount
        editTextBase.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                Log.i(TAG, "After text changed $s")
                if("$s".startsWith("00")){
                    Toast.makeText(applicationContext,"Bill cannot start with two Zeros",Toast.LENGTH_SHORT).show()
                    editTextBase.setText("")
                }
                if ("$s".startsWith(".")){
                    //Toast.makeText(applicationContext,"Please type . after one Zero",Toast.LENGTH_SHORT).show()
                    editTextBase.setText("0.")
                    editTextBase.setSelection(editTextBase.text.length)
                }
                if ("$s".contains(".") && "$s".substring("$s".indexOf(".") + 1).length > 2) {
                    editTextBase.setText("$s".substring(0, "$s".length - 1))
                    editTextBase.setSelection(editTextBase.text.length)
                }
                if (editTextBase.text.length>=15){
                    Toast.makeText(applicationContext,"Max length reached",Toast.LENGTH_SHORT).show()
                }
                calculateTipTotal()
                updateSplitBy(INITIAL_SPLIT)
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })
        //Implementing button listener for reset button
        buttonReset.setOnClickListener {
            editTextBase.setText("")
            seekBar.progress = INITIAL_TIP_PERCENT
            seekBarSplit.progress = INITIAL_SPLIT
            tipAmount.text = ""
            totalAmount.text = ""
            textViewAmountPerPerson.text = ""
        }
        //Implementing button listener for +(plus) button
        buttonAdd.setOnClickListener {
            seekBar.progress++
        }
        //Implementing button listener for -(minus) button
        buttonSub.setOnClickListener {
            seekBar.progress--
        }

    }

    //Function to split bill with split number
    @SuppressLint("SetTextI18n")
    private fun updateSplitBy(SplitNo: Int) {
        if (totalAmount.text.isNotEmpty()) {
            val amount = totalAmount.text.toString().toDouble() / SplitNo
            textViewAmountPerPerson.text = "%.2f".format(amount)
        }
    }

    //Function to update tip percent based on seekbar input
    private fun updateTipPercent(tipPercentage: Int) {
        val color = ArgbEvaluator().evaluate(
            tipPercentage.toFloat() / seekBar.max,
            ContextCompat.getColor(this, R.color.colorWorstTip),
            ContextCompat.getColor(this, R.color.colorBestTip)
        ) as Int
        tipPercent.setTextColor(color)
    }

    //Function to calculate total tip and total bill amount
    @SuppressLint("SetTextI18n")
    private fun calculateTipTotal() {
        //Calculate tip and total amount by getting values
        if (editTextBase.text.isNotEmpty()) {
            val originalAmount = editTextBase.text.toString().toDouble()
            val tipPercentage = seekBar.progress
            val tip = originalAmount * tipPercentage / 100
            val total = originalAmount + tip
            tipAmount.text = "%.2f".format(tip)
            totalAmount.text = "%.2f".format(total)

        } else {
            tipAmount.text = ""
            totalAmount.text = ""
            textViewAmountPerPerson.text = ""
            return
        }
    }
}
