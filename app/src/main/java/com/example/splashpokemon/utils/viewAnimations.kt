package com.example.splashpokemon.utils

import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.animation.AlphaAnimation
import kotlin.time.Duration

// função aplica o fade nas views
fun fadeInViews(vararg views: View, duration: Long = 400) {
    val fadeIn = AlphaAnimation(0f, 1f)
    fadeIn.duration = duration
    fadeIn.fillAfter = true
    for (v in views) {
        v.visibility = View.VISIBLE
        v.startAnimation(fadeIn)
    }
}

//Aplica o fade-out
fun fadeOut(view: View, duration: Long = 400) {
    val fadeOut = AlphaAnimation(1f, 0f)
    fadeOut.duration = duration
    fadeOut.fillAfter = true
    view.startAnimation(fadeOut)
    Handler(Looper.getMainLooper()).postDelayed({
        view.visibility = View.GONE
    }, duration)
}