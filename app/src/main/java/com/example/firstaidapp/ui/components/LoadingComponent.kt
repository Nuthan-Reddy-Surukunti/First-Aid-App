package com.example.firstaidapp.ui.components

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import com.example.firstaidapp.databinding.ComponentLoadingBinding

class LoadingComponent @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private val binding: ComponentLoadingBinding

    init {
        binding = ComponentLoadingBinding.inflate(LayoutInflater.from(context), this, true)
        orientation = VERTICAL
    }

    fun showLoading(message: String = "Loading...") {
        binding.tvLoadingMessage.text = message
        visibility = VISIBLE
    }

    fun hideLoading() {
        visibility = GONE
    }
}
