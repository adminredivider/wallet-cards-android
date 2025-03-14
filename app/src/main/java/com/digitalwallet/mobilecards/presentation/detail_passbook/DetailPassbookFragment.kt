package com.digitalwallet.mobilecards.presentation.detail_passbook

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.digitalwallet.mobilecards.databinding.FragmentPassbookDetailBinding
import com.digitalwallet.mobilecards.presentation.main.MainScreenAction
import com.digitalwallet.mobilecards.presentation.main.MainViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class DetailPassbookFragment : Fragment() {
    private val mainViewModel: MainViewModel by viewModel()
    private var _binding: FragmentPassbookDetailBinding? = null
    private val binding get() = _binding!!
    private var currentId = ""
    private var currentPos = 0

    private val passDetailAdapter = PassBookVMAdapter(
        action = {
            uniqueId, adapterPos ->
                currentId = uniqueId
                currentPos = adapterPos
        }
    )

    companion object {
        private const val MAX_BRIGHTNESS = 1F
    }

    private var previousBrightness = MAX_BRIGHTNESS

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {

        _binding = FragmentPassbookDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as? AppCompatActivity)?.let {
            val attributes = it.window.attributes
            previousBrightness = attributes.screenBrightness
            attributes.screenBrightness = MAX_BRIGHTNESS
            it.window.attributes = attributes
        }

        mainViewModel.action(MainScreenAction.ImportPassbookAction)
        currentId = arguments?.getString("uniqueId").toString()

        setupFieldAction()
        initViewPager()
        setupObservers()

    }

    private fun setupFieldAction() {
        binding.btnDone.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.btnMore.setOnClickListener {
            val action =

                DetailPassbookFragmentDirections.actionDetailPassbookToPassbookBackFragment(
                    currentId,
                    binding.viewPager.currentItem
                )
            findNavController().navigate(action)
        }

    }

    private fun initViewPager() {
        binding.viewPager.adapter = passDetailAdapter
        binding.springDotsIndicator.attachTo(binding.viewPager)
    }

    private fun setupObservers() {
        mainViewModel.groupedPassesLiveData.observe(viewLifecycleOwner) { model ->
            passDetailAdapter.differ.submitList(listOf(model.find { it.uniqueId == currentId }))
        }
    }

    private fun shareText(text: String) {
        val intent = Intent(Intent.ACTION_SEND).apply {
            putExtra(Intent.EXTRA_TEXT, text)
            type = "text/plain"
        }
        requireContext().startActivity(Intent.createChooser(intent, "Share via"))
    }


    override fun onDestroyView() {
        super.onDestroyView()
        (activity as? AppCompatActivity)?.let {
            val attributes = it.window.attributes
            attributes.screenBrightness = previousBrightness
            it.window.attributes = attributes
        }
        _binding = null
    }
}