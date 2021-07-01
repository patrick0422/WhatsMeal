package com.example.whatsmeal

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.whatsmeal.databinding.FragmentSettingBinding


class SettingFragment : Fragment() {
    private lateinit var binding: FragmentSettingBinding
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentSettingBinding.inflate(layoutInflater, container, false)

        return binding.root
    }
}