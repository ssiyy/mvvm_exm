package com.siy.mvvm.exm.views.search

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import com.siy.mvvm.exm.utils.GDB_ERROR
import com.siy.mvvm.exm.utils.autoDisposable
import com.siy.mvvm.exm.utils.toFlowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.Consumer
import java.util.concurrent.TimeUnit


/**
 * 1秒钟没有输入自动进行搜索
 *
 * Created by Siy on 2019/07/18.
 *
 * @author Siy
 */
abstract class AutoSearch( mOwner: LifecycleOwner) : CommonSearch() {

    init {
        //自动搜索
        searchStr.toFlowable(mOwner)
            .debounce(1, TimeUnit.SECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .`as`(mOwner.autoDisposable(Lifecycle.Event.ON_DESTROY))
            .subscribe(Consumer { searchApi(it) }, GDB_ERROR)
    }
}