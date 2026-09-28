package com.anylocale.demo.exampleandroid

import android.content.Context
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.lifecycle.lifecycleScope
import com.anylocale.Anylocale
import com.anylocale.AnylocaleContextWrapper
import kotlinx.coroutines.launch
import java.util.Locale

class MainActivity : ComponentActivity() {

  val anylocale = Anylocale.instance

  override fun attachBaseContext(newBase: Context?) {
    // Wrapping base context will make sure getString calls will use anylocale
    // even for instances which cannot be replaced automatically by the compiler
    super.attachBaseContext(AnylocaleContextWrapper.wrap(newBase))
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    lifecycleScope.launch {
      anylocale.changeFlow.collect {
        // Re-translate views without recreating the Activity for smoother UX
        anylocale.retranslate(this@MainActivity) // or recreate() for more complex activities

        // Make sure the app title is updated
        setTitle(R.string.app_name)

        // Still need to manually update parameterized strings and plurals
        updateParameterizedStrings()
      }
    }

    setContentView(R.layout.activity_main)

    // Make sure the app title stays updated
    setTitle(R.string.app_name)

    // Simple strings (app_name, description) are automatically translated by AnylocaleLayoutInflaterFactory!
    // No need to manually set them - they're handled during layout inflation

    // Only parameterized strings and plurals need manual handling
    updateParameterizedStrings()

    findViewById<Button>(R.id.button_en).setOnClickListener {
      anylocale.setLocale(Locale.ENGLISH)
      anylocale.preload(this)
    }
    findViewById<Button>(R.id.button_fr).setOnClickListener {
      anylocale.setLocale(Locale.FRENCH)
      anylocale.preload(this)
    }
    findViewById<Button>(R.id.button_cs).setOnClickListener {
      anylocale.setLocale("cs")
      anylocale.preload(this)
    }
  }

  private fun updateParameterizedStrings() {
    // These require manual handling because they have format arguments or are plurals
    findViewById<TextView>(R.id.parameterized_text).text =
      getString(R.string.percentage_placeholder, "87")
    findViewById<TextView>(R.id.plural_text).text =
      resources.getQuantityString(R.plurals.plr_test_placeholder_2, 2, 3, "Plurals")
    findViewById<TextView>(R.id.array_text).text =
      resources.getStringArray(R.array.array_test).joinToString()
  }

  override fun onStart() {
    super.onStart()

    // Make sure the translations are loaded
    // This function will initiate translations fetching in the background and
    // will trigger changeFlow whenever updated translations are available
    anylocale.preload(this)
  }
}