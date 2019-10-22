package com.siy.mvvm.exm.base.glide

import android.content.Context
import com.bumptech.glide.Glide
import com.bumptech.glide.GlideBuilder
import com.bumptech.glide.Registry
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.integration.okhttp3.OkHttpUrlLoader
import com.bumptech.glide.load.engine.bitmap_recycle.LruBitmapPool
import com.bumptech.glide.load.engine.cache.DiskLruCacheFactory
import com.bumptech.glide.load.engine.cache.ExternalCacheDiskCacheFactory
import com.bumptech.glide.load.engine.cache.ExternalPreferredCacheDiskCacheFactory
import com.bumptech.glide.load.engine.cache.LruResourceCache
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.module.AppGlideModule
import com.siy.mvvm.exm.base.GbdApplication
import com.siy.mvvm.exm.utils.exCacheDir
import java.io.File
import java.io.InputStream

@GlideModule
class GlideConfigModule : AppGlideModule() {

    companion object {
        const val memoryCacheSizeBytes = 1024 * 1024 * 20L // 20mb
        const val bitmapPoolSizeBytes = 1024 * 1024 * 30L // 30mb
        const val diskCacheSizeBytes = 1024 * 1024 * 100L // 100 MB
    }

    override fun applyOptions(context: Context, builder: GlideBuilder) {
        builder.setDiskCache(DiskLruCacheFactory(DiskLruCacheFactory.CacheDirectoryGetter { File(GbdApplication.instance.exCacheDir, "glide") }, diskCacheSizeBytes))
        builder.setMemoryCache(LruResourceCache(memoryCacheSizeBytes))
        builder.setBitmapPool(LruBitmapPool(bitmapPoolSizeBytes))
    }

    override fun registerComponents(context: Context, glide: Glide, registry: Registry) {
        registry.replace(GlideUrl::class.java, InputStream::class.java, OkHttpUrlLoader.Factory())
    }

}