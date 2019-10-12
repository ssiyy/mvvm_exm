package com.siy.mvvm.exm

import dagger.Component
import dagger.Module
import dagger.Provides
import javax.inject.Inject


/**
 * Created by Siy on 2019/10/11.
 *
 * @author Siy
 */
class Student{
    internal val name: String = "张三"
}

class ClassRoom {
    init {
        DaggerClassRoomComponent.create().inject(this)
    }

    @Inject
    lateinit var student: Student

    fun printName() = println(student.name)
}

@Module
class ClassRoomModule {
    @Provides
    fun provideStudent() = Student()
}

@Component(modules = [ClassRoomModule::class])
interface ClassRoomComponent {
    fun inject(room: ClassRoom)
}