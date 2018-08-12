package cn.nexttec.tanement.presenters

import cn.nexttec.tanementapi.bean.House

interface IMainPresenter {
    fun showNeabyHouses(houses: List<House>)

}
