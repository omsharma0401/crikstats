package com.omsharma.crikstats.navigation

import android.content.Context
import android.content.Intent
import android.util.Log

class DynamicFeatureNavigator(private val context: Context) {

    fun launchActivity(className: String) {
        try {
            val intent = Intent().setClassName(context.packageName, className)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        } catch (e: Exception) {
            Log.e("DynamicFeatureNavigator", "Launch Failed", e)
        }
    }
}