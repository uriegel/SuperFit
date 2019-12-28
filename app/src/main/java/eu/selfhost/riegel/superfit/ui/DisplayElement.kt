package eu.selfhost.riegel.superfit.ui

import android.content.Context
import android.util.AttributeSet
import androidx.constraintlayout.widget.ConstraintLayout
import eu.selfhost.riegel.superfit.R
import kotlinx.android.synthetic.main.view_display_element.view.*


class DisplayElement(context: Context?, attrs: AttributeSet?)
        : ConstraintLayout(context, attrs) {
    init {
        //Inflate xml resource, pass "this" as the parent, we use <merge> tag in xml to avoid
        //redundant parent, otherwise a LinearLayout will be added to this LinearLayout ending up
        //with two view groups
        inflate(context, R.layout.view_display_element,this)

        val a = context?.obtainStyledAttributes(
            attrs,
            R.styleable.DisplayElement, 0, 0
        )
        val titleText = a?.getString(R.styleable.DisplayElement_title)
        val unitText = a?.getString(R.styleable.DisplayElement_unit)
        a?.recycle()

        title.text = titleText
        unit.text = unitText
    }
}