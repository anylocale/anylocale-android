package com.anylocale.demo.examplejetpack

import android.app.Application
import com.anylocale.Anylocale
import com.anylocale.storage.AnylocaleStorageProviderAndroid

class MyApplication : Application() {

  override fun onCreate() {
    super.onCreate()

    Anylocale.init {
      contentDelivery {
        url = "https://anylocale.com/ota/v1/your-distribution-key"
        storage = AnylocaleStorageProviderAndroid(this@MyApplication, BuildConfig.VERSION_CODE)
      }
    }
  }
}