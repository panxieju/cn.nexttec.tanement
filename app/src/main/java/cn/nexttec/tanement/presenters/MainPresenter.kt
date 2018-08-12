package cn.nexttec.tanement.presenters

import TanementLib.Providers.HouseProvider
import android.content.Context
import cn.nexttec.rxamap.RxAMap
import cn.nexttec.tanement.Common.*
import cn.nexttec.tanement.TanementApp
import cn.nexttec.tanementapi.api.TrafficApi
import cn.nexttec.tanementapi.bean.NearbyQuery
import cn.nexttec.tanementapi.bean.ReachStationQuery
import cn.nexttec.tanementapi.bean.StationHouseQuery
import cn.nexttec.tanementlib.Providers.TrafficProvider
import com.amap.api.services.core.LatLonPoint
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.schedulers.Schedulers


class MainPresenter(private val context: Context, private val callback: IMainPresenter) {
    val location = TanementApp.firstLocation
    val token = TanementApp.token
    val address = location.name
    val keyword = "当前位置"
    val city = location.city
    val lat = location.lat
    val lon = location.lon
    val district = location.district
    val houseApi = HouseProvider.service
    val trafficApi = TrafficProvider.service
    var distanceLimit = 10*1000

    //搜索周边的房源
    fun searchNeabyHouse() {
        val query = NearbyQuery(city, address, district, lat, lon, token)
        li(gson(query))

        houseApi.getNearbyHouse(requestBody(query))
                .subscribeOn(Schedulers.newThread())
                .subscribe {
                    if (it.success == 1 && it.count > 0) {
                        callback.showNeabyHouses(it.houses)
                    }
                }
    }

    fun seachReachableHouse() {
        val reachStationsQuery = ReachStationQuery(token, lat, lon, city)
        //log output
        var index = 0
        reachStationsQuery.lg()
        trafficApi.getReachStations(requestBody(reachStationsQuery))
                .subscribeOn(Schedulers.newThread())
                .observeOn(Schedulers.io())
                .filter{
                    it.success == 1 && it.count>0
                }
                .flatMap {
                    val stationList = it.stations / 10
                    Observable.fromIterable(stationList)
                }
                .flatMap {
                    val stationHouseQuery = StationHouseQuery(
                            token,
                            it,
                            lat,
                            lon,
                            city
                    )
                    stationHouseQuery.lg()
                    houseApi.getReachableHouse(requestBody(stationHouseQuery))
                }
                .filter {
                    it.success==1 && it.count>0
                }
                .subscribe{
                    callback.showNeabyHouses(it.houses.filter { it.distance<distanceLimit })
                }




    }
}