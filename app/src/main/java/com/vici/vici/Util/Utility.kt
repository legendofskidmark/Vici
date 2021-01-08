package com.vici.vici.Util

import android.content.Context
import android.view.Window
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.ActionBar

class Utility {
    companion object {
        fun hideActionBar(actionBar: ActionBar?, window: Window) {
            actionBar?.hide()
            val g: Window = window
            g.setFlags(
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
            )
        }

        fun showKeyboard(mContext: Context) {
            val imm: InputMethodManager = mContext.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
        }

        fun hideKeyboard(mContext: Context, window: Window) {
            val imm = mContext.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(window.getDecorView().getRootView().getWindowToken(), 0)
        }
    }
}