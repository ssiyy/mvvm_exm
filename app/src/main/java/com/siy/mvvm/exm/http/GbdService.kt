package com.siy.mvvm.exm.http



/**
 * Created by Siy on 2019/08/08.
 *
 * @author Siy
 */
interface GbdService {

 /*   companion object {
        *//**
         * 正式环境ip
         *//*
        private const val FORMAL_IP = "61.235.77.89"

        *//**
         * 正式环境端口
         *//*
        private const val FORMAL_PORT = "8081"

        *//**
         * 测试环境ip
         *//*
        private const val TEST_IP = "192.168.0.4"

        *//**
         * 测试环境端口
         *//*
        private const val TEST_PORT = "8087"

        const val URL = "http://$TEST_IP:$TEST_PORT"


        private const val GBD = "GBD"

        const val PREFIX = GBD
    }

    *//**
     * 用户登录
     *
     * @param map 登录请求参数
     * @return
     *//*
    @FormUrlEncoded
    @Headers("Content-Type: application/x-www-form-urlencoded;charset=UTF-8")
    @POST("$PREFIX/login")
    fun loginUserObservable(@FieldMap map: Map<String, @JvmSuppressWildcards Any>): Observable<BaseBean<User>>

    *//**
     * 用户登录
     *
     * @param map 登录请求参数
     * @return
     *//*
    @FormUrlEncoded
    @Headers("Content-Type: application/x-www-form-urlencoded;charset=UTF-8")
    @POST("$PREFIX/login")
    suspend fun loginUser(@FieldMap map: Map<String, @JvmSuppressWildcards Any>): BaseBean<User>

    *//**
     * 人脸注册
     *//*
    @FormUrlEncoded
    @Headers("Content-Type: application/x-www-form-urlencoded;charset=UTF-8")
    @POST("$PREFIX/face2/reg")
    suspend fun regface(@Field("jsonParams") base64Encode: String): BaseBean<Any>

    *//**
     * 获取岗位职责列表
     *
     * @param map
     * @return
     *//*
    @Headers("Content-Type: application/x-www-form-urlencoded;charset=UTF-8")
    @POST("$PREFIX/postAccountability/list")
    suspend fun getAccountabilityList(@QueryMap map: Map<String, String>): BaseBean<ResultRsp<Accountability>>


    *//**
     * 获取工作项目库
     *
     * @return
     *//*
    @Headers("Content-Type: application/x-www-form-urlencoded;charset=UTF-8")
    @POST("$PREFIX/workItem/list")
    suspend fun getWorkItems(@QueryMap map: Map<String, String>): BaseBean<ResultRsp<WorkItem>>

    *//**
     * 新增或者修改工作项目
     *
     * @param reqstr
     * @return
     *//*
    @Headers("Content-Type: application/x-www-form-urlencoded;charset=UTF-8")
    @POST("$PREFIX/workItem/save")
    suspend fun addOrUpdateWorkItem(@Query("jsonParams") reqstr: String): BaseBean<Any>

    *//**
     * 删除工作项目
     *
     * @param map
     * @return
     *//*
    @Headers("Content-Type: application/x-www-form-urlencoded;charset=UTF-8")
    @POST("$PREFIX/workItem/delete")
    suspend fun deleteWorkItem(@QueryMap map: Map<String, String>): BaseBean<Any>*/
}