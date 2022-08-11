package com.prosecshane.zametki.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridLayout
import androidx.core.view.isGone
import androidx.core.view.isVisible
import com.prosecshane.zametki.R
import com.prosecshane.zametki.databinding.FragmentImportBinding

class ImportFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        val binding = FragmentImportBinding.inflate(inflater, container, false)
        activity?.findViewById<GridLayout>(R.id.create_buttons)?.isGone = true
        return binding.root
    }
}