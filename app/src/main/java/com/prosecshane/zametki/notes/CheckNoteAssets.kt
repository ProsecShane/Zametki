package com.prosecshane.zametki.notes

import android.content.Context
import android.text.Editable
import android.text.InputType
import android.text.TextUtils
import android.text.TextWatcher
import android.view.Gravity
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.core.view.isInvisible
import androidx.core.widget.addTextChangedListener
import com.prosecshane.zametki.R

class UncheckedRow(context: Context) : GridLayout(context) {
    val swapUp = ImageView(this.context)
    val swapDown = ImageView(this.context)
    val editText = EditText(this.context)
    val checkBox = CheckBox(this.context)
    val deleteButton = ImageView(this.context)

    init {
        this.orientation = HORIZONTAL
        this.columnCount = 4
        this.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )

        val swapUpLayoutParams = LayoutParams(spec(0), spec(0, 1f))
        swapUpLayoutParams.width = LayoutParams.WRAP_CONTENT
        swapUpLayoutParams.height = LayoutParams.WRAP_CONTENT
        this.swapUp.layoutParams = swapUpLayoutParams
        val eightDP = intToDP(8, this.context)
        this.swapUp.setPadding(eightDP, eightDP, eightDP, eightDP)
        this.swapUp.setColorFilter(ContextCompat.getColor(getContext(), R.color.orange_main))
        this.swapUp.setImageResource(android.R.drawable.arrow_up_float)
        this.addView(this.swapUp)

        val editTextLayoutParams = LayoutParams(spec(0, 2), spec(1, 100f))
        editTextLayoutParams.width = 0
        editTextLayoutParams.height = LayoutParams.WRAP_CONTENT
        editTextLayoutParams.setGravity(Gravity.FILL)
        this.editText.layoutParams = editTextLayoutParams
        this.editText.maxLines = 1
        this.editText.inputType = InputType.TYPE_CLASS_TEXT
        this.editText.setPadding(
            intToDP(10, this.context), 0,
            intToDP(10, this.context), 0
        )
        this.editText.hint = "Цель"
        this.addView(this.editText)

        val checkBoxLayoutParams = LayoutParams(spec(0, 2), spec(2, 1f))
        checkBoxLayoutParams.setGravity(Gravity.FILL)
        this.checkBox.layoutParams = checkBoxLayoutParams
        this.addView(this.checkBox)

        val deleteLayoutParams = LayoutParams(spec(0, 2), spec(3, 1f))
        deleteLayoutParams.width = LayoutParams.WRAP_CONTENT
        deleteLayoutParams.height = LayoutParams.WRAP_CONTENT
        deleteLayoutParams.setGravity(Gravity.FILL)
        this.deleteButton.layoutParams = deleteLayoutParams
        this.deleteButton.setColorFilter(ContextCompat.getColor(getContext(), R.color.orange_main))
        this.deleteButton.setImageResource(android.R.drawable.ic_delete)
        this.addView(this.deleteButton)

        val swapDownLayoutParams = LayoutParams(spec(1), spec(0, 1f))
        swapDownLayoutParams.width = LayoutParams.WRAP_CONTENT
        swapDownLayoutParams.height = LayoutParams.WRAP_CONTENT
        this.swapDown.layoutParams = swapDownLayoutParams
        this.swapDown.setPadding(eightDP, eightDP, eightDP, eightDP)
        this.swapDown.setColorFilter(ContextCompat.getColor(getContext(), R.color.orange_main))
        this.swapDown.setImageResource(android.R.drawable.arrow_down_float)
        this.addView(this.swapDown)
    }
}

class UncheckedSegment(context: Context, val note: CheckNote) : LinearLayout(context) {
    private val adder = ImageView(this.context)
    lateinit var checkedSegment: CheckedSegment

    init {
        this.orientation = VERTICAL
        this.update()
        this.adder.setColorFilter(ContextCompat.getColor(this.context, R.color.orange_main))
        this.adder.setImageResource(android.R.drawable.ic_input_add)
        this.adder.setOnClickListener {
            this.note.appendUnchecked("Цель ${this.note.unchecked.size + 1}")
            this.update()
        }
    }

