package com.jmurphy.expensetracker.ui

import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingAdapter
import com.jmurphy.expensetracker.R

@BindingAdapter("android:text")
fun setFloat(view: TextView, value: Float) {
    if (value.isNaN()) view.setText("");
    else view.setText(value.toString());
}

@InverseBindingAdapter(attribute = "android:text")
fun getFloat(view: TextView): Float {
    val num = view.getText().toString();
    if(num.isEmpty()) return 0.0F;
    try {
        return num.toFloat();
    } catch (e: NumberFormatException) {
        return 0.0F;
    }
}
