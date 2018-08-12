package cn.nexttec.tanement.Common

import android.annotation.SuppressLint
import android.content.Context
import android.telephony.TelephonyManager
import android.util.Log
import com.amap.api.services.route.BusRouteResult
import com.google.gson.Gson
import okhttp3.MediaType
import okhttp3.RequestBody
import java.security.MessageDigest
import java.text.DecimalFormat
import kotlin.math.ceil


/**
 * Created by Administrator on 2018/5/17.
 */

infix fun <T> Iterable<T>.intersect(other: Iterable<T>) =
        this.filter {
            other.contains(it)
        }

infix fun <T> Iterable<T>.union(other: Iterable<T>) =
        this - (this intersect other) + other

fun <T> Iterable<T>.removeRepeatItem() =
        this.toSet().toMutableList()

operator fun <T> Iterable<T>.plusAssign(t: T) {
    if (!this.contains(t))
        this + t
}

operator fun <T> List<T>.div(divider:Int):List<List<T>>{
    val length = this.size
    val count = ceil(length.toDouble()/divider).toInt()
    var result = arrayListOf<List<T>>()
    for (i in 0..count-1){
        val startIndex = 10*i
        val max = 10*(i+1)
        val endIndex = if(length>max) max else length
        if(startIndex<length) {
            val subList = this.subList(startIndex, endIndex)
            result.add(subList)
        }
    }
    return result.toList()
}


val gson = { obj: Any? ->
    Gson().toJson(obj)
}

val requestBody = { obj: Any ->
    RequestBody.create(MediaType.parse("application/json"), gson(obj))
}

val pm = { context: Context ->
    context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
}

@SuppressLint("MissingPermission")
val IMEI = { pm: TelephonyManager ->
    pm.deviceId
}

@SuppressLint("MissingPermission")
val IMSI = { pm: TelephonyManager ->
    pm.subscriberId
}

val MODEL = android.os.Build.MODEL
val MANUFACTURER = android.os.Build.MANUFACTURER
val VERSIONNAME = { context: Context ->
    val info = context.packageManager.getPackageInfo(context.packageName, 0)
    info.versionName
}

val VERSIONCODE = {context:Context->
    val info = context.packageManager.getPackageInfo(context.packageName,0)
    info.versionCode
}

fun String.formatCity():String {
    if (this.contains("市"))
        return this.substringBeforeLast("市")
    return this
}

fun String.formatDistrict():String {
    if (this.contains("新区"))
        return this.substringBeforeLast("新区")
    else if (this.contains("区")){
        return this.substringBeforeLast("区")
    }
    return this
}

fun Any.li(msg:String){
    Log.i(this::class.java.simpleName,msg)
}

fun Any.le(msg:String){
    Log.e(this::class.java.simpleName,msg)
}

fun BusRouteResult.isDirectReach():Boolean {
    return this.paths.any { it.steps.sumBy { it.busLines.size }== 1 }
}

fun BusRouteResult.directLines(): List<String> {
    return this.paths.filter { it.steps.sumBy { it.busLines.size } == 1 }.map{
        it.steps[0].busLines[0].busLineName.formatBuslineName()
    }
}

fun String.formatBuslineName(): String {
    if(this.contains("("))
        return this.substringBeforeLast("(")
    return this
}

fun BusRouteResult.formateDuration():String{
    val duration = this.paths.filter { it.steps.sumBy { it.busLines.size } == 1 }
            .minBy { it.duration }!!.duration
    return if(duration > 60) "${(duration/60).toInt()}小时${duration%60}分钟" else "${duration}分钟"
}

fun BusRouteResult.traffic(keyword:String):String{
    if (this.isDirectReach()){
        val lines = this.directLines()
        val desc = lines.reduce { acc, s -> "$acc,$s" }
        return "公交距离约${this.paths[0].busDistance}米,可乘坐" +
                "${this.directLines()}到达${keyword}, 通勤时间约需${this.formateDuration()}"
    }
    else
        return ""
}

fun Any.lg(){
    val tag = this::class.java.simpleName
    Log.i(tag, gson(this))
}



fun Float.formatDistance():String{
    if(this<1000)
        return "${this.toInt()}米"
    else{
        return "${DecimalFormat("0.00").format(this/1000)}千米"
    }
}

