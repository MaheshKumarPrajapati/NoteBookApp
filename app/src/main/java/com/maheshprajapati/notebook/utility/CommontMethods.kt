package com.maheshprajapati.myapplication.utility


import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Gravity
import android.view.View
import android.view.Window
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.maheshprajapati.myapplication.R
import java.text.SimpleDateFormat
import java.util.*


public class CommontMethods {

    public fun getFormattedDateString(date: Date): String {
        val formatter = SimpleDateFormat("dd MMM", Locale.ENGLISH)
        return formatter.format(date)
    }

    fun hideKeyboard(activity: Activity) {
        val imm: InputMethodManager =
            activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        //Find the currently focused view, so we can grab the correct window token from it.
        var view = activity.currentFocus
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = View(activity)
        }
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    fun customToast(
        activity: Context?,
        message: String?
    ) {
        val toast: Toast = Toast.makeText(activity, message, Toast.LENGTH_SHORT)
        val toastView =
            toast.view
        val toastMessage = toastView!!.findViewById<View>(android.R.id.message) as TextView
        toastMessage.textSize = 18f
        toastMessage.setTextColor(Color.WHITE)
        //  toastMessage.setCompoundDrawablesWithIntrinsicBounds(icPushToPin, 0, 0, 0)
        toastMessage.gravity = Gravity.CENTER
        // toastMessage.compoundDrawablePadding = 16
        toastView!!.setBackgroundColor(Color.BLACK)
        toast.show()
    }

    fun showTwoButtonDialogWithCallBack(
        ctx: Context?,
        message: String?,
        negativeBtn: String?,
        positiveBtn: String?,
        isCancelable: Boolean,
        listener: OnTwoButtonDialogClickListener
    ): Dialog {
        return try {
            val dialog = Dialog(ctx!!)
            dialog.window!!.requestFeature(Window.FEATURE_NO_TITLE)
            dialog.setContentView(R.layout.alert_dialog_layout)
            dialog.window!!.setBackgroundDrawable(
                ColorDrawable(Color.TRANSPARENT)
            )
            dialog.window!!.setLayout(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            val window = dialog.window
            window!!.setLayout(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            window.setGravity(Gravity.CENTER)
            val text = dialog.findViewById<TextView>(R.id.alert_message)
            val ok =
                dialog.findViewById<Button>(R.id.alert_button)
            ok.visibility = View.GONE
            val twoBut = dialog.findViewById<LinearLayout>(R.id.layout_two_but)
            twoBut.visibility = View.VISIBLE
            val cancel =
                dialog.findViewById<Button>(R.id.alert_cancel)
            val continu =
                dialog.findViewById<Button>(R.id.alert_continue)
            text.text = message
            cancel.text = negativeBtn
            cancel.setOnClickListener {
                listener.onDialogNegativeButtonClick(dialog)
                dialog.dismiss()
            }
            continu.text = positiveBtn
            continu.setOnClickListener {
                listener.onDialogPositiveButtonClick(dialog)
                dialog.dismiss()
            }
            dialog.setCancelable(isCancelable)
            dialog.show()
            dialog
        } catch (e: Exception) {
            Dialog(ctx!!)
        }
    }

    interface OnTwoButtonDialogClickListener {
        fun onDialogPositiveButtonClick(dialog: Dialog?)
        fun onDialogNegativeButtonClick(dialog: Dialog?)
    }
}