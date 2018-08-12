package cn.nexttec.tanement.views


interface WelcomeView{
    fun showGuideViews()
    fun showLocatingTips()
    fun scrollTabs(tabIndex:Int)
    fun showError(error: String?)
    fun loginDone()
    fun timerIsUp()
}