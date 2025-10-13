package com.example.firstaidapp

import android.app.Application
import android.os.Handler
import android.os.Looper
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.firstaidapp.utils.DataInitializationWorker

class FirstAidApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        // Defer initialization briefly to avoid doing heavy DB work directly in Application.onCreate
        Handler(Looper.getMainLooper()).postDelayed({
            val request = OneTimeWorkRequestBuilder<DataInitializationWorker>().build()
            WorkManager.getInstance(this).enqueueUniqueWork(
                "data_initialization",
                ExistingWorkPolicy.KEEP,
                request
            )
        }, 1000)
    }
}