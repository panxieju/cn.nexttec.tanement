package cn.nexttec.tanement

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.support.v4.view.ViewPager
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import cn.nexttec.tanement.Common.li
import cn.nexttec.tanement.presenters.WelcomePresenter
import cn.nexttec.tanement.views.WelcomeView
import com.facebook.drawee.view.SimpleDraweeView
import com.romainpiel.shimmer.Shimmer
import com.romainpiel.shimmer.ShimmerTextView
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import net.frakbot.jumpingbeans.JumpingBeans


const val MSG_SHOW_JUMPING_TEXT = 0x0001
const val MSG_ERROR = 0x0002

class WelcomeActivity : AppCompatActivity(), WelcomeView {


    private lateinit var welcome_viewpager: ViewPager
    private lateinit var welcome_imageview: SimpleDraweeView
    private lateinit var shimmer_text: ShimmerTextView
    private lateinit var tabs: List<View>

    private lateinit var handler: Handler

    private var isTimerUp: Boolean = false
    private var isLogin: Boolean = false
    private var isUpdating: Boolean = false
    private var isNeedUpdate: Boolean = false
    private var isMustUpdate: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.activity_welcome)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN)

        initWidgets()
        initProcess()
    }


    private fun initWidgets() {

        welcome_imageview = findViewById(R.id.welcome_imageview) as SimpleDraweeView
        with(welcome_imageview) {
            Observable.just("image/welcome.jpg")
                    .subscribeOn(Schedulers.newThread())
                    .flatMap {
                        val inputStream = assets.open(it)
                        val bitmap = BitmapFactory.decodeStream(inputStream)
                        Observable.just(bitmap)
                    }
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe {
                        setImageBitmap(it)
                    }
        }

        welcome_viewpager = findViewById(R.id.welcome_viewpager) as ViewPager
        with(welcome_viewpager) {
            visibility = View.GONE
        }

        shimmer_text = findViewById(R.id.shimmer_text) as ShimmerTextView
        with(shimmer_text) {
            visibility = View.GONE
        }

        tabs = listOf(
                findViewById(R.id.tab_index0),
                findViewById(R.id.tab_index1),
                findViewById(R.id.tab_index2)
        )
        with(tabs) {
            forEach { it.visibility = View.GONE }
        }

    }

    private lateinit var presenter: WelcomePresenter
    private fun initProcess() {
        handler = Handler(Handler.Callback {
            when (it.what) {
                MSG_SHOW_JUMPING_TEXT -> {
                    showLocatingTip()
                }

                MSG_ERROR -> {
                    showErrorToast(it.obj as String)
                }
            }
            false
        })

        presenter = WelcomePresenter(this, this)
        presenter.countdown()
        presenter.login()
    }


    override fun showGuideViews() {
    }

    override fun showLocatingTips() {
        handler.sendEmptyMessage(MSG_SHOW_JUMPING_TEXT)
    }

    override fun scrollTabs(tabIndex: Int) {
    }

    override fun showError(error: String?) {
        val message = Message()
        message.obj = error
        message.what = MSG_ERROR
    }

    override fun loginDone() {
        isLogin = true
        switchMainActivity()
    }

    fun switchMainActivity() {
        if (isTimerUp && isLogin) {
            val intent = Intent(this@WelcomeActivity, MainActivity::class.java)
            startActivity(intent)
        }
    }

    override fun timerIsUp() {
        isTimerUp = true
        switchMainActivity()
        li("Timer is up!")
    }

    //在右上角显示“定位中”字样
    private fun showLocatingTip() {
        shimmer_text.visibility = View.VISIBLE
        val shimmer = Shimmer()
        shimmer.setDuration(2000)
                .start(shimmer_text)
        JumpingBeans.with(shimmer_text)
                .setLoopDuration(2000)
                .appendJumpingDots().build()
    }

}

fun Context.showErrorToast(msg: String) {
    Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
}
