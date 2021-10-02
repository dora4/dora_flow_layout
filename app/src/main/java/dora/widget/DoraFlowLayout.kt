package dora.widget

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup

class DoraFlowLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ViewGroup(context, attrs, defStyleAttr) {

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)
        var width = 0
        var height = 0
        var lineWidth = 0
        var lineHeight = 0
        val childCount = this.childCount
        for (i in 0 until childCount) {
            val child = getChildAt(i)
            if (child.visibility == GONE) {
                if (i == childCount - 1) {
                    width = Math.max(lineWidth, width)
                    height += lineHeight
                }
                continue
            }
            measureChild(child, widthMeasureSpec, heightMeasureSpec)
            val lp = child
                .layoutParams as MarginLayoutParams
            val childWidth = (child.measuredWidth + lp.leftMargin
                    + lp.rightMargin)
            val childHeight = (child.measuredHeight + lp.topMargin
                    + lp.bottomMargin)
            if (lineWidth + childWidth > widthSize - paddingLeft - paddingRight) {
                width = Math.max(width, lineWidth)
                lineWidth = childWidth
                height += lineHeight
                lineHeight = childHeight
            } else {
                lineWidth += childWidth
                lineHeight = Math.max(lineHeight, childHeight)
            }
            if (i == childCount - 1) {
                width = Math.max(lineWidth, width)
                height += lineHeight
            }
        }
        setMeasuredDimension(
            if (widthMode == MeasureSpec.EXACTLY) widthSize else width + paddingLeft + paddingRight,
            if (heightMode == MeasureSpec.EXACTLY) heightSize else height + paddingTop + paddingBottom
        )
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        val width = this.width
        var lineWidth = 0
        var lineHeight = 0
        var maxChildHeight = 0
        val childCount = this.childCount
        for (i in 0 until childCount) {
            val child = getChildAt(i)
            if (child.visibility == VISIBLE) {
                val mlp = child.layoutParams as MarginLayoutParams
                val childWidth = child.measuredWidth + mlp.leftMargin + mlp.rightMargin
                val childHeight = child.measuredHeight + mlp.topMargin + mlp.bottomMargin
                if (childWidth + lineWidth > width) {
                    lineWidth = 0
                    lineHeight += maxChildHeight
                    maxChildHeight = 0
                }
                val left = lineWidth + mlp.leftMargin
                val top = lineHeight + mlp.topMargin
                val right = left + childWidth - mlp.leftMargin - mlp.rightMargin
                val bottom = top + childHeight - mlp.topMargin - mlp.bottomMargin
                lineWidth = right + mlp.rightMargin
                maxChildHeight = Math.max(maxChildHeight, childHeight)
                child.layout(left, top, right, bottom)
            }
        }
    }

    override fun generateDefaultLayoutParams(): LayoutParams {
        return MarginLayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
    }

    override fun generateLayoutParams(p: LayoutParams): LayoutParams {
        return MarginLayoutParams(p)
    }

    override fun generateLayoutParams(attrs: AttributeSet): LayoutParams {
        return MarginLayoutParams(this.context, attrs)
    }
}