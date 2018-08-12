package cn.nexttec.tanement.bean

import cn.nexttec.tanement.Common.formatDistance
import com.amap.api.maps.AMapUtils
import com.amap.api.maps.model.LatLng
import java.text.DecimalFormat


class Location() {

    lateinit var keyword: String
    lateinit var name: String
    lateinit var city: String
    lateinit var district: String
    var lat: Double = 0.0
    var lon: Double = 0.0

    fun reset() {
        keyword = ""
        name = ""
        city = ""
        district = ""
        lat = 0.toDouble()
        lon = 0.toDouble()
    }
}

fun Location.distance(lat:Double, lon:Double) =
    AMapUtils.calculateLineDistance(LatLng(this.lat, this.lon), LatLng(lat, lon))
