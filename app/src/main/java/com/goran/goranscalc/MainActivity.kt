
package com.goran.goranscalc

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.util.Locale

enum class Mode {
    NoMode, Add, Subtract, Multiply, Divide
}

class MainActivity : AppCompatActivity() {

    var lastWasMode = false
    var currentMode = Mode.NoMode
    var labelText = ""
    var savedNumber = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(
                systemBars.left, systemBars.top,
                systemBars.right, systemBars.bottom
            )
            insets
        }

        setup()
    }

    fun setup() {
        val buttons: Array<Button> = arrayOf(
            findViewById(R.id.button0),
            findViewById(R.id.button1),
            findViewById(R.id.button2),
            findViewById(R.id.button3),
            findViewById(R.id.button4),
            findViewById(R.id.button5),
            findViewById(R.id.button6),
            findViewById(R.id.button7),
            findViewById(R.id.button8),
            findViewById(R.id.button9)
        )

        for (i in buttons.indices) {
            buttons[i].setOnClickListener {
                pressedNumber(i)
            }
        }

        findViewById<Button>(R.id.button_plus)
            .setOnClickListener { changeMode(Mode.Add) }

        findViewById<Button>(R.id.button_minus)
            .setOnClickListener { changeMode(Mode.Subtract) }

        findViewById<Button>(R.id.button_multiply)
            .setOnClickListener { changeMode(Mode.Multiply) }

        findViewById<Button>(R.id.button_divide)
            .setOnClickListener { changeMode(Mode.Divide) }

        findViewById<Button>(R.id.button_decimal)
            .setOnClickListener { pressedDecimal() }

        findViewById<Button>(R.id.button_c)
            .setOnClickListener { pressedClear() }

        findViewById<Button>(R.id.button_equal)
            .setOnClickListener { pressedEquals() }
    }

    fun pressedEquals() {
        if (lastWasMode || labelText.isEmpty()) {
            return
        }

        val labelNumber =
            labelText.replace(',', '.').toDoubleOrNull() ?: return

        when (currentMode) {
            Mode.NoMode -> return
            Mode.Add -> savedNumber += labelNumber
            Mode.Subtract -> savedNumber -= labelNumber
            Mode.Multiply -> savedNumber *= labelNumber
            Mode.Divide -> {
                if (labelNumber == 0.0) {
                    pressedClear()
                    findViewById<TextView>(R.id.textView).text = "Fel: /0"
                    return
                }
                savedNumber /= labelNumber
            }
        }

        currentMode = Mode.NoMode
        labelText = String.format(Locale("sv", "SE"), "%.2f", savedNumber)
        updateLabelText()
        lastWasMode = true
    }

    fun pressedClear() {
        lastWasMode = false
        currentMode = Mode.NoMode
        labelText = ""
        savedNumber = 0.0
        findViewById<TextView>(R.id.textView).text = "0"
    }

    fun pressedNumber(num: Int) {
        val numString = num.toString()

        if (lastWasMode) {
            lastWasMode = false
            labelText = ""
        }

        if (labelText == "0") {
            labelText = ""
        }

        if (labelText.length >= 12) {
            return
        }

        labelText = "$labelText$numString"
        updateLabelText()
    }

    fun pressedDecimal() {
        if (lastWasMode) {
            lastWasMode = false
            labelText = ""
        }

        if (labelText.contains(",") || labelText.length >= 12) {
            return
        }

        if (labelText.isEmpty()) {
            labelText = "0"
        }

        labelText += ","
        updateLabelText()
    }

    fun updateLabelText() {
        val tview = findViewById<TextView>(R.id.textView)

        if (labelText.isEmpty()) {
            tview.text = "0"
            return
        }

        if (currentMode == Mode.NoMode) {
            savedNumber =
                labelText.replace(',', '.').toDoubleOrNull() ?: 0.0
        }

        tview.text = labelText
    }

    fun changeMode(mode: Mode) {
        if (labelText.isEmpty()) {
            return
        }

        currentMode = mode
        lastWasMode = true
    }
}
