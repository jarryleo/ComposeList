package cn.leo.compose_list.model

import androidx.lifecycle.ViewModel
import cn.leo.compose_list.App
import cn.leo.compose_list.net.Apis
import cn.leo.compose_list.net.Urls
import cn.leo.compose_list.net.http.OkHttp3Creator
import cn.leo.compose_list.net.http.ServiceCreator
import cn.leo.compose_list.net.interceptor.CacheInterceptor
import cn.leo.compose_list.net.interceptor.LoggerInterceptor

open class BaseViewModel : ViewModel() {

    companion object {
        val api by lazy {
            ServiceCreator.create(Apis::class.java) {
                baseUrl = Urls.baseUrlZhiHu
                httpClient = OkHttp3Creator.build {
                    //缓存文件夹
                    cacheDir = App.context?.cacheDir
                    //网络请求日志打印拦截器
                    addInterceptor(LoggerInterceptor())
                    //接口缓存拦截器
                    addInterceptor(CacheInterceptor())
                }
            }
        }
    }
}