    fun update() {
        this.removeAllViews()
        for (i in 0 until this.note.unchecked.size) {
            val row = UncheckedRow(context)
            val rowLayoutParams = LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT
            )
            rowLayoutParams.setMargins(0, 0, 0, intToDP(10, this.context))
            row.layoutParams = rowLayoutParams
            row.editText.setText(this.note.unchecked[i])
            if (i == 0) { row.swapUp.isInvisible = true } else {
                row.swapUp.setOnClickListener {
                    this.note.swapWithHigher(i)
                    this.update()
                }
            }
            if (i == note.unchecked.size - 1) { row.swapDown.isInvisible = true } else {
                row.swapDown.setOnClickListener {
                    this.note.swapWithLower(i)
                    this.update()
                }
            }
            row.editText.addTextChangedListener(object: TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
                override fun afterTextChanged(p0: Editable?) {}
                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    note.changeUncheckedLine(i, p0.toString())
                }
            })
            row.checkBox.setOnCheckedChangeListener { _, _ ->
                this.note.checkUnchecked(i)
                this.update()
                this.checkedSegment.update()
            }
            row.deleteButton.setOnClickListener {
                this.note.deleteUnchecked(i)
                this.update()
            }
            this.addView(row)
        }
        this.addView(this.adder)
    }
}

class CheckedRow(context: Context) : GridLayout(context) {
    val textView = TextView(this.context)
    val checkBox = CheckBox(this.context)
    val deleteButton = ImageView(this.context)

    init {
        this.orientation = HORIZONTAL
        this.columnCount = 3
        this.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )

        val textViewLayoutParams = LayoutParams(spec(0), spec(0, 100f))
        textViewLayoutParams.width = 0
        textViewLayoutParams.height = LayoutParams.WRAP_CONTENT
        textViewLayoutParams.setGravity(Gravity.CENTER_VERTICAL)
        this.textView.layoutParams = textViewLayoutParams
        this.textView.textSize = 18f
        this.textView.setPadding(
            intToDP(10, this.context), 0,
            intToDP(10, this.context), 0
        )
        this.textView.maxLines = 1
        this.textView.ellipsize = TextUtils.TruncateAt.END
        this.addView(this.textView)

        val checkBoxLayoutParams = LayoutParams(spec(0), spec(1, 1f))
        checkBoxLayoutParams.setGravity(Gravity.FILL)
        this.checkBox.layoutParams = checkBoxLayoutParams
        this.addView(this.checkBox)

        val deleteLayoutParams = LayoutParams(spec(0), spec(2, 1f))
        deleteLayoutParams.width = LayoutParams.WRAP_CONTENT
        deleteLayoutParams.height = LayoutParams.WRAP_CONTENT
        deleteLayoutParams.setGravity(Gravity.FILL)
        this.deleteButton.layoutParams = deleteLayoutParams
        this.deleteButton.setColorFilter(ContextCompat.getColor(getContext(), R.color.orange_main))
        this.deleteButton.setImageResource(android.R.drawable.ic_delete)
        this.addView(this.deleteButton)
    }
}

class CheckedSegment(context: Context, val note: CheckNote) : LinearLayout(context) {
    lateinit var uncheckedSegment: UncheckedSegment

    init {
        this.orientation = VERTICAL
        this.update()
    }

    fun update() {
        this.removeAllViews()
        for (i in 0 until this.note.getChecked().size) {
            val row = CheckedRow(context)
            val rowLayoutParams = LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT
            )
            rowLayoutParams.setMargins(0, intToDP(10, this.context), 0, 0)
            row.layoutParams = rowLayoutParams
            row.textView.text = this.note.getChecked()[i]
            row.checkBox.isChecked = true

            row.checkBox.setOnCheckedChangeListener { _, _ ->
                this.note.uncheckChecked(i)
                this.update()
                this.uncheckedSegment.update()
            }
            row.deleteButton.setOnClickListener {
                this.note.deleteChecked(i)
                this.update()
            }
            this.addView(row)
        }
    }
}
