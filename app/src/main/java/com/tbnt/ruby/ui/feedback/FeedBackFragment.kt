package com.tbnt.ruby.ui.feedback

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.squareup.picasso.Picasso
import com.tbnt.ruby.R
import com.tbnt.ruby.databinding.FeedbackFragmentLayoutBinding
import com.tbnt.ruby.setRoundedCorner
import com.tbnt.ruby.toPx

class FeedBackFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.feedback_fragment_layout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FeedbackFragmentLayoutBinding.bind(view)
        binding.previewImage.setRoundedCorner(20.toPx)
        Picasso.get().load("https://d31qtdfy11mjj9.cloudfront.net/places/1511524295874407964.jpg").into(binding.previewImage)
        binding.submitButton.apply {
            setTextColor("#FFFFFF")
            setBgColor("#F5C037")
            setText(getString(R.string.gen_submit))
        }
    }
}