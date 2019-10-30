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
