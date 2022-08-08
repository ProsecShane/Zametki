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
import com.prosecshane.zametki.MainActivity
import com.prosecshane.zametki.databinding.FragmentMainBinding
import com.prosecshane.zametki.notes.*


class MainFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        val binding = FragmentMainBinding.inflate(inflater, container, false)

        val textNote = Note("Text note", "String of content")
        val noteView = NoteView(context, textNote)
        binding.notesColumn.addView(noteView)

        noteView.setOnClickListener {
            (activity as MainActivity).editNote(textNote)
        }

        val checkNote = CheckNote("Check note")
        checkNote.deleteUnchecked(0)
        checkNote.appendUnchecked("Item 1")
        checkNote.appendUnchecked("Item 2")
        checkNote.appendUnchecked("Item 3")
        val checkNoteView = CheckNoteView(context, checkNote)
        binding.notesColumn.addView(checkNoteView)

        checkNoteView.setOnClickListener {
            (activity as MainActivity).editNote(checkNote)
        }

        val alarmNote = AlarmNote("Alarm note", "String of content")
        alarmNote.setAlarmTime(1999, 10, 5, 14, 53)
        val alarmNoteView = AlarmNoteView(context, alarmNote)
        binding.notesColumn.addView(alarmNoteView)

        alarmNoteView.setOnClickListener {
            (activity as MainActivity).editNote(alarmNote)
        }

        val imageNote = ImageNote("Image note", "")
        val imageNoteView = ImageNoteView(context, imageNote)
        binding.notesColumn.addView(imageNoteView)

        imageNoteView.setOnClickListener {
            (activity as MainActivity).editNote(imageNote)
        }

//        val note = CheckNote(); binding.mainText.text = Gson().toJson(note)
//        activity?.openFileInput("jsonNotes").use {val allNotes = it?.readBytes().toString()}
        return binding.root
    }
}
