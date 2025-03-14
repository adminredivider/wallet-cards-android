package io.walletcards.android.presentation.detail_passbook.back_view

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import io.walletcards.android.R
import io.walletcards.android.databinding.FragmentBackPassBinding
import io.walletcards.android.presentation.main.MainScreenAction
import io.walletcards.android.presentation.main.MainViewModel
import io.walletcards.android.presentation.passbook.PassFileIO
import io.walletcards.android.presentation.util.bindFields
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class BackPassbookViewFragment : Fragment() {
    private val mainViewModel: MainViewModel by viewModel()
    private val passFileIO: PassFileIO by inject { parametersOf(this) }
    private var _binding: FragmentBackPassBinding? = null
    private val binding get() = _binding!!

    private var currentId = ""
    private var currentPos = 0

    companion object {
        private const val MAX_BRIGHTNESS = 1F
    }

    private var previousBrightness = MAX_BRIGHTNESS

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {

        _binding = FragmentBackPassBinding.inflate(inflater, container, false)
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
        currentPos = arguments?.getInt("currentPos") ?: 0

        setupFieldAction()
        setupObservers()

    }

    private fun setupFieldAction() {
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun setupObservers() {
        mainViewModel.groupedPassesLiveData.observe(viewLifecycleOwner) { itemList ->
            val groupItem = itemList.find { it.uniqueId == currentId }
            val selectedItem = groupItem?.passList?.get(currentPos)
            val labelColor = Color.parseColor("#FF9E9E9E")
            val valueColor = Color.parseColor("#FF9E9E9E")

            binding.passDescription.text = selectedItem?.pass?.description

            binding.btnRemovePass.setOnClickListener {
                selectedItem?.directory?.let { it1 ->
                    selectedItem.file.let { it2 ->
                        passFileIO.delete(
                            it1,
                            it2
                        )
                    }
                }
                findNavController().navigate(
                    R.id.mainFragment,
                    null,
                    NavOptions.Builder()
                        .setPopUpTo(R.id.nav_host_fragment, true)
                        .build()
                )
            }

            selectedItem?.pass?.storeCard?.backFields?.let { fields ->
                bindFields(
                    fields,
                    labelColor,
                    18f,
                    valueColor,
                    14f,
                    binding.passPrimaryFields,
                    requireContext(),
                    showDivider = true
                )
            }

            selectedItem?.pass?.boardingPass?.backFields?.let { fields ->
                bindFields(
                    fields,
                    labelColor,
                    18f,
                    valueColor,
                    14f,
                    binding.passPrimaryFields,
                    requireContext(),
                    showDivider = true
                )
            }

            selectedItem?.pass?.coupon?.backFields?.let { fields ->
                bindFields(
                    fields,
                    labelColor,
                    18f,
                    valueColor,
                    14f,
                    binding.passPrimaryFields,
                    requireContext(),
                    showDivider = true
                )
            }

            selectedItem?.pass?.generic?.backFields?.let { fields ->
                bindFields(
                    fields,
                    labelColor,
                    18f,
                    valueColor,
                    14f,
                    binding.passPrimaryFields,
                    requireContext(),
                    showDivider = true
                )
            }

            selectedItem?.pass?.eventTicket?.backFields?.let { fields ->
                bindFields(
                    fields,
                    labelColor,
                    18f,
                    valueColor,
                    14f,
                    binding.passPrimaryFields,
                    requireContext(),
                    showDivider = true
                )
            }
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