package com.codeeditor.ide

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class CodeEditorApp : Application() {
    override fun onCreate() {
        super.onCreate()
    }
}
