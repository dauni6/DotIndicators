package com.dontsu.dotindicators

import android.animation.ArgbEvaluator
import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.ImageView
import android.widget.LinearLayout

class DotsIndicator @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : BaseDotsIndicator(context, attrs, defStyleAttr) {

    companion object {
        const val DEFAULT_WIDTH_FACTOR = 2.5f
        const val DEFAULT_ELEVATION_VALUE = 0f
        const val DEFAULT_PROGRESS_MODE_VALUE = false
    }

    private lateinit var linearLayout: LinearLayout
    private var dotsWidthFactor: Float = 0f
    private var progressMode: Boolean = false
    private var dotsElevation: Float = 0f

    var selectedDotColor: Int = 0
        set(value) {
            field = value
            refreshDotsColors()
        }

    private val argbEvaluator = ArgbEvaluator()

    init {
        init(attrs)
    }

    private fun init(attrs: AttributeSet?) {
        linearLayout = LinearLayout(context)
        linearLayout.orientation = LinearLayout.HORIZONTAL // 가로로 보여준다
        addView(linearLayout, WRAP_CONTENT, WRAP_CONTENT)

        dotsWidthFactor = DEFAULT_WIDTH_FACTOR

        if (attrs != null) {
            // attrs.xml에  <declare-styleable>로 정의했던 attributes를 가져오기
            val a = context.obtainStyledAttributes(attrs, R.styleable.DotsIndicator)
            selectedDotColor = a.getColor(R.styleable.DotsIndicator_selectedDotColor, DEFAULT_POINT_COLOR)
            dotsWidthFactor = a.getFloat(R.styleable.DotsIndicator_dotsWidthFactor, DEFAULT_WIDTH_FACTOR)
            if (dotsWidthFactor < 1) {
                //사용자가 1미만으로 width를 입력했을 경우 자동으로 DEFAULT(2.5f)로 넣어주려고 하나보다
                dotsWidthFactor = DEFAULT_WIDTH_FACTOR
            }
            progressMode = a.getBoolean(R.styleable.DotsIndicator_progressMode, DEFAULT_PROGRESS_MODE_VALUE)
            dotsElevation = a.getDimension(R.styleable.DotsIndicator_dotsElevation, DEFAULT_ELEVATION_VALUE)

            a.recycle()
        }

        // 개발자 툴인지 아닌지 확인 후 맞으면 dot을 5개 보여주고 그리기
        if (isInEditMode) {
            addDots(5)
            refreshDots()
        }

    }

    override fun addDot(index: Int) {
        val dot = LayoutInflater.from(context).inflate(R.layout.dot_layout, this, false)
        val imageView = dot.findViewById<ImageView>(R.id.dot)
        val params = imageView.layoutParams as LayoutParams
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            dot.layoutDirection = View.LAYOUT_DIRECTION_LTR
        }
        
        params.height = dotsSize.toInt()
        params.width = params.height
        params.setMargins(dotsSpacing.toInt(), 0, dotsSpacing.toInt(), 0)
        val background = DotsGradientDrawable()
        background.cornerRadius = dotsCornerRadius
        if (isInEditMode) {
            background.setColor(if (index == 0) selectedDotColor else dotsColor)
        } else {
            background.setColor(if (index == pager!!.currentItem) selectedDotColor else dotsColor)
        }
        imageView.background = background
        
        dot.setOnClickListener { 
            if (dotsClickable && index < pager?.count ?: 0) {
                pager!!.setCurrentItem(index, true)
            }
        }
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            dot.setPaddingHorizontal((dotsElevation * 0.8f).toInt())
            dot.setPaddingVertical((dotsElevation * 2).toInt())
            imageView.elevation = dotsElevation
        }

        dots.add(imageView)
        linearLayout.addView(dot)
        
    }

    override fun removeDot(index: Int) {
        linearLayout.removeViewAt(linearLayout.childCount - 1)
        dots.removeAt(dots.size - 1)
    }

    override fun buildOnPageChangedListener(): OnPageChangeListenerHelper {
       return object : OnPageChangeListenerHelper() {
           override val pageCount: Int
               get() = dots.size

           override fun onPageScrolled(
               selectedPosition: Int,
               nextPosition: Int,
               positionOffset: Float
           ) {
               val selectedDot = dots[selectedPosition]
               // Selected dot
               val selectedDotWidth = (dotsSize + dotsSize * (dotsWidthFactor - 1) * (1 - positionOffset)).toInt()
               selectedDot.setWidth(selectedDotWidth)

               if (dots.isInBounds(nextPosition)) {
                   val nextDot = dots[nextPosition]

                   val nextDotWidth = (dotsSize + dotsSize * (dotsWidthFactor - 1) * positionOffset).toInt()
                   nextDot.setWidth(nextDotWidth)

                   val selectedDotBackground = selectedDot.background as DotsGradientDrawable
                   val nextDotBackground = nextDot.background as DotsGradientDrawable

                   if (selectedDotColor != dotsColor) {
                       // 선택 색상을 아마 변경해주려고 하는 것 같은데 떨어진 정도에 따라 색상이 변하게 하려고 argbEvaluator를 사용한게 아닌가 싶다
                       val selectedColor = argbEvaluator.evaluate(positionOffset, selectedDotColor, dotsColor) as Int
                       val nextColor = argbEvaluator.evaluate(positionOffset, dotsColor, selectedColor) as Int

                       nextDotBackground.setColor(nextColor)

                       if (progressMode && selectedPosition <= pager!!.currentItem) {
                           selectedDotBackground.setColor(selectedDotColor)
                       } else {
                           selectedDotBackground.setColor(selectedColor)
                       }
                   }
               }

               invalidate()
           }

           override fun resetPosition(position: Int) {
               dots[position].setWidth(dotsSize.toInt())
               refreshDotColor(position)
           }
       }
    }

    override fun refreshDotColor(index: Int) {
        val elevationItem = dots[index]
        val background = elevationItem.background as? DotsGradientDrawable?

        background?.let {
            if (index == pager!!.currentItem || progressMode && index < pager!!.currentItem) {
                background.setColor(selectedDotColor)
            } else {
                background.setColor(dotsColor)
            }
        }

        elevationItem.background = background
        elevationItem.invalidate()
    }


    override val type: Type get() = Type.DEFAULT

    //*********************************************************
    // Users Methods
    //*********************************************************
    @Deprecated("Use setSelectedDotColor() instead", ReplaceWith("setSelectedDotColor()"))
    fun setSelectedPointColor(color: Int) {
        selectedDotColor = color
    }

}
