package com.prosecshane.zametki.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.prosecshane.zametki.MainActivity
import com.prosecshane.zametki.R
import com.prosecshane.zametki.databinding.FragmentMainBinding
import com.prosecshane.zametki.notes.*
import java.lang.Exception

class MainFragment : Fragment() {
    private lateinit var binding: FragmentMainBinding

    fun updateNoteColumn() {
        (activity as MainActivity).deleteMode = false
        (activity as MainActivity).paintDeleteButton(R.color.orange_main)
        binding.notesColumn.removeAllViews()
        val allNotes: MutableList<Note> = MutableList(0) { Note() }
        for (fileName in activity?.fileList()!!) {
            val json = activity?.openFileInput(fileName.toString())?.readBytes()?.toString(Charsets.UTF_8)
            if (json != "" && json != null) {
                allNotes.add(noteFromJsonWithType(json))
            } else { activity?.deleteFile(fileName) }
        }
        allNotes.sortByDescending { it.lastUsed }
        for (note in allNotes) {
            val noteView: NoteView = when (note.typeOfNote) {
                "Text" -> NoteView(context, note)
                "Check" -> CheckNoteView(context, note as CheckNote)
                "Alarm" -> AlarmNoteView(context, note as AlarmNote)
                "Image" -> ImageNoteView(context, note as ImageNote)
                else -> throw Exception("MainFragment: Exception occurred")
            }
            noteView.setOnClickListener {
                (activity as MainActivity).editNote(note)
            }
            binding.notesColumn.addView(noteView)
        }
        binding.notesColumn.setPadding(
            0, 0, 0,
            activity?.findViewById<GridLayout>(R.id.create_buttons)?.height?: 0
        )
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        binding = FragmentMainBinding.inflate(inflater, container, false)
        activity?.findViewById<GridLayout>(R.id.create_buttons)?.isVisible = true
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        updateNoteColumn()
        binding.mainScroll.fullScroll(ScrollView.FOCUS_UP)
    }
}
