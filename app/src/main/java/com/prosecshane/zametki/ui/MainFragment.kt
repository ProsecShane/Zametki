package com.prosecshane.zametki.ui

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.prosecshane.zametki.databinding.FragmentMainBinding
import com.prosecshane.zametki.notes.*


class MainFragment : Fragment() {
    val imageNote = ImageNote("Image note", "")
    lateinit var imageNoteView: ImageNoteView

    private val requestPermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) {isGranted ->
        if (isGranted) {
            val getterIntent = Intent(Intent.ACTION_GET_CONTENT)
            getterIntent.type = "image/*"
            val chooserIntent = Intent.createChooser(getterIntent, "Выберите изображение")
            this.getImagePath.launch(chooserIntent)
        } else {
            Toast.makeText(context,
                "Запрещен выбор файлов. Разрешите в настройках приложения",
                Toast.LENGTH_SHORT).show()
        }
    }

    private val getImagePath = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            this.imageNote.content = result.data?.data.toString()
            this.imageNoteView.updateImage()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        val binding = FragmentMainBinding.inflate(inflater, container, false)

        val textNote = Note("Text note", "String of content")
        val noteView = NoteView(context, textNote)
        binding.notesColumn.addView(noteView)

        val checkNote = CheckNote("Check note")
        checkNote.deleteUnchecked(0)
        checkNote.appendUnchecked("Item 1")
        checkNote.appendUnchecked("Item 2")
        checkNote.appendUnchecked("Item 3")
        val checkNoteView = CheckNoteView(context, checkNote)
        binding.notesColumn.addView(checkNoteView)

        val alarmNote = AlarmNote("Alarm note", "String of content")
        alarmNote.setAlarmTime(1999, 10, 5, 14, 53)
        val alarmNoteView = AlarmNoteView(context, alarmNote)
        binding.notesColumn.addView(alarmNoteView)

        val button = Button(context)
        button.text = "Debug Button: File Selection for ImageNoteView"
        button.setOnClickListener {
            this.requestPermission.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
        binding.notesColumn.addView(button)

        this.imageNoteView = ImageNoteView(context, this.imageNote)
        binding.notesColumn.addView(this.imageNoteView)

//        val note = CheckNote(); binding.mainText.text = Gson().toJson(note)
//        activity?.openFileInput("jsonNotes").use {val allNotes = it?.readBytes().toString()}

        return binding.root
    }
}
