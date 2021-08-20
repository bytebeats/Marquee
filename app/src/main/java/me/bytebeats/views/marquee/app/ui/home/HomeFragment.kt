package me.bytebeats.views.marquee.app.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import me.bytebeats.views.marquee.app.R
import me.bytebeats.views.marquee.app.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel
    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textHome
        homeViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }
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
//            binding.marqueeTextViewLong.resumeMarquee()
            binding.marqueeTextViewShort.resumeMarquee()
        }
        binding.btnPause.setOnClickListener {
//            binding.marqueeTextViewLong.pauseMarquee()
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