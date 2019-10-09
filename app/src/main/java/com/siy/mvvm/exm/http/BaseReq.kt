package com.siy.mvvm.exm.http

import com.siy.mvvm.exm.utils.detailMsg
import timber.log.Timber
import java.lang.reflect.Modifier


/**
 * Created by Siy on 2019/07/23.
 *
 * @author Siy
 */
open class BaseReq {

    val reqMap: Map<String, Any>
        get() {
            val fields = this::class.java.declaredFields
            val map = HashMap<String, Any>()
            fields.forEach continuing@{ item ->
                if (!item.isAccessible) {
                    item.isAccessible = true
                }

                if (Modifier.isStatic(item.modifiers)) {
                    return@continuing
                }

                try {
                    val reqIgnore = item.getAnnotation(ReqIgnore::class.java)
                    if (reqIgnore == null) {
                        //等于null就代表不忽略

                        //如果值为null，也过滤掉
                        val fieldValue = item.get(this) ?: return@continuing

                        val reqName = item.getAnnotation(ReqName::class.java)
                        if (reqName != null) {
                            //不为null，就去取值
                            val name = reqName.reqName
                            if (name.isEmpty()) {
                                //reqName为空的就表示没有设值，用字段名就可以了
                                map[item.name] = fieldValue
                            } else {
                                //有值就用注解设置的
                                map[name] = fieldValue
                            }
                        } else {
                            map[item.name] = fieldValue
                        }
                    }
                } catch (e: Exception) {
                    Timber.e(e.detailMsg)
                }
            }
            return map
        }

}