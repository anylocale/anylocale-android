package com.anylocale.demo.compose

import com.anylocale.Anylocale

object Setup {

  fun init() {
    Anylocale.init {
      contentDelivery {
        url = "https://anylocale.com/ota/v1/your-distribution-key"
      }
    }
  }
}