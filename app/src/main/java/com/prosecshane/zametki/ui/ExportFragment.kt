package com.prosecshane.zametki.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.prosecshane.zametki.databinding.FragmentExportBinding

class ExportFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        val binding = FragmentExportBinding.inflate(inflater, container, false)

        // code

        return binding.root
    }
}