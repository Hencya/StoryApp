package com.example.storyapp.view.customView

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.text.Editable
import android.text.TextWatcher
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.core.content.ContextCompat
import com.example.storyapp.R
import com.google.android.material.textfield.TextInputEditText

class MyEditTextPassword : TextInputEditText, View.OnTouchListener {

    private lateinit var eyeIcon: Drawable
    private lateinit var passIcon: Drawable

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        showEyeButton()
        setBackgroundResource(R.drawable.border_edit_text)
        hint = context.getString(R.string.hint_password)
        textAlignment = View.TEXT_ALIGNMENT_VIEW_START
    }

    private fun init() {
        eyeIcon = ContextCompat.getDrawable(context, R.drawable.ic_eye_off_24) as Drawable
        passIcon = ContextCompat.getDrawable(context, R.drawable.ic_baseline_lock_24) as Drawable

        setOnTouchListener(this)

        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                // Do nothing.
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                // Do nothing.
            }

            override fun afterTextChanged(s: Editable) {
                if (s.toString().length < 6) showError()
            }
        })
    }

    private fun showError() {
        error = context.getString(R.string.invalid_password)
    }

    private fun showEyeButton() {
        setCompoundDrawablesWithIntrinsicBounds(
            passIcon, null,
            eyeIcon, null
        )
    }

    private fun hideEyeButton() {
        setCompoundDrawablesWithIntrinsicBounds(passIcon, null, null, null)
    }


    override fun onTouch(v: View?, event: MotionEvent): Boolean {
        if (compoundDrawables[2] != null) {
            val eyeButtonStart: Float
            val eyeButtonEnd: Float
            var isEyeButtonClicked = false

            if (layoutDirection == View.LAYOUT_DIRECTION_RTL) {
                eyeButtonEnd = (eyeIcon.intrinsicWidth + paddingStart).toFloat()
                if (event.x < eyeButtonEnd) isEyeButtonClicked = true
            } else {
                eyeButtonStart = (width - paddingEnd - eyeIcon.intrinsicWidth).toFloat()
                if (event.x > eyeButtonStart) isEyeButtonClicked = true
            }

            if (isEyeButtonClicked) {
                return when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        hideEyeButton()
                        if (transformationMethod.equals(HideReturnsTransformationMethod.getInstance())) {
                            transformationMethod =
                                PasswordTransformationMethod.getInstance()
                            eyeIcon = ContextCompat.getDrawable(
                                context,
                                R.drawable.ic_eye_off_24
                            ) as Drawable
                            showEyeButton()
                        } else {
                            transformationMethod =
                                HideReturnsTransformationMethod.getInstance()
                            eyeIcon =
                                ContextCompat.getDrawable(context, R.drawable.ic_eye_24) as Drawable
                            showEyeButton()
                        }
                        true
                    }
                    else -> false
                }
            } else return false
        }
        return false
    }
}