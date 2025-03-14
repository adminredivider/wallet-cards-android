package io.walletcards.mobilecards.presentation.main.fragment

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricPrompt
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
import com.google.zxing.ResultPoint
import com.journeyapps.barcodescanner.BarcodeCallback
import com.journeyapps.barcodescanner.BarcodeResult
import com.journeyapps.barcodescanner.DecoratedBarcodeView
import io.walletcards.mobilecards.R
import io.walletcards.mobilecards.databinding.FragmentMainBinding
import io.walletcards.mobilecards.domain.model.PassGroup
import io.walletcards.mobilecards.presentation.biometric.BiometricAuthListener
import io.walletcards.mobilecards.presentation.biometric.BiometricUtils
import io.walletcards.mobilecards.presentation.main.MainScreenAction
import io.walletcards.mobilecards.presentation.main.MainViewModel
import io.walletcards.mobilecards.presentation.passbook.PassFileIO
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import timber.log.Timber
import java.io.File

class MainFragment : Fragment(), BiometricAuthListener, OnMainCardClickListener {
    private val mainViewModel: MainViewModel by viewModel()
    private val passFileIO: PassFileIO by inject { parametersOf(this) }
    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!
    private val cardAdapter = CardAdapter(this)

    private val requestFileLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                result.data?.data?.let { uri ->
                    handleSelectedFile(uri)
                }
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupActions()
        initRecycler()
        setupObservers()
        checkCameraPermission()
    }


    private fun setupActions() {
        binding.apply {
            btnTutorialToolbar.setOnClickListener {
                mainViewModel.action(MainScreenAction.TutorialButtonClicked)
            }

            nocardImage.setOnClickListener {
                navigateToAddCreditCard()
            }

            btnAddToolbar.setOnClickListener {
                openBottomSheetDialog()
            }

//            rvCard.addOnItemTouchListener(object : RecyclerView.OnItemTouchListener {
//                override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
//                    findNavController().navigate(R.id.action_mainFragment_to_cardDetailFragment)
//                    return true
//                }
//
//                override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {}
//
//                override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {}
//            })
        }


//        binding.btnInfo.setOnClickListener {
//            mainViewModel.action(MainScreenAction.InfoButtonClicked)
//        }

//        binding.btnAddCreditCard.setOnClickListener {
//            checkAuthentication()
//        }

    }

    private fun initRecycler() {
        binding.rvCard.addItemDecoration(OverlapItemDecoration(requireContext(), 135))
        binding.rvCard.layoutManager = LinearLayoutManager(requireContext())
        binding.rvCard.adapter = cardAdapter
    }

    private fun setupObservers() {
        mainViewModel.state.onEach { state ->
            //   binding.btnTutorialToolbar.isVisible = !state.isTutorialWatched
        }.launchIn(lifecycleScope)

        mainViewModel.cardLiveData.observe(viewLifecycleOwner) {
            if (it.isEmpty()) {
                binding.rvCard.visibility = View.GONE
                //binding.creditCardPlaceholder.visibility = View.VISIBLE
                binding.noCardsYet.visibility = View.VISIBLE
            } else {
                binding.rvCard.visibility = View.VISIBLE
                // binding.creditCardPlaceholder.visibility = View.GONE
                binding.noCardsYet.visibility = View.GONE
                Timber.tag("MainFragment").e("cardLiveData: $it")
                cardAdapter.differ.submitList(it.takeLast(3))
            }
        }

        mainViewModel.groupedPassesLiveData.observe(viewLifecycleOwner) { itemList ->
            if (itemList.isEmpty()) {
                binding.rvPasses.visibility = View.GONE
                val passList = mutableListOf<PassGroup>()
                // binding.passbookPlaceholder.visibility = View.VISIBLE
            } else {
                binding.noCardsYet.visibility = View.GONE
                Timber.tag("groupedPassesLiveData").e("setupObservers: $itemList")
                val passesAdapter = PassBookAdapter(
                    action = {
                        navigateToPassbookDetail(it)
                    }
                )

                binding.rvPasses.adapter = null
                binding.rvPasses.layoutManager = null

                binding.rvPasses.removeAllViews()
                if (binding.rvPasses.itemDecorationCount > 0) {
                    binding.rvPasses.removeItemDecorationAt(0)
                }

                binding.rvPasses.layoutManager = LinearLayoutManager(requireContext())
                binding.rvPasses.adapter = passesAdapter
                binding.rvPasses.viewTreeObserver.addOnPreDrawListener(object :
                    ViewTreeObserver.OnPreDrawListener {
                    override fun onPreDraw(): Boolean {
                        if (binding.rvPasses.itemDecorationCount == 0) {
                            binding.rvPasses.addItemDecoration(
                                OverlapDiffItemViewDecoration(
                                    requireContext()
                                )
                            )
                        }
                        binding.rvPasses.viewTreeObserver.removeOnPreDrawListener(this)
                        return true
                    }
                })
                binding.rvPasses.visibility = View.VISIBLE
                // binding.passbookPlaceholder.visibility = View.GONE
                passesAdapter.differ.submitList(itemList.toList())
            }
        }
    }

    private fun checkCameraPermission() {
        if (context?.let {
                ContextCompat.checkSelfPermission(
                    it,
                    Manifest.permission.CAMERA
                )
            } != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.CAMERA),
                123
            )
        }
    }

    private fun openBottomSheetDialog() {
        val dialog = BottomSheetDialog(requireActivity())
        val view = layoutInflater.inflate(R.layout.bottom_sheet_dialog, null)

        val btnClose = view.findViewById<ImageView>(R.id.btn_close)
        val btnAddCreditCard = view.findViewById<MaterialButton>(R.id.btn_add_card)
        val btnImportPass = view.findViewById<MaterialButton>(R.id.btn_import_device)
        val btnTutorial = view.findViewById<MaterialButton>(R.id.btn_tutorial)
        val barcodeView = view.findViewById<DecoratedBarcodeView>(R.id.qr_scanner)
        barcodeView?.viewFinder?.isVisible = false
        barcodeView?.statusView?.isVisible = false
        barcodeView?.decodeContinuous(object : BarcodeCallback {
            override fun barcodeResult(result: BarcodeResult?) {
                result?.let {
                    Timber.tag("barcodeResult").e("$result")
                    dialog.dismiss()
                    mainViewModel.action(MainScreenAction.ScanPassbookAction(it.text))
                }
            }

            override fun possibleResultPoints(resultPoints: List<ResultPoint>) {}
        })

        btnAddCreditCard.setOnClickListener {
            dialog.dismiss()
            checkAuthentication()
        }

        btnImportPass.setOnClickListener {
            dialog.dismiss()
            openFilePicker()
        }

        btnTutorial.setOnClickListener {
            dialog.dismiss()
            mainViewModel.action(MainScreenAction.TutorialButtonClicked)
        }

        btnClose.setOnClickListener {
            dialog.dismiss()
        }

        dialog.setOnDismissListener {
            dialog.dismiss()
            barcodeView?.pause()
        }

        dialog.setOnShowListener {
            val bottomSheet =
                dialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            bottomSheet?.let { sheet ->
                val layoutParams = sheet.layoutParams
                layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
                sheet.layoutParams = layoutParams

                val behavior = BottomSheetBehavior.from(sheet)
                behavior.state = BottomSheetBehavior.STATE_EXPANDED
                behavior.isHideable = true
                behavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
                    override fun onStateChanged(bottomSheet: View, newState: Int) {
                        if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                            dialog.dismiss()
                        }
                    }

                    override fun onSlide(bottomSheet: View, slideOffset: Float) {

                    }
                })
            }
            barcodeView?.resume()
        }


        dialog.setCancelable(true)
        dialog.setContentView(view)
        dialog.show()
    }

    private fun checkAuthentication() {
        if (BiometricUtils.isBiometricReady(requireActivity())) {
            BiometricUtils.showBiometricPrompt(
                activity = context as AppCompatActivity,
                listener = this,
                cryptoObject = null,
            )
        } else {
            navigateToAddCreditCard()
            Toast.makeText(
                requireContext(),
                "No biometric feature perform on this device",
                Toast.LENGTH_SHORT
            )
                .show()
        }
    }


    private fun openFilePicker() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "application/*"
        }

        requestFileLauncher.launch(intent)
    }

    private fun handleSelectedFile(uri: Uri) {
        try {
            val inputStream = requireContext().contentResolver.openInputStream(uri)
            val tempFile =
                File.createTempFile("selected_pass", ".pkpass", requireContext().cacheDir).apply {
                    outputStream().use { output ->
                        inputStream?.copyTo(output)
                    }
                }

            passFileIO.saveFromPath(
                externalPassFile = tempFile,
                onSuccess = { passFile ->
                    Toast.makeText(
                        requireContext(),
                        "Pass imported successfully: ${passFile.id}",
                        Toast.LENGTH_SHORT
                    ).show()
                    mainViewModel.action(MainScreenAction.ImportPassbookAction)
                },
                onError = {
                    Toast.makeText(
                        requireContext(),
                        it.message,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            )


        } catch (e: Exception) {
            Toast.makeText(
                requireContext(),
                "Failed to import pass: ${e.message}",
                Toast.LENGTH_LONG
            ).show()
            e.printStackTrace()
        }
    }

    override fun onBiometricAuthenticateError(error: Int, errMsg: String) {
        when (error) {
            BiometricPrompt.ERROR_USER_CANCELED -> {

            }

            BiometricPrompt.ERROR_NEGATIVE_BUTTON -> {

            }
        }
    }

    override fun onBiometricAuthenticateSuccess(result: BiometricPrompt.AuthenticationResult) {
        navigateToAddCreditCard()
    }

    private fun navigateToAddCreditCard() {
        findNavController().navigate(R.id.action_mainFragment_to_addCardFragment)
    }

    private fun navigateToPassbookDetail(uniqueId: String) {
        val action = MainFragmentDirections.actionMainFragmentToPassbookDetailFragment(uniqueId)
        findNavController().navigate(action)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onCardClick(cardId: Int) {
        Timber.tag("MainFragment").e("onCardClick: $cardId")
        val action = MainFragmentDirections.actionMainFragmentToCardDetailFragment(cardId.toString())
        findNavController().navigate(action)
    }
}