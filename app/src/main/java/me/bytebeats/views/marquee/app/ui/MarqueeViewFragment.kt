package me.bytebeats.views.marquee.app.ui

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import me.bytebeats.views.marquee.MarqueeView
import me.bytebeats.views.marquee.app.R
import me.bytebeats.views.marquee.app.databinding.FragmentMarqueeViewBinding
import me.bytebeats.views.marquee.app.vm.MarqueeViewModel

class MarqueeViewFragment : Fragment() {

    private val adapter by lazy { MarqueeAdapter(requireContext()) }

    private lateinit var marqueeViewModel: MarqueeViewModel
    private var _binding: FragmentMarqueeViewBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        marqueeViewModel =
            ViewModelProvider(this).get(MarqueeViewModel::class.java)

        _binding = FragmentMarqueeViewBinding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.marqueeView.adapter = adapter

        return root
    }

    override fun onStart() {
        super.onStart()
        val triples = mutableListOf<Triple<Int, String, String>>()
        for (i in 0 until 10) {
            val first = when (i % 3) {
                0 -> R.color.purple_200
                1 -> R.color.purple_500
                else -> R.color.purple_700
            }
            triples.add(Triple(first, "Title $i", "Subtitle $i"))
        }
        adapter.update(triples)
    }

    override fun onResume() {
        super.onResume()
        binding.marqueeView.startFlipping()
    }

    override fun onPause() {
        super.onPause()
        binding.marqueeView.stopFlipping()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private class MarqueeAdapter(val context: Context) :
        MarqueeView.Adapter<MarqueeAdapter.MarqueeViewHolder>() {
        private val triples = mutableListOf<Triple<Int, String, String>>()

        fun update(data: Collection<Triple<Int, String, String>>) {
            triples.clear()
            triples.addAll(data)
            notifyDataSetChanged()
        }

        override fun createViewHolder(position: Int): MarqueeViewHolder {
            return MarqueeViewHolder(
                LayoutInflater.from(context).inflate(R.layout.list_item_marquee_layout, null)
            )
        }

        override fun bindViewHolder(holder: MarqueeView.ViewHolder, position: Int) {
            (holder as MarqueeViewHolder).bind(position)
        }

        override fun itemCount(): Int = triples.size

        fun item(position: Int): Triple<Int, String, String> = triples[position]

        inner class MarqueeViewHolder(view: View) : MarqueeView.ViewHolder(view) {
            private val image = view.findViewById<ImageView>(R.id.image_view)
            private val title = view.findViewById<TextView>(R.id.title)
            private val subtitle = view.findViewById<TextView>(R.id.subtitle)

            fun bind(position: Int) {
                item(position).apply {
                    image.setImageResource(first)
                    title.text = second
                    subtitle.text = third
                    Log.i("MarqueeView", this.toString())
                }
            }
        }
    }

    companion object {
        private const val TAG = "NotificationsFragment"
    }
}