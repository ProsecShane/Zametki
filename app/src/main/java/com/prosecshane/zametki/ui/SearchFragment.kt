package com.prosecshane.zametki.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridLayout
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.prosecshane.zametki.R
import com.prosecshane.zametki.databinding.FragmentSearchBinding

class SearchFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        val binding = FragmentSearchBinding.inflate(inflater, container, false)
        activity?.findViewById<GridLayout>(R.id.create_buttons)?.isGone = true
        return binding.root
    }
}