package com.siy.mvvm.exm.utils

import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.Consumer
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import timber.log.Timber


/**
 * Created by Siy on 2019/08/23.
 *
 * @author Siy
 */
class TaskQuenueManager<T> {
    /**
     *存储执行任务的Observable,采用的任务队列的模式，
     * 所以一定要保持任务队列的执行的连续性
     */
    private val mObservableList = mutableListOf<Pair<Single<T>, Consumer<T>>>()

    private val disposes = CompositeDisposable()

    @Volatile
    private var running = false

    /**
     * 添加一个任务
     */
    fun addObservablePair(taskPair: Pair<Single<T>, Consumer<T>>) {
        Timber.d("添加了一组任务：$taskPair")

        synchronized(mObservableList) {
            if (mObservableList.isEmpty() && !running) {
                //如果任务队列是空，并且没有正在运行就直接运行
                running = true
                val observable = taskPair.first

            } else {
                mObservableList.add(taskPair)
            }
        }
    }

    /**
     * 运行任务
     */
    fun runObservablePair() {
        synchronized(mObservableList) {
            running = false
            if (mObservableList.isNotEmpty()) {
                val pair = mObservableList[0]
                mObservableList.removeAt(0)
                running = true

                val observable = pair.first
                val observer = TaskQuenueObserver(pair.second, this)
                disposes.add(observer)
                observable.subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(observer)
                Timber.d("运行了一组任务,还有${mObservableList.size}组任务")
            }
        }
    }

    /**
     * 停止任务队列
     */
    fun stopTaskQuenue() {
        synchronized(mObservableList) {
            if (mObservableList.isNotEmpty()) {
                mObservableList.clear()
            }
            disposes.dispose()
            Timber.d("停止了任务队列,还有${mObservableList.size}组任务")
        }
    }

}


class TaskQuenueObserver<E>(
    private val consumer: Consumer<E>,
    private val manager: TaskQuenueManager<E>
) : DisposableSingleObserver<E>() {
    override fun onSuccess(t: E) {
        try {
            consumer.accept(t)
        } catch (e: Exception) {
            //如果异常了最好也不要断开
            manager.runObservablePair()
        }
    }

    override fun onError(e: Throwable) {
        Timber.e(e.detailMsg)
        manager.runObservablePair()
    }
}