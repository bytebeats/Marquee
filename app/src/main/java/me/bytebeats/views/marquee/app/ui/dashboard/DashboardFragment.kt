package me.bytebeats.views.marquee.app.ui.dashboard

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import me.bytebeats.views.marquee.TextMarqueeView
import me.bytebeats.views.marquee.app.databinding.FragmentDashboardBinding

class DashboardFragment : Fragment() {

    private lateinit var dashboardViewModel: DashboardViewModel
    private var _binding: FragmentDashboardBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dashboardViewModel =
            ViewModelProvider(this).get(DashboardViewModel::class.java)

        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        val root: View = binding.root
        _binding?.marqueeText?.onItemClickListener = object : TextMarqueeView.OnItemClickListener {
            override fun onItemClick(
                textMarqueeView: TextMarqueeView,
                itemView: View,
                position: Int
            ) {
                Log.i(TAG, "position: $position")
            }
        }
        _binding?.marqueeText?.startWithMessage(
            "赵客缦胡缨，吴钩霜雪明。" +
                    "银鞍照白马，飒沓如流星。" +
                    "十步杀一人，千里不留行。" +
                    "事了拂衣去，深藏身与名。" +
                    "闲过信陵饮，脱剑膝前横。" +
                    "将炙啖朱亥，持觞劝侯嬴。" +
                    "三杯吐然诺，五岳倒为轻。" +
                    "眼花耳热后，意气素霓生。" +
                    "救赵挥金槌，邯郸先震惊。" +
                    "千秋二壮士，烜赫大梁城。" +
                    "纵死侠骨香，不惭世上英。" +
                    "谁能书阁下，白首太玄经。"
        )
        return root
    }

    override fun onResume() {
        super.onResume()
        _binding?.marqueeText?.startFlipping()
    }

    override fun onPause() {
        super.onPause()
        _binding?.marqueeText?.stopFlipping()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val TAG = "DashboardFragment"
    }
}