package com.prosecshane.zametki

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Gravity
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.widget.addTextChangedListener
import androidx.documentfile.provider.DocumentFile
import com.google.android.material.imageview.ShapeableImageView
import com.prosecshane.zametki.notes.*
import java.util.*

class NoteActivity : AppCompatActivity() {
    private fun updateImage(imageView: ShapeableImageView, uri: String) {
        if (DocumentFile.fromSingleUri(this, Uri.parse(uri))?.exists() != true) {
            imageView.setImageResource(R.drawable.no_image)
        } else {
            imageView.setImageURI(Uri.parse(uri))
        }
        imageView.shapeAppearanceModel =
            imageView.shapeAppearanceModel
                .toBuilder()
                .setAllCornerSizes(10f)
                .build()
    }

    fun saveNote(note: Note) {
        this.openFileOutput(
            note.id.toString(),
            Context.MODE_PRIVATE
        ).write(note.toJson().toByteArray())
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_note)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Редактировать заметку"
        supportActionBar?.setBackgroundDrawable(ColorDrawable(
                resources.getColor(R.color.orange_main, this.theme)
        ))

        val icon = findViewById<ImageView>(R.id.editIcon)
        val title = findViewById<EditText>(R.id.editTitle)
        val contentLayout = findViewById<LinearLayout>(R.id.editContentLayout)
        val note = noteFromJsonWithType(
            (intent.extras?.get("bundle") as Bundle).get("note") as String
        )

