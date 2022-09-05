package com.prosecshane.zametki.notes

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.core.view.children
import androidx.core.view.isGone
import androidx.documentfile.provider.DocumentFile
import androidx.loader.content.CursorLoader
import com.prosecshane.zametki.R
import java.lang.Integer.min
import java.util.*

fun intToDP(value: Int, context: Context): Int {
    return (value * context.resources.displayMetrics.density).toInt()
}

open class NoteView(context: Context?, open val note: Note) : LinearLayout(context) {
    val background = LinearLayout(this.context)
    val title = TextView(this.context)
    lateinit var preview: View

    init {
        val outsideLayoutParams = LayoutParams(
            LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
        val OLPMargin = intToDP(15, this.context)
        outsideLayoutParams.setMargins(OLPMargin, OLPMargin, OLPMargin, OLPMargin)
        this.layoutParams = outsideLayoutParams
        this.orientation = HORIZONTAL
        this.setBackgroundResource(R.drawable.rounded_outside)

        val backgroundLayoutParams = LayoutParams(
            LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
        val BLPMargin = intToDP(10, this.context)
        backgroundLayoutParams.setMargins(BLPMargin, BLPMargin, BLPMargin, BLPMargin)
        this.background.layoutParams = backgroundLayoutParams
        this.background.orientation = VERTICAL
        this.background.setBackgroundResource(R.drawable.rounded_inside)

        val titleLayoutParams = LayoutParams(
            LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
        titleLayoutParams.setMargins(OLPMargin, OLPMargin, OLPMargin, OLPMargin)
        this.title.layoutParams = titleLayoutParams
        this.title.textSize = 30f
        this.title.ellipsize = TextUtils.TruncateAt.END
        this.title.text = "\t" + if (this.note.title != "") { this.note.title } else { "Без названия" }
        this.title.maxLines = 1

        this.background.addView(this.title)
        this.addView(background)
        this.typeDependedInit()
    }

    open fun typeDependedInit() {
        assert(note.typeOfNote == "Text")
        this.preview = TextView(this.context)
        val previewLayoutParams = LayoutParams(
            LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
        val PLPMargin = intToDP(15, this.context)
        previewLayoutParams.setMargins(PLPMargin, PLPMargin, PLPMargin, PLPMargin)
        (this.preview as TextView).also {
            it.layoutParams = previewLayoutParams
            it.textSize = 20f
            it.ellipsize = TextUtils.TruncateAt.END
            it.text = this.note.content
            it.maxLines = 15
            this.background.addView(it)
        }
        this.title.setCompoundDrawablesRelativeWithIntrinsicBounds(
            R.drawable.baseline_text_fields_24, 0, 0, 0)
    }
}

class CheckNoteView(context: Context?, note: CheckNote) : NoteView(context, note) {
    override fun typeDependedInit() {
        assert(note.typeOfNote == "Check")

        this.preview = LinearLayout(this.context)
        val previewLayoutParams = LayoutParams(
            LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
        val PLPMargin = intToDP(15, this.context)
        previewLayoutParams.setMargins(PLPMargin, PLPMargin, PLPMargin, PLPMargin)
        (this.preview as LinearLayout).also {
            it.layoutParams = previewLayoutParams
            it.orientation = VERTICAL
            var demoData = (this.note as CheckNote).unchecked
            demoData = demoData.slice(0..min(4, demoData.size - 1)).toMutableList()
            var first = true
            for (elem in demoData) {
                if (first) { first = false } else {
                    val divider = View(this.context)
                    val dividerLayoutParams = LayoutParams(
                        LayoutParams.MATCH_PARENT, intToDP(2, this.context)
                    )
                    dividerLayoutParams.setMargins(
                        intToDP(5, this.context), intToDP(5, this.context),
                        intToDP(5, this.context), intToDP(5, this.context)
                    )
                    divider.layoutParams = dividerLayoutParams
                    divider.setBackgroundColor(Color.GRAY)
                    it.addView(divider)
                }
                val rowElem = CheckedRow(this.context)
                rowElem.deleteButton.isGone = true
                rowElem.textView.text = if (elem != "") { elem } else { "Пустая цель" }
                it.addView(rowElem)
            }
            this.background.addView(it)
        }
        this.title.setCompoundDrawablesRelativeWithIntrinsicBounds(
            R.drawable.baseline_done_24, 0, 0, 0)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(
            Int.MAX_VALUE shr 2, MeasureSpec.AT_MOST))
    }

    override fun setOnClickListener(l: OnClickListener?) {
        super.setOnClickListener(l)
        val button = Button(this.context)
        button.setOnClickListener(l)
        for (child in (this.preview as LinearLayout).children) { if (child is GridLayout) {
            for (child_ in child.children) { if (child_ is CheckBox) {
                child_.setOnCheckedChangeListener { _, _ ->
                    child_.isChecked = false
                    child_.jumpDrawablesToCurrentState()
                    button.performClick()
                }
            }}
        }}
    }
}

class AlarmNoteView(context: Context?, note: AlarmNote) : NoteView(context, note) {
    override fun typeDependedInit() {
        assert(note.typeOfNote == "Alarm")
        this.preview = TextView(this.context)
        val previewLayoutParams = LayoutParams(
            LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
        val PLPMargin = intToDP(15, this.context)
        previewLayoutParams.setMargins(PLPMargin, PLPMargin, PLPMargin, PLPMargin)
        (this.preview as TextView).also {
            it.layoutParams = previewLayoutParams
            it.textSize = 20f
            it.ellipsize = TextUtils.TruncateAt.END
            it.text = "\t●\tСработа${if (Calendar.getInstance().timeInMillis >=
                (this.note as AlarmNote).alarmTime.timeInMillis) {
                "ло" } else { "ет" }} в\n\t${this.note.getAlarmTimeAsString()}\n\n" +
                    if (this.note.content != "") { this.note.content } else { "Без описания" }
            it.maxLines = 15
            this.background.addView(it)
        }
        this.title.setCompoundDrawablesRelativeWithIntrinsicBounds(
            android.R.drawable.ic_lock_idle_alarm, 0, 0, 0)
    }
}

class ImageNoteView(context: Context?, note: ImageNote) : NoteView(context, note) {
    override fun typeDependedInit() {
        assert(note.typeOfNote == "Image")
        this.preview = ImageView(this.context)
        val previewLayoutParams = LayoutParams(
            LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
        val PLPMargin = intToDP(15, this.context)
        previewLayoutParams.setMargins(PLPMargin, PLPMargin, PLPMargin, PLPMargin)
        (this.preview as ImageView).also {
            it.layoutParams = previewLayoutParams
            it.adjustViewBounds = true
            this.updateImage()
            this.background.addView(it)
        }
        this.title.setCompoundDrawablesRelativeWithIntrinsicBounds(
            R.drawable.baseline_image_20, 0, 0, 0)
    }

    fun updateImage() {
        (this.preview as ImageView).also {
            if (DocumentFile.fromSingleUri(this.context,
                    Uri.parse(this.note.content))?.exists() != true) {
                it.setImageResource(R.drawable.no_image)
            } else { it.setImageURI(Uri.parse(this.note.content)) }

            val bitmap = it.drawable.toBitmap()
            val width = bitmap.width
            val height = bitmap.height
            if (width > height) {
                it.setImageBitmap(Bitmap.createBitmap(
                    bitmap,
                    (width - height) / 2, 0,
                    height, height
                ))
            } else {
                it.setImageBitmap(Bitmap.createBitmap(
                    bitmap,
                    0, (height - width) / 2,
                    width, width
                ))
            }
        }
    }
}
