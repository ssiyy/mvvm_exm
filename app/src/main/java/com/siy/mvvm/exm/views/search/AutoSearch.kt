package com.siy.mvvm.exm.views.search

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.siy.mvvm.exm.utils.toFlowable
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.reactive.asFlow


/**
 * 1秒钟没有输入自动进行搜索
 *
 * Created by Siy on 2019/07/18.
 *
 * @author Siy
 */
abstract class AutoSearch(mOwner: LifecycleOwner, initSearchStr: String? = "") :
    CommonSearch(initSearchStr) {

    init {
        //自动搜索
        searchStr.toFlowable(mOwner).asFlow()
            .debounce(1000)
            .catch {
                emit("")
            }
            .onEach {
                searchApi(it)
            }
            .launchIn(mOwner.lifecycleScope)
        /*  .observeOn(AndroidSchedulers.mainThread())
          .`as`(mOwner.autoDisposable(Lifecycle.Event.ON_DESTROY))
          .subscribe(Consumer { searchApi(it) }, GDB_ERROR)*/
    }
}