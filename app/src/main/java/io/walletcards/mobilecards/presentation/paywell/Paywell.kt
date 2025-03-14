package io.walletcards.mobilecards.presentation.paywell

import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ClickableSpan
import android.text.style.UnderlineSpan
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import io.walletcards.mobilecards.R
import io.walletcards.mobilecards.databinding.FragmentPaywellBinding


class Paywell : Fragment() {
    private lateinit var binding: FragmentPaywellBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentPaywellBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpViews()
        clickEvent()
    }

    private fun clickEvent() {
        binding.apply {
          icClose.setOnClickListener {
                findNavController().navigateUp()
            }

            makePayment.setOnClickListener {
                Snackbar.make(binding.root, "Unlock Design", Snackbar.LENGTH_SHORT).show()
            }

        }
    }

    private fun setUpViews() {
        val fullText = getString(R.string.spannable_text)
        val spannableString = SpannableString(fullText)

        // Indices of "Terms" and "Privacy policies"
        val termsStart = fullText.indexOf("Terms")
        val termsEnd = termsStart + "Terms".length

        val privacyStart = fullText.indexOf("Privacy policies")
        val privacyEnd = privacyStart + "Privacy policies".length

        // Underline and ClickableSpan for "Terms"
        val termsClickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                Toast.makeText(requireActivity(), "Terms Clicked", Toast.LENGTH_SHORT).show()
            }
        }
        spannableString.setSpan(
            UnderlineSpan(),
            termsStart,
            termsEnd,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        spannableString.setSpan(
            termsClickableSpan,
            termsStart,
            termsEnd,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        // Underline and ClickableSpan for "Privacy policies"
        val privacyClickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                Toast.makeText(requireActivity(), "Privacy Policies Clicked", Toast.LENGTH_SHORT)
                    .show()
            }
        }
        spannableString.setSpan(
            UnderlineSpan(),
            privacyStart,
            privacyEnd,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        spannableString.setSpan(
            privacyClickableSpan,
            privacyStart,
            privacyEnd,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        // Set the SpannableString to the TextView
//        binding.tvTerms.text = spannableString
//        binding.tvTerms.movementMethod = LinkMovementMethod.getInstance() // Enable link clicking

    }


}