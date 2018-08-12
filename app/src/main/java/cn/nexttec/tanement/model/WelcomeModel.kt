package cn.nexttec.tanement.model

import android.content.Context

const val SETTING = "setting"
const val IS_FISRT_RUN = "is_first_run"

class WelcomeModel(val context: Context){

    val mPreference = context.getSharedPreferences(SETTING, Context.MODE_PRIVATE)

    fun isFirstRun():Boolean{
        return mPreference.getBoolean(IS_FISRT_RUN, true)
    }

}