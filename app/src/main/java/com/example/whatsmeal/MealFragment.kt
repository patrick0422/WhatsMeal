package com.example.whatsmeal

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.whatsmeal.databinding.FragmentMealBinding


class MealFragment : Fragment() {
    private lateinit var binding: FragmentMealBinding
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentMealBinding.inflate(layoutInflater, container, false)


        return binding.root
    }
}