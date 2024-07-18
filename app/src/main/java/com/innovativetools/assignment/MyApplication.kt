package com.innovativetools.assignment


import android.app.Application
import com.innovativetools.assignment.data.network.auth.TokenManager

class MyApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        TokenManager.init(this)
        TokenManager.saveToken("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6MjU5MjcsImlhdCI6MTY3NDU1MDQ1MH0.dCkW0ox8tbjJA2GgUx2UEwNlbTZ7Rr38PVFJevYcXFI")
    }
}