package com.dontsu.dotindicators

abstract class OnPageChangeListenerHelper {
    private var lastLeftPosition: Int = -1
    private var lastRightPosition: Int = -1

    internal abstract val pageCount: Int

    fun onPageScrolled(position: Int, positionOffset: Float) {
        var offset = (position + positionOffset)
        val lastPageIndex = (pageCount - 1).toFloat()
        if (offset == lastPageIndex) {
            offset = lastPageIndex - .0001f // 왜 이러게 한걸까? 미세한 움직임을 감지하기 위함인가?
        }

        // 포지션 구하기
        val leftPosition = offset.toInt()
        val rightPosition = leftPosition + 1

        if (rightPosition > lastPageIndex || leftPosition == -1) {
            // 둘 다 범위를 넘어가는 상황이라 return
            return
        }

        onPageScrolled(leftPosition, rightPosition, offset % 1) // 현재 메서드와 동일한 이름임에도 recursive 되지 않고 하단에 만들어놓은 onPageScrolled() 추상 메서드를 가리킴.

        if (lastLeftPosition != -1) { // -1이 아니면 lastLeftPosition을 새롭게 할당할 수 있다.
            if (leftPosition > lastLeftPosition) {
                (lastLeftPosition until leftPosition).forEach {
                    resetPosition(it)
                }
            }

            if (rightPosition < lastRightPosition) {
                resetPosition(lastRightPosition)
                ((rightPosition + 1)..lastRightPosition).forEach {
                    resetPosition(it)
                }
            }
        }

        lastLeftPosition = leftPosition
        lastRightPosition = rightPosition
    }

    internal abstract fun onPageScrolled(selectedPosition: Int, nextPosition: Int,
                                         positionOffset: Float)

    internal abstract fun resetPosition(position: Int)

}
