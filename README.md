# mvvm_exm
一个MVVM架构单Activity的Android工程

## Demo示例
![demo](./img/mvvm.gif)

该示例用Navigation进行页面管理

解决了下面几个问题：
## 闪屏页面用Navigation怎么实现
### 方法一：Theme
当向用户显示初始屏幕达几秒钟时，通常会滥用初始屏幕，并且用户在已经可以与应用程序交互的同时浪费时间在初始屏幕上。取而代之的是，您应该尽快将它们带到可以与应用程序交互的屏幕。因此，以前的Splash屏幕在Android上被视为反模式。但是Google意识到，用户单击图标与您的第一个应用程序屏幕之间仍然存在短暂的窗口，可以进行交互，在此期间，您可以显示一些品牌信息。这是实现启动屏幕的正确方法。

因此，以正确的方式实施“启动画面”时，您不需要单独的“启动画面片段”，因为这会导致App加载过程中不必要的延迟。为此，您只需要特殊的主题。理论上讲，App主题可以应用于UI，并且比您的App UI初始化并变得可见的时间要早​​得多。简而言之，您只需要这样的SplashTheme即可：
```java
<style name="SplashTheme" parent="Theme.AppCompat.NoActionBar">
    <item name="android:windowBackground">@drawable/splash_background</item>
</style>
```
splash_background:
```java
<?xml version="1.0" encoding="utf-8"?>
<layer-list xmlns:android="http://schemas.android.com/apk/res/android"
    android:opacity="opaque"> <!-- android:opacity="opaque" should be here -->
    <item>
        <color android:color="@color/colorPrimary" />
    </item>
    <item>
        <bitmap
            android:antialias="true"
            android:filter="true"
            android:src="@drawable/splash" />
    </item>
</layer-list>
```
```java
<activity android:name=".ui.MainActivity"
              android:theme="@style/SplashTheme">
```
MainActivity:
```java
override fun onCreate(savedInstanceState: Bundle?) {
    setTheme(R.style.AppTheme)
    super.onCreate(savedInstanceState)
    .....
}
```
### 方法一：popUpToInclusive
```java
 <fragment
        android:id="@+id/splashFragment"
        android:name="com.siy.mvvm.exm.ui.splash.SplashFragment"
        android:label="SplashFragment">
        <action
            android:id="@+id/action_splashFragment_to_loginFragment"
            app:destination="@id/loginFragment"
            app:popUpTo="@id/splashFragment"
            app:popUpToInclusive="true" />
    </fragment>
 
    <fragment
        android:id="@+id/loginFragment"
        android:name="com.siy.mvvm.exm.ui.login.LoginFragment"
        android:label="LoginFragment"
        tools:layout="@layout/fragment_login">
        <action
            android:id="@+id/action_loginFragment_to_mainFragment"
            app:destination="@id/mainFragment" />
    </fragment>
```
注意action的属性：
```java
app:popUpTo="@id/splashFragment"       
app:popUpToInclusive="true"
```
用代码也可以实现同样的效果：
```java
navController.navigateAnimate(
                SplashFragmentDirections.actionSplashFragmentToLoginFragment(),
                navOptions {
                    popUpTo(R.id.splashFragment) {
                        inclusive = true
                    }
                })
```
个人比较喜欢用代码实现。
解释一下：
```java
 /**
     * Pop up to a given destination before navigating. This pops all non-matching destinations
     * from the back stack until this destination is found.
     */
    fun popUpTo(@IdRes id: Int, popUpToBuilder: PopUpToBuilder.() -> Unit) {
        popUpTo = id
        inclusive = PopUpToBuilder().apply(popUpToBuilder).inclusive
    }
```
popUpTo: 导航之前，弹出至给定的目的地。这将从后堆栈中弹出所有不匹配的目标，直到找到该目标为止。

id:弹出目的地，清除所有中间目的地。

inclusive:如果为true，也会从后堆栈中弹出给定的目标,false不会
## startActivityForResult用Navigation怎么实现
你在文档和官方demo中都找不到相关的内容，但是可以找到这么一句话
```java
通常，强烈建议您仅在目标之间传递最少的数据量。例如，您应该传递键来检索对象而不是传递对象本身，因为所有保存状态的总空间在Android上受到限制。如果需要传递大量数据，请考虑使用ViewModel，如在Fragments之间共享数据中所述。
```
Navigation推荐使用ViewModel在Fragment之间共享数据，这种方式在startActivityForResult并不友好。因此Google Issue Tracker有这么一个Issue：Navigation: startActivityForResult analog，但是它的优先级并不高。所以在官方给出解决方案之前我这有一种解决方式。
##### 1、定义一个这样的接口
```java
interface NavigationResult {
    fun onNavigationResult(result: Bundle)
```
##### 2、将下面的方法添加到您的Activity中
```java
fun navigateBackWithResult(result: Bundle) {
        val childFragmentManager =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment)?.childFragmentManager
        var backStackListener: FragmentManager.OnBackStackChangedListener by Delegates.notNull()
        backStackListener = FragmentManager.OnBackStackChangedListener {
            (childFragmentManager?.fragments?.get(0) as NavigationResult).onNavigationResult(result)
            childFragmentManager.removeOnBackStackChangedListener(backStackListener)
        }
        childFragmentManager?.addOnBackStackChangedListener(backStackListener)
        navController().popBackStack()
    }
```
因为从另一个Fragment分发的结果必须要经过Activity路由。
##### 3、在您要接受结果的Fragment中实现NavigationResult
## Navigation视图的状态保存
Google Issue Tracker有2个相关的Issue:

[Open fragment without lose the previous fragment states](https://issuetracker.google.com/issues/127932815)

[Transaction type is not available with Navigation Architecture Component](https://issuetracker.google.com/issues/109856764)

可以看到这2个问题下面google工程师给出的回答是：Status: Won't Fix (Intended Behavior)。
 
那么这个问题真的没有解决方案么？最终我在[Ian Lake(Android Toolkit Developer and Runner)](https://twitter.com/ianhlake/)的twitter下面找到了答案。关于这个问题的Twitter原文地址：https://twitter.com/ianhlake/status/1103522856535638016
 
|您不必每次调用onCreateView时都为新视图inflater-您可以保留对您第一次创建的View的引用，然后再次返回它。请记住，即使不缓存视图本身，Fragment视图也会自动保存和恢复其状态。如果不是这种情况，则应首先解决该问题（确保视图具有android：id等）|
|:-: | 

为什么要确保视图有id才能自动缓存视图？答案[看这里](http://www.jcodecraeer.com/a/anzhuokaifa/androidkaifa/2015/0512/2870.html)
## 数据管理
![data](https://developer.android.google.cn/topic/libraries/architecture/images/network-bound-resource.png)

Kotlin协程的实现上面的逻辑[BaseRepository.kt](https://github.com/Siy-Wu/mvvm_exm/blob/master/app/src/main/java/com/siy/mvvm/exm/base/repository/BaseRepository.kt)

详细内容可以查看这边文章：https://blog.csdn.net/baidu_34012226/article/details/102458177
