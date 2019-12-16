package com.siy.mvvm.exm

import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactive.asPublisher
import kotlinx.coroutines.runBlocking
import org.junit.Test
import kotlin.concurrent.thread

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {

    fun requestFlow(i: Int): Flow<String> = flow {
        emit("$i: First")
        delay(99) // wait 500 ms
        emit("$i: Second")
    }

    class T(
        val msg: String,
        val flow: Flow<String>,
        val run: () -> Unit
    )


    suspend fun testRun(): T {
        var i = 1
        var str = "ce,shi"
        val dfe = ConflatedBroadcastChannel<String>()

        val lamda = {
            str = "ce,shi：${i++}"
            if (!dfe.isClosedForSend) {
                dfe.offer(str)
            }
            Unit
        }
        return T(msg = "测试", run = lamda,flow = dfe.asFlow())
    }

    @Test
    fun testFlow() = runBlocking<Unit> {
        val abc = ConflatedBroadcastChannel<Int>()
        val t = abc.asFlow().map {
            testRun()
        }
        abc.offer(1)
        var runner: (() -> Unit)? = null
        launch {
            t.collect {
                println(it.msg)
                runner = it.run
            }
            println("试试嘿嘿")
        }

        launch {
            t.flatMapMerge {
                it.flow.asPublisher().asFlow()
            }.collect{
                println("aa")
            }
        }

        thread {
            while (true){
                runner?.invoke()
                Thread.sleep(1000)
            }
        }

    }

}
