package com.siy.mvvm.exm.ui.main

import android.text.InputFilter
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.view.View
import com.siy.mvvm.exm.R
import com.siy.mvvm.exm.base.ui.BaseLazyFragment
import com.siy.mvvm.exm.base.ui.navigateAnimate
import com.siy.mvvm.exm.databinding.FragmentFirstPageBinding
import timber.log.Timber

class FirstPageFragment(override val layoutId: Int = R.layout.fragment_first_page) :
    BaseLazyFragment<FragmentFirstPageBinding>() {

    override fun initViewsAndEvents(view: View) {
        mViewDataBinding.run {

            click0s = mapOf(
                "toMainPage" to { navController.navigateAnimate(MainFragmentDirections.actionMainFragmentToArticleListFragment()) },
                "toSquarePage" to { navController.navigateAnimate(MainFragmentDirections.actionMainFragmentToSquareListFragment()) },
                "forResult" to { navController.navigateAnimate(MainFragmentDirections.actionMainFragmentToToResultFragment()) }
            )


            test.filters = arrayOf(*(test.filters), AccuracyFilter(500.0, 2))

        }
    }
}

class AccuracyFilter(private val _maxNum: Double, private val accuracy: Int) :
    DigitsInputFilter(false, accuracy > 0) {

    private val maxNum: Double
        get() {
            return if (_maxNum < 0) {
                0.0
            } else {
                _maxNum
            }
        }

    override fun filter(
        source: CharSequence,
        start: Int,
        end: Int,
        dest: Spanned,
        dstart: Int,
        dend: Int
    ): CharSequence? {
        val out = super.filter(source, start, end, dest, dstart, dend)
        var innerSource = source
        var innerStart = start
        var innerEnd = end

        if (out != null) {
            innerSource = out
            innerStart = 0
            innerEnd = out.length
        }

        log(innerSource, innerStart, innerEnd, dest, dstart, dend)

        val afterStr = if (innerSource.isEmpty()) {
            //是删除操作
            dest.replaceRange(dstart, dend, "")
        } else {
            //添加操作
            dest.insertAt(dstart, innerSource.subSequence(innerStart, innerEnd))
        }


        //判断一下小数位的精度
        val decimalPointIndex = afterStr.indexOf(mDecimalPointChars)
        if (decimalPointIndex != -1) {
            val innerAccuracy = afterStr.length-1 - decimalPointIndex
            if (innerAccuracy > accuracy) {
                return ""
            }
        }

        val result = try {
            afterStr.toString().toDouble()
        } catch (e: Exception) {
            0.0
        }

        val innerMaxNum = if (accuracy == 0) {
            maxNum.toInt().toDouble()//把小数位截掉
        } else {
            maxNum
        }

        if (result > innerMaxNum) {
            return ""
        }

        return null
    }
}


