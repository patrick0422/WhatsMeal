package com.example.whatsmeal

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.example.whatsmeal.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    private val mealFragment by lazy { MealFragment() }
    private val settingFragment by lazy { SettingFragment() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.bottomNav.setOnNavigationItemSelectedListener {
            when(it.itemId) {
                R.layout.fragment_meal -> {
                    changeFragment(mealFragment)
                }
                R.layout.fragment_setting -> {
                    changeFragment(settingFragment)
                }
                else -> {
                    changeFragment(mealFragment)
                }
            }
            true
        }

    }
    private fun changeFragment(fragment: Fragment) {
        supportFragmentManager
            .beginTransaction()
            .replace(binding.viewPager.id, fragment)
            .commit()
    }
}