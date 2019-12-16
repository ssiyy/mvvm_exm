package com.siy.mvvm.exm

import com.siy.mvvm.exm.http.PageRes
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.Test
import kotlin.concurrent.thread

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {

    data class ListPageing<T>(
        val list: Flow<T>,
        val refresh: () -> Unit,
        val loadData: () -> Unit,
        val loadStatus: Flow<PageRes>,
        val refreshStatus: Flow<PageRes>
    )

    private fun getlistPage(): ListPageing<String> {
        //加载更多的状态,用来标识是否还可以加载更多，只有END才不可以加载更多
        val loadStatus = ConflatedBroadcastChannel<PageRes>()


        //刷新的状态
        val refreshStatus = ConflatedBroadcastChannel<PageRes>()


        val pageChannel = ConflatedBroadcastChannel<Int>()
        val listFlow = pageChannel.asFlow().map {
            "测试一下而已$it"
        }


        val refreshs = {
            refreshStatus.offer(PageRes.complete("测试一下refreshs"))
            Unit
        }

        val load = {
            loadStatus.offer(PageRes.complete("测试一下load"))
            Unit
        }

        {
            pageChannel.offer(1)
        }()

        return ListPageing(
            refresh = refreshs,
            loadData = load,
            loadStatus = loadStatus.asFlow(),
            list = listFlow,
            refreshStatus = refreshStatus.asFlow()
        )

    }

    private fun <T> flow(flow: Flow<T>) = GlobalScope.launch {
        flow.collect {
            println("refreshStatus:$it")
        }
    }

    @Test
    fun testFlow() = runBlocking {
        val search = ConflatedBroadcastChannel<Int>()

        val result = search.asFlow().map {
            println("-------------------------")
            getlistPage()
        }

        var refresh: (() -> Unit)? = null

        val job = GlobalScope.launch {
            result.collect { pageing ->
                refresh = pageing.refresh
                flow(pageing.refreshStatus)
                flow(pageing.list)
            }
        }

        thread {
            while (true) {
                Thread.sleep(1000)
                refresh?.invoke()
            }
        }

        search.offer(1)
        job.join()
        println("end")
    }

}
