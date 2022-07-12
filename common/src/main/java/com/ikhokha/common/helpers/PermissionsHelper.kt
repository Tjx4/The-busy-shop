package com.ikhokha.common.helpers

import android.content.Context
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

fun requestNotGrantedPermissions(activity: AppCompatActivity, permissions: Array<out String>, requestCode: Int) {
    val notGrantedPermissions = permissions.filterNot { isPermissionGranted(activity, it) }
    if (notGrantedPermissions.isNotEmpty()) {
        ActivityCompat.requestPermissions(activity, notGrantedPermissions.toTypedArray(), requestCode)
    }
}

fun isPermissionGranted(context: Context, permission: String): Boolean {
    return ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
}

fun areAllPermissionsGranted(context: Context, permissions: Array<out String>): Boolean {
    permissions.forEach { permission ->
        if (isPermissionGranted(context, permission).not()) {
            return false
        }
    }
    return true
}