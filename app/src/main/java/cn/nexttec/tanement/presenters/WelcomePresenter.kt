package cn.nexttec.tanement.presenters

import TanementLib.Providers.AppProvider
import android.app.Activity
import android.content.Context
import android.os.CountDownTimer
import cn.nexttec.convenienttanement.Common.PERMISSION_NEED
import cn.nexttec.rxamap.RxAMap
import cn.nexttec.rxpermission.RxPermissions
import cn.nexttec.tanement.Common.*
import cn.nexttec.tanement.TanementApp
import cn.nexttec.tanement.model.WelcomeModel
import cn.nexttec.tanement.views.WelcomeView
import cn.nexttec.tanementapi.bean.LoginRequest
import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.schedulers.Schedulers

class WelcomePresenter(private val context: Activity, private val callback: WelcomeView) {

    fun countdown() {
        val countDownTimer = object : CountDownTimer(2000, 1000) {
            override fun onFinish() {
                callback.timerIsUp()
            }

            override fun onTick(p0: Long) {
            }

        }
        countDownTimer.start()
    }

    fun login() {
        val model = WelcomeModel(context)
        val app = AppProvider.service
        val permission_denied = arrayListOf<String>()
        RxPermissions(context).requestEachCombined(PERMISSION_NEED)
                .map {
                    if (!it.granted) {
                        permission_denied += it.name
                    }
                    permission_denied
                }
                .filter {
                    it.size == 0
                }
                .flatMap {
                    callback.showLocatingTips()
                    RxAMap.locate(context)
                }
                .subscribeOn(Schedulers.newThread())
                .doOnError {
                    callback.showError(it.localizedMessage)
                }
                .observeOn(Schedulers.newThread())
                .flatMap {
                    val location = TanementApp.firstLocation
                    location.city = it.city
                    location.district = it.district
                    location.keyword = "当前位置"
                    location.name = it.address
                    location.lat = it.latitude
                    location.lon = it.longitude
                    context.li("${it.latitude},${it.longitude},${it.address}")
                    val loginRequest = LoginRequest(
                            IMEI(pm(context)),
                            IMSI(pm(context)),
                            MODEL,
                            MANUFACTURER,
                            VERSIONCODE(context),
                            it.address,
                            it.latitude,
                            it.longitude,
                            it.city
                    )
                    app.login(requestBody(loginRequest))
                }
                .subscribeOn(Schedulers.newThread())
                .subscribe {
                    context.li(it.token)
                    TanementApp.token = it.token
                    callback.loginDone()

                }


    }
}