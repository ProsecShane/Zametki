package com.prosecshane.zametki.ui

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.prosecshane.zametki.R
import com.prosecshane.zametki.databinding.FragmentAboutBinding

class AboutFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle? ): View {
        val binding = FragmentAboutBinding.inflate(inflater, container, false)

        val copyEmail = binding.copyEmail
        copyEmail.setOnClickListener {
            val clipboardManager: ClipboardManager = activity?.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clipData = ClipData.newPlainText("zametkiEmail", "ProsecShane@yandex.ru")
            clipboardManager.setPrimaryClip(clipData)
            Toast.makeText(activity?.applicationContext, "Скопировано", Toast.LENGTH_SHORT).show()
        }
        activity?.findViewById<LinearLayout>(R.id.create_buttons)?.isGone = true
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        activity?.findViewById<LinearLayout>(R.id.create_buttons)?.isVisible = true
    }
}