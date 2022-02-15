package com.dontsu.dotindicators

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.annotation.StyleableRes

class BaseDotsIndicator @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    companion object {
        const val DEFAULT_POINT_COLOR = Color.CYAN
    }

    enum class Type(
        val defaultSize: Float,
        val defaultSpacing: Float,
        @StyleableRes val styleableId: IntArray,
        @StyleableRes val dotsColorId: Int,
        @StyleableRes val dotsSizeId: Int,
        @StyleableRes val dotsSpacingId: Int,
        @StyleableRes val dotsCornerRadiusId: Int,
        @StyleableRes val dotsClickableId: Int
    ) {
        DEFAULT(
            defaultSize = 16f,
            defaultSpacing = 8f,
            styleableId = R.styleable.SpringDotsIndicator,
            dotsColorId = R.styleable.SpringDotsIndicator_dotsColor,
            dotsSizeId = R.styleable.SpringDotsIndicator_dotsSize,
            dotsSpacingId = R.styleable.SpringDotsIndicator_dotsSpacing,
            dotsCornerRadiusId = R.styleable.SpringDotsIndicator_dotsCornerRadius,
            dotsClickableId = R.styleable.SpringDotsIndicator_dotsClickable
        ),
        SPRING(
            defaultSize = 16f,
            defaultSpacing = 4f,
            styleableId = R.styleable.SpringDotsIndicator,
            dotsColorId = R.styleable.SpringDotsIndicator_dotsColor,
            dotsSizeId = R.styleable.SpringDotsIndicator_dotsSize,
            dotsSpacingId = R.styleable.SpringDotsIndicator_dotsSpacing,
            dotsCornerRadiusId = R.styleable.SpringDotsIndicator_dotsCornerRadius,
            dotsClickableId = R.styleable.SpringDotsIndicator_dotsClickable
        ),
        WORM(
            defaultSize = 16f,
            defaultSpacing = 4f,
            styleableId = R.styleable.SpringDotsIndicator,
            dotsColorId = R.styleable.SpringDotsIndicator_dotsColor,
            dotsSizeId = R.styleable.SpringDotsIndicator_dotsSize,
            dotsSpacingId = R.styleable.SpringDotsIndicator_dotsSpacing,
            dotsCornerRadiusId = R.styleable.SpringDotsIndicator_dotsCornerRadius,
            dotsClickableId = R.styleable.SpringDotsIndicator_dotsClickable
        )
    }

    @JvmField
    protected val dots = ArrayList<ImageView>() // ImageView를 여러개 넣으려나보다

    var dotsClickable: Boolean = true
    var dotsColor: Int = DEFAULT_POINT_COLOR // 위 companion object에서 만들어 놓은 컬러
        set(value) {
            field = value
            refreshDotsColors()
        }

    protected var dotsSize = dpToPxF(type.de)

    protected fun refreshDotsColors() {
        for (i in dots.indices) {
            refreshDotColor(i)
        }
    }

}