open class DigitsInputFilter constructor(
    private val mSign: Boolean,
    private val mDecimal: Boolean
) :
    NumberInputFilter() {

    override val acceptedChars: CharArray =
        COMPATIBILITY_CHARACTERS[(if (mSign) SIGN else 0) or (if (mDecimal) DECIMAL else 0)]

    protected val mDecimalPointChars: String = DEFAULT_DECIMAL_POINT_CHARS
    protected val mSignChars = DEFAULT_SIGN_CHARS

    companion object {
        private val COMPATIBILITY_CHARACTERS = arrayOf(
            charArrayOf('0', '1', '2', '3', '4', '5', '6', '7', '8', '9'),
            charArrayOf('0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '-', '+'),
            charArrayOf('0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '.'),
            charArrayOf('0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '-', '+', '.')
        )

        private const val DEFAULT_DECIMAL_POINT_CHARS = "."
        private const val DEFAULT_SIGN_CHARS = "-+"

        private const val SIGN = 1
        private const val DECIMAL = 2

        fun signlFilter() = DigitsInputFilter(mSign = true, mDecimal = false)

        fun decimalFilter() = DigitsInputFilter(mSign = false, mDecimal = true)

        fun signDecimalFilter() = DigitsInputFilter(mSign = true, mDecimal = true)

    }


    private fun isSignChar(c: Char): Boolean {
        return mSignChars.indexOf(c) != -1
    }

    private fun isDecimalPointChar(c: Char): Boolean {
        return mDecimalPointChars.indexOf(c) != -1
    }


    override fun filter(
        source: CharSequence,
        start: Int,
        end: Int,
        dest: Spanned,
        dstart: Int,
        dend: Int
    ): CharSequence? {
        val out = super.filter(source, start, end, dest, dstart, dend)

        if (!mSign && !mDecimal) {
            return out
        }

        var innerSource = source
        var innerStart = start
        var innerEnd = end

        if (out != null) {
            innerSource = out
            innerStart = 0
            innerEnd = out.length
        }

        var sign = -1
        var decimal = -1
        val dlen = dest.length

        //找出现有文本是否带有符号或小数点字符。
        for (i in 0 until dstart) {
            val c = dest[i]
            if (isSignChar(c)) {
                sign = i
            } else if (isDecimalPointChar(c)) {
                decimal = i
            }
        }

        for (i in dend until dlen) {
            val c = dest[i]
            if (isSignChar(c)) {
                return "" // 请勿在符号字符前插入任何内容。
            } else if (isDecimalPointChar(c)) {
                decimal = i
            }
        }

        //如果是这样，我们必须从源中删除它们。此外，符号字符必须是第一个字符，
        // 并且在现有符号字符之前不能插入任何内容。按相反的顺序进行操作，以使偏移量稳定。
        var stripped: SpannableStringBuilder? = null

        for (i in innerEnd - 1 downTo innerStart) {
            val c = innerSource[i]
            var strip = false
            if (isSignChar(c)) {
                if (i != innerStart || dstart != 0) {
                    strip = true
                } else if (sign >= 0) {
                    strip = true
                } else {
                    sign = i
                }
            } else if (isDecimalPointChar(c)) {
                if (decimal >= 0) {
                    strip = true
                } else {
                    decimal = i
                }
            }
            if (strip) {
                if (innerEnd == innerStart + 1) {
                    return "" // Only one character, and it was stripped.
                }
                if (stripped == null) {
                    stripped = SpannableStringBuilder(innerSource, innerStart, innerEnd)
                }
                stripped.delete(i - innerStart, i + 1 - innerStart)
            }
        }

        return stripped ?: out
    }

}

fun CharSequence.insertAt(index: Int, str: CharSequence): CharSequence {
    if (index < 0 || index > length) {
        throw StringIndexOutOfBoundsException()
    }

    return "${subSequence(0, index)}$str${substring(index, length)}"
}

abstract class NumberInputFilter : InputFilter {

    /**
     * 可接受字符
     */
    protected abstract val acceptedChars: CharArray

    protected fun log(
        source: CharSequence?,
        start: Int,
        end: Int,
        dest: Spanned?,
        dstart: Int,
        dend: Int
    ) {

        Timber.e("source:$source,start:$start,end:$end,dest:$dest,dstart:$dstart,dend:$dend")
        val afterStr = if (source.isNullOrEmpty()) {
            //是删除操作
            dest?.replaceRange(dstart, dend, "")
        } else {
            //添加操作
            dest?.insertAt(dstart, source.subSequence(start, end))

        }
        Timber.e("afterStr:$afterStr")
    }

    /**
     * @param source 为即将输入的字符串
     * @param start source的start, start 为0
     * @param end source的end ，因为start为0，end也可理解为source长度了
     * @param dest 输入框中原来的内容
     * @param dstart 要替换或者添加的起始位置，即光标所在的位置
     * @param dend 要替换或者添加的终止始位置，若为选择一串字符串进行更改，则为选中字符串 最后一个字符在dest中的位置
     *
     * @return null表示原始输入，""表示不接受输入，其他字符串表示变化值
     */
    override fun filter(
        source: CharSequence,
        start: Int,
        end: Int,
        dest: Spanned,
        dstart: Int,
        dend: Int
    ): CharSequence? {
        log(source, start, end, dest, dstart, dend)

        val accept = acceptedChars

        var i = start
        while (i < end) {
            if (!ok(accept, source[i])) {
                break
            }
            i++
        }

        if (i == end) {
            //全部都输入可接受的字符
            return null
        }

        if (end - start == 1) {
            //不好，只有一个字符，所以什么也没剩下。
            return ""
        }

        var innerEnd = end
        val filtered = SpannableStringBuilder(source, start, innerEnd)
        i -= start
        innerEnd -= start

        for (j in innerEnd - 1 downTo i) {
            if (!ok(accept, source[j])) {
                filtered.delete(j, j + 1)
            }
        }

        return filtered
    }

    protected open fun ok(accept: CharArray, c: Char): Boolean {
        for (i in accept.indices.reversed()) {
            if (accept[i] == c) {
                return true
            }
        }
        return false
    }
}
