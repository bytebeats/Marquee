package me.bytebeats.views.marquee.app.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import me.bytebeats.views.marquee.app.R
import me.bytebeats.views.marquee.app.databinding.FragmentMarqueeTextViewBinding
import me.bytebeats.views.marquee.app.vm.MarqueeTextViewModel

class MarqueeTextViewFragment : Fragment() {

    private lateinit var marqueeTextViewModel: MarqueeTextViewModel
    private var _binding: FragmentMarqueeTextViewBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        marqueeTextViewModel =
            ViewModelProvider(this).get(MarqueeTextViewModel::class.java)

        _binding = FragmentMarqueeTextViewBinding.inflate(inflater, container, false)
        val root: View = binding.root

        var short = true
        binding.marqueeTextViewShort.setOnClickListener {
            short = !short
            if (short) {
                binding.marqueeTextViewShort.setText(R.string.marquee_text_view_text)
            } else {
                binding.marqueeTextViewShort.setText(R.string.marquee_text_view_text_short)
            }
            Log.i(TAG, "short: $short")
        }
        binding.marqueeTextViewLong.setOnClickListener { Log.i(TAG, "click") }
        binding.btnResume.setOnClickListener {
            binding.marqueeTextViewLong.resumeMarquee()
            binding.marqueeTextViewShort.resumeMarquee()
        }
        binding.btnPause.setOnClickListener {
            binding.marqueeTextViewLong.pauseMarquee()
            binding.marqueeTextViewShort.pauseMarquee()
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val TAG = "HomeFragment"
    }
}