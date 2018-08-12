package cn.nexttec.tanement.tools

import android.content.Context
import android.util.LruCache
import cn.nexttec.rxamap.RxAMap
import cn.nexttec.tanement.Common.JTools
import cn.nexttec.tanementapi.bean.Campus
import com.amap.api.services.core.LatLonPoint
import io.reactivex.schedulers.Schedulers
import java.lang.Exception


class TrafficCache(){

    private val cache = LruCache<String,String>(1000)

    fun saveTraffic(campus:Campus, latLonPoint:LatLonPoint,traffic:String){
        val str = "${campus.campus},${latLonPoint.toString()}"
        val key = JTools.stringToMD5(str)
        cache.put(key,traffic)
    }

    @Throws(NullPointerException::class)
    fun getTraffic(context: Context, campus: Campus, latLonPoint: LatLonPoint):String{
        val str = "${campus.campus},${latLonPoint.toString()}"
        val key = JTools.stringToMD5(str)
        var traffic:String
        return cache.get(key)
    }
}

