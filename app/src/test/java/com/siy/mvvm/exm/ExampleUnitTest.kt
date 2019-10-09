package com.siy.mvvm.exm

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        flowOf(1)
            .onStart{
                delay(1000)
                println(Thread.currentThread().name)
            }
           // .flowOn(Dispatchers.IO)
            .catch {

            }
            .onEach {
                println(it)
                println(Thread.currentThread().name)
            }


        Thread.sleep(5*1000)
    }
}
