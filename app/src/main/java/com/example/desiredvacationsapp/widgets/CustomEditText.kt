package com.example.desiredvacationsapp.widgets

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Resources
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.core.content.ContextCompat
import com.example.desiredvacationsapp.R
import com.example.desiredvacationsapp.databinding.WidgetCustomEditTextBinding


class CustomEditText @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private val floatingLabel: TextView
    private val editText: EditText
    private val clearButton: ImageView
    private val actionButton: ImageView
    private var isErrorState = false
    private var isTextChanged = false
    private var isSaveClicked = false


    init {
        val view = LayoutInflater.from(context).inflate(R.layout.widget_custom_edit_text, this, true)
        floatingLabel = view.findViewById(R.id.floatingLabel)
        editText = view.findViewById(R.id.customEditText)
        editText.setBackgroundResource(R.drawable.bg_text_input)
        editText.invalidateDrawable(editText.background)
        clearButton = view.findViewById(R.id.clearButton)
        actionButton = view.findViewById(R.id.actionButton)

        val attributes = context.obtainStyledAttributes(attrs, R.styleable.CustomEditText)
        val customHint = attributes.getString(R.styleable.CustomEditText_customHint)
        attributes.recycle()

        if (!customHint.isNullOrEmpty()) {
            editText.hint = customHint
            floatingLabel.text = customHint
        }

        editText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                setInvalidState(false)
                editText.hint = null
                if (editText.text.isEmpty()) {
                    showFloatingLabel()
                }
            } else {
                if (editText.text.isEmpty()) {
                    editText.hint = floatingLabel.text
                    hideFloatingLabel()  // Hide the floating label when the EditText is empty
                } else {
                    setInvalidState(false)
                }
            }
        }



        editText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                isTextChanged = true

                // If the user types something in the field, reset the error state
                if (s?.isNotEmpty() == true || s?.isEmpty() == true) {
                    setInvalidState(false)
                }

                if (isErrorState) {
                    editText.isSelected = true
                    floatingLabel.isSelected = true
                    actionButton.setImageResource(R.drawable.ic_error)
                    actionButton.visibility = View.VISIBLE
                } else {
                    editText.isSelected = false
                    floatingLabel.isSelected = false
                    actionButton.setImageResource(R.drawable.ic_clear)
                    actionButton.visibility = if (s?.isNotEmpty() == true) View.VISIBLE else View.INVISIBLE
                }
            }
        })

        actionButton.setOnClickListener {
            if (isErrorState) {
                // Show the tooltip below the error icon when the error icon is clicked
                showTooltip(it)
            } else {
                editText.text.clear()
            }
        }

        clearButton.setOnClickListener {
            editText.text.clear()
        }
    }



    private fun showFloatingLabel() {
        if (floatingLabel.visibility != VISIBLE) {
            floatingLabel.visibility = VISIBLE
            animateLabelIn()  // add this line
        }
    }


    private fun hideFloatingLabel() {
        if (floatingLabel.visibility != INVISIBLE) {
            animateLabelOut()  // add this line
            floatingLabel.visibility = INVISIBLE
        }
    }
    private fun animateLabelIn() {
        val fadeIn = ObjectAnimator.ofFloat(floatingLabel, "alpha", 0f, 1f)
        fadeIn.duration = 200  // duration in milliseconds, adjust as needed
        fadeIn.start()
    }

    private fun animateLabelOut() {
        val fadeOut = ObjectAnimator.ofFloat(floatingLabel, "alpha", 1f, 0f)
        fadeOut.duration = 200  // duration in milliseconds, adjust as needed
        fadeOut.start()
    }

    private fun setInvalidState(isInvalid: Boolean) {
        isErrorState = isInvalid // set the error state based on the input

        if (isInvalid) {
            editText.isSelected = true
            floatingLabel.isSelected = true
            actionButton.setImageResource(R.drawable.ic_error)
            actionButton.visibility = View.VISIBLE
            editText.setHintTextColor(ContextCompat.getColor(context, android.R.color.holo_red_dark))
        } else {
            editText.isSelected = false
            floatingLabel.isSelected = false
            actionButton.setImageResource(R.drawable.ic_clear)
            actionButton.visibility = if (editText.text.isNotEmpty()) View.VISIBLE else View.INVISIBLE
            editText.setHintTextColor(ContextCompat.getColor(context, android.R.color.darker_gray))
        }
    }

    fun markAsError() {
        setInvalidState(true)
    }
    val text: CharSequence
        get() = editText.text


    @SuppressLint("InflateParams")
    private fun showTooltip(anchor: View) {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val tooltipView = inflater.inflate(R.layout.tooltip_layout, null)

        val popup = PopupWindow(tooltipView, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        popup.isOutsideTouchable = true
        popup.isFocusable = true

        // This will display the tooltip right below the anchor (error icon)
        popup.showAsDropDown(anchor)

        // If you want to apply offsets, you can use:
        // popup.showAsDropDown(anchor, xOffset, yOffset)
    }
}
