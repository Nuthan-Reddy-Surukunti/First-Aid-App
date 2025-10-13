package com.example.firstaidapp.utils

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters

class DataInitializationWorker(appContext: Context, params: WorkerParameters): CoroutineWorker(appContext, params) {

    private val TAG = "DataInitWorker"

    override suspend fun doWork(): Result {
        return try {
            Log.i(TAG, "doWork: starting data initialization via WorkManager")
            val initializer = DataInitializer(applicationContext)
            initializer.initializeDataBlocking()
            Log.i(TAG, "doWork: data initialization completed successfully")
            Result.success()
        } catch (e: Exception) {
            Log.e(TAG, "doWork: data initialization failed", e)
            // Let WorkManager retry with backoff
            Result.retry()
        }
    }
}

