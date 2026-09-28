package com.anylocale.demo.exampleandroid

import android.app.Application
import com.anylocale.Anylocale
import com.anylocale.storage.AnylocaleStorageProviderAndroid

class MyApplication : Application() {

  override fun onCreate() {
    super.onCreate()

    Anylocale.init {
      contentDelivery {
        url = "http://10.0.2.2:3005/ota/v1/dk_51af250f9952639a5bba181e0ac0c7fc22bd2dd28819782bfa2627a4ba4a8b34"
        storage = AnylocaleStorageProviderAndroid(this@MyApplication, BuildConfig.VERSION_CODE)
      }
      defaultLanguage("en")
    }
  }
}