package cn.nexttec.tanement.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.LinearLayout
import android.widget.TextView
import cn.nexttec.convenienttanement.Common.PAGE_COUNT
import cn.nexttec.tanement.Common.formatBuslineName
import cn.nexttec.tanement.Common.formatDistance
import cn.nexttec.tanement.Common.getPositionColor
import cn.nexttec.tanementapi.bean.House
import cn.nexttec.tanement.R
import cn.nexttec.tanement.TanementApp
import cn.nexttec.tanement.bean.distance
import com.facebook.drawee.view.SimpleDraweeView

class SingleLocationAdapter(val context: Context) : BaseAdapter() {
    private val houses = ArrayList<House>()
    var page = 0

    init {

    }

    fun addHouse(house: House) {
        this.houses += houses
        notifyDataSetChanged()
    }

    fun addHouses(house: List<House>) {
        this.houses += house
        notifyDataSetChanged()
    }

    fun nextPage() {
        page++
        notifyDataSetChanged()
    }

    override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View {
        var holder: ViewHolder? = null
        var itemView: View? = null
        if (p1 == null) {
            itemView = LayoutInflater.from(context)
                    .inflate(R.layout.house_item_layout, p2, false)
            holder = ViewHolder()
            holder.infoContainer = itemView.findViewById(R.id.house_info_container)
            holder.image = itemView.findViewById(R.id.house_image)
            holder.houseTitle = itemView.findViewById(R.id.house_title)
            holder.houseSource = itemView.findViewById(R.id.house_source)
            holder.houseTraffic = itemView.findViewById(R.id.house_traffic)
            itemView.tag = holder
        } else {
            itemView = p1
            holder = itemView.tag as ViewHolder
        }
        val house = houses[p0]
        holder.setHouseInfo(house)
        holder.infoContainer!!.setBackgroundColor(getPositionColor(p0))
        return itemView!!
    }

    override fun getItem(p0: Int): Any {
        return houses[p0]
    }

    override fun getItemId(p0: Int): Long {
        return p0.toLong()
    }

    override fun getCount(): Int {
        if (houses.isEmpty())
            return 0
        val max = (page + 1) * PAGE_COUNT
        return if (max > houses.size) houses.size else max
    }

    inner class ViewHolder() {
        var image: SimpleDraweeView? = null
        var infoContainer: LinearLayout? = null
        var houseTitle: TextView? = null
        var houseSource: TextView? = null
        var houseTraffic: TextView? = null

        fun setHouseInfo(house: House) {
            with(house) {
                image!!.setImageURI(image_url)
                houseTitle!!.setText("$campus/$house_type/月租${if (rental > 0) rental.toInt() else "面议"}")
                houseSource!!.setText("来源:$source\t发布日期:$date")
                val distance = TanementApp.firstLocation.distance(house.lat, house.lon)
                val trafficStr =
                        if (distance < 1000)
                            "距离${TanementApp.firstLocation.keyword}约${distance.formatDistance()}"
                        else {
                            val busDistance = house.distance
                            val linsStr = house.lines.map { "${it.line_name.formatBuslineName()}(${it.arrival_station}-${it.depart_station})" }
                                    .reduce { acc, s -> "$acc,$s" }
                            "与${TanementApp.firstLocation.keyword}公交里程约${busDistance.formatDistance()},可乘坐${linsStr}往返,需步行${walk_distance.toInt()}米,通勤时间约${(duration/60).toInt()}分钟"
                        }

                houseTraffic!!.setText(trafficStr)
            }
        }
    }

}