package com.ikhokha.common.helpers

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.view.Window
import com.ikhokha.common.R

fun showSuccessDialog(
    context: Context,
    title: String,
    message: String,
    buttonText: String = context.getString(R.string.ok),
    callbackFun: () -> Unit = {}
) {
    showDialog(context, title, message, buttonText, callbackFun, R.drawable.ic_success)
}

fun showErrorDialog(
    context: Context,
    title: String,
    message: String,
    buttonText: String = context.getString(R.string.ok),
    callbackFun: () -> Unit = {}
) {
    showDialog(context, title, message, buttonText, callbackFun, R.drawable.ic_error)
}

private fun showDialog(
    context: Context,
    title: String,
    message: String,
    buttonText: String = context.getString(R.string.ok),
    callbackFun: () -> Unit = {},
    icon: Int
) {
    val ab = setupBasicMessage(title, message, buttonText, "", "", callbackFun, {}, {}, context)
    ab.setIcon(icon)
    ab.setCancelable(false)
    showDialogMessage(ab, context)
}

private fun showDialogMessage(ab: AlertDialog.Builder, context: Context) {
    val a = ab.create()
    a.requestWindowFeature(Window.FEATURE_NO_TITLE)
    a.show()

    a.getButton(DialogInterface.BUTTON_NEGATIVE)
        .setTextColor(context.resources.getColor(R.color.dark_text))
    a.getButton(DialogInterface.BUTTON_POSITIVE)
        .setTextColor(context.resources.getColor(R.color.dark_text))
    a.getButton(DialogInterface.BUTTON_NEUTRAL)
        .setTextColor(context.resources.getColor(R.color.dark_text))
}

private fun setupBasicMessage(
    title: String,
    message: String,
    positiveButtonText: String?,
    neutralButtonText: String?,
    negativeButtonText: String?,
    positiveCallback: () -> Unit,
    neutralCallback: () -> Unit,
    negativeCallback: () -> Unit,
    context: Context
): AlertDialog.Builder {
    val ab = AlertDialog.Builder(context, R.style.AlertDialogCustom)
    ab.setMessage(message)
        .setTitle(title)
        .setPositiveButton(positiveButtonText) { dialogInterface, i ->
            positiveCallback()
        }

    if (neutralButtonText != null) {
        ab.setNeutralButton(neutralButtonText) { dialogInterface, i ->
            neutralCallback()
        }
    }

    if (negativeButtonText != null) {
        ab.setNegativeButton(negativeButtonText) { dialogInterface, i ->
            negativeCallback()
        }
    }

    return ab
}