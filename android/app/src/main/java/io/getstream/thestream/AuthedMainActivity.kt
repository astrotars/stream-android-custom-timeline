package io.getstream.thestream

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class AuthedMainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_authed_main)

        val navigation = findViewById<BottomNavigationView>(R.id.navigation)
        navigation.setOnNavigationItemSelectedListener(navListener)

        addFragment(TimelineFragment())
    }

    private fun addFragment(fragment: Fragment) {
        supportFragmentManager
            .beginTransaction()
            .setCustomAnimations(
                R.anim.design_bottom_sheet_slide_in,
                R.anim.design_bottom_sheet_slide_out
            )
            .replace(R.id.content, fragment, fragment.javaClass.simpleName)
            .commit()
    }

    private val navListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_timeline -> {
                addFragment(TimelineFragment())

                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_profile -> {
                addFragment(ProfileFragment())

                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_people -> {
                addFragment(PeopleFragment())

                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }
}
