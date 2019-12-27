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
import java.util.regex.Pattern

class FirstPageFragment(override val layoutId: Int = R.layout.fragment_first_page) :
    BaseLazyFragment<FragmentFirstPageBinding>() {

    override fun initViewsAndEvents(view: View) {
        mViewDataBinding.run {

            click0s = mapOf(
                "toMainPage" to { navController.navigateAnimate(MainFragmentDirections.actionMainFragmentToArticleListFragment()) },
                "toSquarePage" to { navController.navigateAnimate(MainFragmentDirections.actionMainFragmentToSquareListFragment()) },
                "forResult" to { navController.navigateAnimate(MainFragmentDirections.actionMainFragmentToToResultFragment()) }
            )


            test.filters = arrayOf(MaxNumInputFilter())

        }
    }
}


/**
 * 设置这个表示只允许输入数据类型（0123456789.+-）
 */
class MaxNumInputFilter : InputFilter {


    private val CHARACTERS =
        charArrayOf('0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '+', '-', '.')

    val floatingPattern = Pattern.compile("^([-+])?\\d+(\\.\\d+)?\$")

    private fun ok(accept: CharArray, c: Char): Boolean {
        for (i in accept.indices.reversed()) {
            if (accept[i] == c) {
                return true
            }
        }
        return false
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
        source: CharSequence?,
        start: Int,
        end: Int,
        dest: Spanned?,
        dstart: Int,
        dend: Int
    ): CharSequence? {
        Timber.e("source:$source,start:$start,end:$end,dest:$dest,dstart:$dstart,dend:$dend")
        /* val afterStr = if (source.isNullOrEmpty()) {
             //是删除操作
             dest?.replaceRange(dstart, dend, "")
         } else {
             //添加操作
             dest?.insertAt(dstart, source.subSequence(start, end))
         }
         Timber.e("afterStr:$afterStr")*/

        var ended = end

        val accept: CharArray = CHARACTERS


        var i: Int
        run {
            i = start
            while (i < ended) {
                if (!ok(accept, source!![i])) {
                    break
                }
                i++
            }
        }

        if (i == ended) { // It was all OK.
            return null
        }

        if (ended - start == 1) { // It was not OK, and there is only one char, so nothing remains.
            return ""
        }

        val filtered = SpannableStringBuilder(source, start, ended)
        i -= start
        ended -= start

        // Only count down to i because the chars before that were all OK.
        // Only count down to i because the chars before that were all OK.
        for (j in ended - 1 downTo i) {
            if (!ok(accept, source!![j])) {
                filtered.delete(j, j + 1)
            }
        }

        return filtered

    }
}
