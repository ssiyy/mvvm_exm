package com.siy.mvvm.exm

import dagger.Component
import dagger.Module
import dagger.Provides
import dagger.Subcomponent
import javax.inject.Inject


/**
 * 用来测试Dagger2
 *
 * Created by Siy on 2019/10/11.
 *
 * @author Siy
 */

class Father {
    val name = "老王"
}

@Module(subcomponents = [SonComponent::class])
class FatherModule {
    @Provides
    fun providerFather() = Father()
}

@Component(modules = [FatherModule::class])
interface FatherComponent {
    fun buildChildComponent(): SonComponent.Builder
}

class Son(private val father: Father) {
    val fatherName: String
        get() = father.name

}

@Module
class SonModule {
    @Provides
    fun providerSon(father: Father) = Son(father)
}

@Subcomponent(modules = [SonModule::class])
interface SonComponent {
    fun inject(runner: Runner)

    @Subcomponent.Builder
    interface Builder {
        fun build(): SonComponent
    }
}

class Runner {
    init {
       DaggerFatherComponent.create().buildChildComponent().build().inject(this)
    }

    @Inject
    lateinit var son: Son

    fun runner() {
        println(son.fatherName)
    }
}

fun main() {
    Runner().runner()
}