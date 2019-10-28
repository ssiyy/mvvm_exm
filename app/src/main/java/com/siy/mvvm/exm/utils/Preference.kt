package com.siy.mvvm.exm.utils

import android.content.Context
import android.content.SharedPreferences
import com.siy.mvvm.exm.base.MvvmApplication
import timber.log.Timber
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.*
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty


/**
 * Created by Siy on 2019/07/29.
 *
 *
 * @author Siy
 */
class Preference<T>(
    /**
     * 存储值得key
     */
    val name: String,
    /**
     * 存储值得默认值
     */
    val default: T,
    /**
     * 存储的文件名
     */
    prefName: String = "gbd_sp",
    /**
     * 存储的模式
     */
    mode: MODE = MODE.STROGE_SP
) : ReadWriteProperty<Any?, T> {

    enum class MODE {
        /**
         * sp存储是存放在私有缓存中的
         */
        STROGE_SP,
        /**
         * file存储是存放在外置的公有缓存中的
         */
        STROGE_FILE
    }

    private val prefs: FileWriterReader by lazy {
        Timber.d(prefName)
        if (mode == MODE.STROGE_SP) {
            SpFileWriterReader(MvvmApplication.instance.getSharedPreferences(prefName, Context.MODE_PRIVATE))
        } else {
            val file = File(MvvmApplication.instance.exCacheDir, "crashlog")
            if (!file.exists()) {
                file.mkdir()
            }

            PropertiesWriterReader(File(file, "$prefName.txt"))
        }
    }

    constructor(default: T, prefName: String = "gbd_sp", mode: MODE = MODE.STROGE_SP) : this(
        "",
        default,
        prefName,
        mode
    )


    override fun getValue(thisRef: Any?, property: KProperty<*>): T {
        return findPreference(findProperName(property), default)
    }


    override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        putPreference(findProperName(property), value)
    }

    private fun findProperName(property: KProperty<*>) = if (name.isEmpty()) property.name else name

    private fun <U> findPreference(name: String, default: U): U = with(prefs) {
        val res: Any = when (default) {
            is Long -> getLong(name, default)
            is String -> getString(name, default)
            is Int -> getInt(name, default)
            is Boolean -> getBoolean(name, default)
            is Float -> getFloat(name, default)
            else -> throw IllegalArgumentException("Unsupport type")
        }

        @Suppress("UNCHECKED_CAST")
        res as U
    }

    private fun <U> putPreference(name: String, value: U) = with(prefs) {
        when (value) {
            is Long -> putLong(name, value)
            is String -> putString(name, value)
            is Int -> putInt(name, value)
            is Boolean -> putBoolean(name, value)
            is Float -> putFloat(name, value)
            else -> throw IllegalArgumentException("Unsupport type")
        }
    }
}


private interface FileWriterReader {
    fun getString(key: String, defValue: String): String

    fun getLong(key: String, defValue: Long): Long

    fun getInt(key: String, defValue: Int): Int

    fun getBoolean(key: String, defValue: Boolean): Boolean

    fun getFloat(key: String, defValue: Float): Float

    fun putString(key: String, value: String)

    fun putLong(key: String, value: Long)

    fun putInt(key: String, value: Int)

    fun putBoolean(key: String, value: Boolean)

    fun putFloat(key: String, value: Float)

}

private class SpFileWriterReader(private val sp: SharedPreferences) : FileWriterReader {
    override fun putString(key: String, value: String) = with(sp.edit()) {
        putString(key, value).apply()
    }

    override fun putLong(key: String, value: Long) = with(sp.edit()) {
        putLong(key, value).apply()
    }

    override fun putInt(key: String, value: Int) = with(sp.edit()) {
        putInt(key, value).apply()
    }

    override fun putBoolean(key: String, value: Boolean) = with(sp.edit()) {
        putBoolean(key, value).apply()
    }

    override fun putFloat(key: String, value: Float) = with(sp.edit()) {
        putFloat(key, value).apply()
    }

    override fun getString(key: String, defValue: String) = sp.getString(key, defValue)

    override fun getLong(key: String, defValue: Long) = sp.getLong(key, defValue)

    override fun getInt(key: String, defValue: Int) = sp.getInt(key, defValue)

    override fun getBoolean(key: String, defValue: Boolean) = sp.getBoolean(key, defValue)

    override fun getFloat(key: String, defValue: Float) = sp.getFloat(key, defValue)
}

private class PropertiesWriterReader(private val file: File) : FileWriterReader {

    override fun getString(key: String, defValue: String) = if (file.exists()) {
        Properties().apply {
            FileInputStream(file).use {
                load(it)
            }
        }.getProperty(key, defValue)
    } else {
        defValue
    }

    override fun getLong(key: String, defValue: Long) = getString(key, defValue.toString()).toLong()

    override fun getInt(key: String, defValue: Int) = getString(key, defValue.toString()).toInt()

    override fun getBoolean(key: String, defValue: Boolean) = getString(key, defValue.toString()).toBoolean()

    override fun getFloat(key: String, defValue: Float) = getString(key, defValue.toString()).toFloat()

    override fun putString(key: String, value: String) {
        if (file.exists()) {
            Properties().run {
                FileInputStream(file).use {
                    load(it)
                }

                FileOutputStream(file).use {
                    setProperty(key, value)
                    store(it, "crash_log")
                }
            }
        } else {
            FileOutputStream(file).use {
                val properties = Properties()
                properties[key] = value
                properties.store(it, "crash_log")
                it.flush()
            }
        }
    }

    override fun putLong(key: String, value: Long) = putString(key, value.toString())

    override fun putInt(key: String, value: Int) = putString(key, value.toString())

    override fun putBoolean(key: String, value: Boolean) = putString(key, value.toString())

    override fun putFloat(key: String, value: Float) = putString(key, value.toString())
}