        title.setText(note.title)
        title.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(p0: Editable?) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                note.title = p0.toString()
                saveNote(note)
            }
        })
        when (note.typeOfNote) {
            "Text" -> {
                icon.setImageResource(R.drawable.baseline_text_fields_24)
                val content = EditText(this)
                val contentLayoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT
                )
                content.layoutParams = contentLayoutParams
                content.gravity = Gravity.TOP
                content.textSize = 20f
                content.setText(note.content)
                content.addTextChangedListener(object: TextWatcher {
                    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
                    override fun afterTextChanged(p0: Editable?) {}
                    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                        note.content = p0.toString()
                        saveNote(note)
                    }
                })
                contentLayout.addView(content)
            }
            "Check" -> {
                icon.setImageResource(R.drawable.baseline_done_24)

                val scrollView = ScrollView(this)
                scrollView.layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT
                )
                contentLayout.addView(scrollView)

                val checkContentLayout = LinearLayout(this)
                checkContentLayout.layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT
                )
                checkContentLayout.orientation = LinearLayout.VERTICAL
                scrollView.addView(checkContentLayout)

                val unchecked = UncheckedSegment(this, note as CheckNote, this)
                checkContentLayout.addView(unchecked)

                val divider = View(this)
                val dividerLayoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    intToDP(3, this)
                )
                dividerLayoutParams.setMargins(
                    intToDP(10, this),
                    intToDP(10, this),
                    intToDP(10, this),
                    intToDP(5, this)
                )
                divider.layoutParams = dividerLayoutParams
                divider.setBackgroundColor(resources.getColor(R.color.orange_main, this.theme))
                checkContentLayout.addView(divider)

                val tv = TextView(this)
                val tvLayoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                tvLayoutParams.setMargins(
                    0, intToDP(10, this), 0, 0
                )
                tv.layoutParams = tvLayoutParams
                tv.textSize = 18f
                tv.text = "\tВыполненное:"
                tv.setCompoundDrawablesRelativeWithIntrinsicBounds(
                    R.drawable.baseline_done_24, 0, 0, 0
                )
                checkContentLayout.addView(tv)

                val checked = CheckedSegment(this, note, this)
                checkContentLayout.addView(checked)

                unchecked.checkedSegment = checked
                checked.uncheckedSegment = unchecked
            }
            "Alarm" -> {
                icon.setImageResource(android.R.drawable.ic_lock_idle_alarm)
                val textView = TextView(this)
                val textViewParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                textView.layoutParams = textViewParams
                textView.textSize = 20f
                textView.text = "Время напоминания:"
                contentLayout.addView(textView)

                val ten = (10 * this.resources.displayMetrics.density).toInt()
                val timePicker = Button(this)
                val timePickerParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                timePickerParams.setMargins(0, ten,0, ten)
                timePicker.layoutParams = timePickerParams
                timePicker.textSize = 26f
                timePicker.text = (note as AlarmNote).getAlarmTimeAsString()
                timePicker.isAllCaps = false
                contentLayout.addView(timePicker)

                val content = EditText(this)
                val contentLayoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT
                )
                content.layoutParams = contentLayoutParams
                content.gravity = Gravity.TOP
                content.textSize = 20f
                content.setText(note.content)
                content.addTextChangedListener(object: TextWatcher {
                    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
                    override fun afterTextChanged(p0: Editable?) {}
                    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                        note.content = p0.toString()
                        saveNote(note)
                    }
                })
                contentLayout.addView(content)

                val c = Calendar.getInstance()
                val timePickerDialog = TimePickerDialog(this,
                    { _, hour, minute ->
                        note.setAlarmTime(
                            note.alarmTime.get(Calendar.YEAR),
                            note.alarmTime.get(Calendar.MONTH),
                            note.alarmTime.get(Calendar.DAY_OF_MONTH),
                            hour, minute)
                        timePicker.text = note.getAlarmTimeAsString()
                        saveNote(note)
                    }, c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), true)
                val datePickerDialog = DatePickerDialog(this,
                    { _, year, month, day ->
                        note.setAlarmTime(year, month, day,
                            note.alarmTime.get(Calendar.HOUR_OF_DAY),
                            note.alarmTime.get(Calendar.MINUTE))
                        timePicker.text = note.getAlarmTimeAsString()
                        timePickerDialog.show()
                    }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH))
                timePicker.setOnClickListener { datePickerDialog.show() }
            }
            "Image" -> {
                icon.setImageResource(R.drawable.baseline_image_20)
                val content = ShapeableImageView(this)
                content.adjustViewBounds = true
                this.updateImage(content, note.content)

                val getImagePath = registerForActivityResult(
                    ActivityResultContracts.StartActivityForResult()
                ) { result ->
                    if (result.resultCode == Activity.RESULT_OK) {
                        note.content = result.data?.data.toString()
                        this.updateImage(content, note.content)
                    }
                }
                val requestPermission = registerForActivityResult(
                    ActivityResultContracts.RequestPermission()
                ) {isGranted ->
                    if (isGranted) {
                        val getterIntent = Intent(Intent.ACTION_GET_CONTENT)
                        getterIntent.type = "image/*"
                        val chooserIntent = Intent.createChooser(
                            getterIntent,
                            "Выберите изображение"
                        )
                        getImagePath.launch(chooserIntent)
                    } else {
                        Toast.makeText(this,
                            "Запрещен выбор файлов. Разрешите в настройках приложения",
                            Toast.LENGTH_LONG).show()
                    }
                }
                val alertBuilder = AlertDialog.Builder(this)
                alertBuilder.setTitle("Изображение")
                alertBuilder.setMessage("Выберите действие для изображения")
                alertBuilder.setPositiveButton("Поменять") { _, _ ->
                    requestPermission.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    saveNote(note)
                }
                alertBuilder.setNegativeButton("Удалить") { _, _ ->
                    note.content = ""
                    this.updateImage(content, note.content)
                    saveNote(note)
                }
                alertBuilder.setNeutralButton("Отмена") { _, _ -> }
                (content as View).setOnClickListener { alertBuilder.create().show() }
                contentLayout.addView(content)
            }
        }
        note.updateLastUsed()
        saveNote(note)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) { android.R.id.home -> { finish(); return true } }
        return super.onContextItemSelected(item)
    }
}
