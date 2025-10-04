package com.example.firstaidapp.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.LruCache
import java.io.InputStream

/**
 * Performance optimization utilities for the First Aid App
 * Handles image caching, memory management, and database optimization
 */
class PerformanceOptimizer private constructor() {

    companion object {
        @Volatile
        private var INSTANCE: PerformanceOptimizer? = null

        fun getInstance(): PerformanceOptimizer {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: PerformanceOptimizer().also { INSTANCE = it }
            }
        }
    }

    // Image cache for better performance
    private val imageCache: LruCache<String, Bitmap> by lazy {
        val maxMemory = (Runtime.getRuntime().maxMemory() / 1024).toInt()
        val cacheSize = maxMemory / 8 // Use 1/8th of available memory

        object : LruCache<String, Bitmap>(cacheSize) {
            override fun sizeOf(key: String, bitmap: Bitmap): Int {
                return bitmap.byteCount / 1024
            }
        }
    }

    /**
     * Loads and caches bitmaps efficiently
     */
    fun loadBitmapFromAssets(context: Context, fileName: String): Bitmap? {
        // Check cache first
        imageCache.get(fileName)?.let { return it }

        try {
            val inputStream: InputStream = context.assets.open("images/$fileName")
            val options = BitmapFactory.Options().apply {
                inJustDecodeBounds = true
            }
            BitmapFactory.decodeStream(inputStream, null, options)
            inputStream.close()

            // Calculate optimal sample size
            options.inSampleSize = calculateInSampleSize(options, 300, 300)
            options.inJustDecodeBounds = false

            val newInputStream: InputStream = context.assets.open("images/$fileName")
            val bitmap = BitmapFactory.decodeStream(newInputStream, null, options)
            newInputStream.close()

            // Cache the bitmap
            bitmap?.let { imageCache.put(fileName, it) }
            return bitmap
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    /**
     * Calculates optimal sample size for bitmap loading
     */
    private fun calculateInSampleSize(
        options: BitmapFactory.Options,
        reqWidth: Int,
        reqHeight: Int
    ): Int {
        val height = options.outHeight
        val width = options.outWidth
        var inSampleSize = 1

        if (height > reqHeight || width > reqWidth) {
            val halfHeight: Int = height / 2
            val halfWidth: Int = width / 2

            while (halfHeight / inSampleSize >= reqHeight && halfWidth / inSampleSize >= reqWidth) {
                inSampleSize *= 2
            }
        }
        return inSampleSize
    }

    /**
     * Clears image cache to free memory
     */
    fun clearImageCache() {
        imageCache.evictAll()
    }

    /**
     * Gets current cache size for monitoring
     */
    fun getCacheSize(): Int {
        return imageCache.size()
    }
}
