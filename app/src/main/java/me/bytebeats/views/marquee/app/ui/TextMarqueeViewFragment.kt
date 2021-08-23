package me.bytebeats.views.marquee.app.ui

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import me.bytebeats.views.marquee.MarqueeDirection
import me.bytebeats.views.marquee.OnItemClickListener
import me.bytebeats.views.marquee.TextMarqueeView
import me.bytebeats.views.marquee.app.R
import me.bytebeats.views.marquee.app.databinding.FragmentTextMarqueeViewBinding
import me.bytebeats.views.marquee.app.vm.TextMarqueeViewModel
import kotlin.math.min

class TextMarqueeViewFragment : Fragment() {

    private lateinit var textMarqueeViewModel: TextMarqueeViewModel
    private var _binding: FragmentTextMarqueeViewBinding? = null
    private val _adapter by lazy { MarqueeTextAdapter(requireContext()) }

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        textMarqueeViewModel =
            ViewModelProvider(this).get(TextMarqueeViewModel::class.java)

        _binding = FragmentTextMarqueeViewBinding.inflate(inflater, container, false)
        val root: View = binding.root
        binding.marqueeText.onItemClickListener = object : OnItemClickListener<TextMarqueeView> {
            override fun onItemClick(view: TextMarqueeView, itemView: View, position: Int) {
                Log.i(TAG, "position: $position")
            }
        }
        binding.marqueeText.startWithMessage(TEXT)
        binding.marqueeText2.startWithMessage(TEXT)
        binding.recyclerView.let {
            it.layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            it.adapter = _adapter
        }
        return root
    }

    override fun onResume() {
        super.onResume()
        binding.marqueeText.startFlipping()
        binding.marqueeText2.startFlipping()
        _adapter.update(TEXT.split("。").filter { it.isNotEmpty() })
    }

    override fun onPause() {
        super.onPause()
        binding.marqueeText.stopFlipping()
        binding.marqueeText2.stopFlipping()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private inner class MarqueeTextAdapter(private val context: Context) :
        RecyclerView.Adapter<MarqueeTextAdapter.MarqueeTextViewHolder>() {
        private val messages = mutableListOf<CharSequence>()

        fun update(data: List<CharSequence>) {
            messages.clear()
            messages.addAll(data)
            notifyDataSetChanged()
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MarqueeTextViewHolder {
            return MarqueeTextViewHolder(
                LayoutInflater.from(context).inflate(R.layout.list_item_text_marquee, parent, false)
            )
        }

        override fun onBindViewHolder(holder: MarqueeTextViewHolder, position: Int) {
            holder.bind(position)
        }

        override fun onViewAttachedToWindow(holder: MarqueeTextViewHolder) {
            super.onViewAttachedToWindow(holder)
            holder.textMarqueeView.startFlipping()
        }

        override fun onViewDetachedFromWindow(holder: MarqueeTextViewHolder) {
            super.onViewDetachedFromWindow(holder)
            holder.textMarqueeView.stopFlipping()
        }

        override fun getItemCount(): Int = messages.size / 2

        inner class MarqueeTextViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
            val textMarqueeView =
                view.findViewById<TextMarqueeView>(R.id.list_item_text_marquee)

            fun bind(position: Int) {
                textMarqueeView.direction = MarqueeDirection.values()[position % 4]
                textMarqueeView.startWithMessages(
                    messages.subList(
                        position * 2,
                        min(2 * (position + 1), messages.size)
                    )
                )
            }
        }
    }

    companion object {
        private const val TAG = "DashboardFragment"
        private const val TEXT = "赵客缦胡缨，吴钩霜雪明。" +
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
    }
}