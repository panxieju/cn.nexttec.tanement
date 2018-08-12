package cn.nexttec.tanement

import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.AbsListView
import android.widget.AbsListView.OnScrollListener.SCROLL_STATE_FLING
import android.widget.AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL
import android.widget.LinearLayout
import cn.nexttec.tanement.Common.li
import cn.nexttec.tanement.adapters.SingleLocationAdapter
import cn.nexttec.tanement.presenters.IMainPresenter
import cn.nexttec.tanement.presenters.MainPresenter
import cn.nexttec.tanementapi.bean.House
import com.github.ksoichiro.android.observablescrollview.ObservableListView
import kotlinx.android.synthetic.main.activity_main.*

const val MSG_MORE_HOUSE = 0x0001
const val MSG_ON_SCROLL = 0x1000
const val MSG_NOT_ON_SCROLL = 0x1001

class MainActivity : AppCompatActivity(), IMainPresenter {
    private lateinit var listView: ObservableListView
    private lateinit var searchLayout: LinearLayout
    private val singleLAdapter = SingleLocationAdapter(this@MainActivity)

    private val handler = Handler(Handler.Callback {
        when (it.what) {
            MSG_MORE_HOUSE -> {
                singleLAdapter.addHouses(it.obj as List<House>)
            }
            MSG_ON_SCROLL ->{
                searchLayout?.visibility = View.GONE
            }
            MSG_NOT_ON_SCROLL ->{
                searchLayout?.visibility = View.VISIBLE
            }

        }
        false
    })

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        searchLayout = findViewById<LinearLayout>(R.id.bottom_layout)
        listView = findViewById(R.id.listview)
        with(listView) {
            adapter = singleLAdapter
            setOnScrollListener(object : AbsListView.OnScrollListener {
                override fun onScroll(p0: AbsListView?, p1: Int, p2: Int, p3: Int) {
                }

                override fun onScrollStateChanged(p0: AbsListView?, p1: Int) {
                    when (p1) {
                        SCROLL_STATE_TOUCH_SCROLL, SCROLL_STATE_FLING ->
                            handler.sendEmptyMessage(MSG_ON_SCROLL)
                        else ->
                            handler.sendEmptyMessage(MSG_NOT_ON_SCROLL)
                    }

                }
            })
        }
        val presenter = MainPresenter(this@MainActivity, this@MainActivity)
        presenter.searchNeabyHouse()
        presenter.seachReachableHouse()

    }

    override fun showNeabyHouses(houses: List<House>) {
        houses.map { "${it.campus}/${it.house_type}/月租${if (it.rental > 0) it.rental.toInt() else "面议"}" }
                .forEach {
                    li(it)
                }
        val message = Message()
        message.what = MSG_MORE_HOUSE
        message.obj = houses
        handler.sendMessage(message)
    }


}
