package io.walletcards.mobilecards.presentation.add_card

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.JustifyContent
import io.walletcards.mobilecards.R
import io.walletcards.mobilecards.databinding.FragmentAddCreditCardBinding
import io.walletcards.mobilecards.domain.model.Card
import io.walletcards.mobilecards.domain.model.ValidationResult
import io.walletcards.mobilecards.presentation.main.MainScreenAction
import io.walletcards.mobilecards.presentation.main.MainViewModel
import io.walletcards.mobilecards.presentation.util.Constants.colorList
import io.walletcards.mobilecards.presentation.util.detectCardType
import io.walletcards.mobilecards.presentation.util.isValidExpiryDate
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber


class AddCardFragment : Fragment() {
    private val mainViewModel: MainViewModel by viewModel()
    private var _binding: FragmentAddCreditCardBinding? = null
    private val binding get() = _binding!!

    private var currentSelectedColor = colorList[0]

    // intially set set default card card design 33
    private var cardDesignId: Int? = R.drawable.card_33

    companion object {
        private const val MAX_BRIGHTNESS = 1F
    }

    private var previousBrightness = MAX_BRIGHTNESS

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentAddCreditCardBinding.inflate(inflater, container, false)
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

        //  mainViewModel.changeDesignId(R.drawable.card_33)
        binding.apply {

        }

        val navController = findNavController()
        // Observe the SavedStateHandle for the "cardId" key
        navController.currentBackStackEntry?.savedStateHandle?.getLiveData<Int>("cardId")
            ?.observe(viewLifecycleOwner) { designId ->
                // Handle the received cardId
                val designIdd = if (designId != null) {
                    designId
                } else {
                    R.drawable.card_33
                }
                cardDesignId = designIdd
                binding.carDesignIv.setImageDrawable(
                    ContextCompat.getDrawable(requireContext(), designId)
                )
                Timber.tag("FragmentA").d("Received cardId: $designId")
                // Optional: Remove the data after handling to prevent re-triggering
                navController.currentBackStackEntry?.savedStateHandle?.remove<Int>("cardId")
            }


        binding.brush.setOnClickListener {
            findNavController().navigate(R.id.action_addCardFragment_to_cardDesignFragment)
        }

        //  (requireActivity() as AppCompatActivity).setSupportActionBar(binding.toolbar)

        // Enable Navigation Icon Click
//        binding.toolbar.setNavigationOnClickListener {
//            requireActivity().onBackPressedDispatcher.onBackPressed() // Handle back or open drawer
//        }
        binding.addCardToolbar.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.addCardToolbar.actionMore.visibility = View.GONE
        setupFieldAction()
        setupColorList()
    }

    private fun setupFieldAction() {
//        binding.toolbar.setNavigationOnClickListener {
//            findNavController().navigateUp()
//        }
        binding.contentLayout.apply {
            edtCardholderName.isEnabled = true
            edtCardNumber.isEnabled = true
            tvValidCvvLayout.edtDate.isEnabled = true
            tvValidCvvLayout.edtSecurityCode.isEnabled = true
            edtCardName.isEnabled = true
            notesLayout.visibility = View.VISIBLE
        }

        binding.contentLayout.edtCardNumber.addTextChangedListener(object : TextWatcher {
            private var isEditing = false

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                if (isEditing) return
                isEditing = true

                val input = s.toString().replace(" ", "")
                val formattedInput = input.chunked(4).joinToString(" ")

                if (input.length > 16) {
                    val trimmedInput = input.take(16)
                    binding.contentLayout.edtCardNumber.setText(
                        trimmedInput.chunked(4).joinToString(" ")
                    )

                } else if (formattedInput != s.toString()) {
                    binding.contentLayout.edtCardNumber.setText(formattedInput)
                }

                binding.contentLayout.edtCardNumber.text?.let {
                    binding.contentLayout.edtCardNumber.setSelection(
                        it.length
                    )
                }

                isEditing = false
            }
        })
        binding.contentLayout.  tvValidCvvLayout.edtDate.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                charSequence: CharSequence?,
                start: Int,
                count: Int,
                after: Int,
            ) {

            }

            @SuppressLint("SetTextI18n")
            override fun onTextChanged(
                charSequence: CharSequence?,
                start: Int,
                before: Int,
                count: Int,
            ) {

                if (charSequence != null && charSequence.length == 1) {
                    val digit = charSequence[0]
                    if (digit in '2'..'9') {
                        binding.contentLayout.  tvValidCvvLayout.edtDate.removeTextChangedListener(this)
                        binding.contentLayout.  tvValidCvvLayout.edtDate.setText("0$digit")
                        binding.contentLayout.  tvValidCvvLayout.edtDate.setSelection(2)
                        binding.contentLayout.  tvValidCvvLayout.edtDate.addTextChangedListener(this)
                    }
                }

                if (charSequence != null && charSequence.length == 2) {
                    val month = charSequence.toString().toInt()
                    if (month > 12) {
                        binding.contentLayout.  tvValidCvvLayout.edtDate.removeTextChangedListener(this)
                        binding.contentLayout.  tvValidCvvLayout.edtDate.setText("12")
                        binding.contentLayout.  tvValidCvvLayout.edtDate.setSelection(2)
                        binding.contentLayout.  tvValidCvvLayout.edtDate.addTextChangedListener(this)
                    }
                }
            }

            override fun afterTextChanged(editable: Editable?) {
                if (editable != null) {
                    val text = editable.toString()
                    val formattedText = text.replace(Regex("[^\\d]"), "")

                    if (formattedText.length > 2) {
                        val month = formattedText.substring(0, 2)
                        val year = formattedText.substring(2, formattedText.length.coerceAtMost(4))
                        val newText = "$month/$year"
                        if (newText != text) {
                            binding.contentLayout.  tvValidCvvLayout.edtDate.removeTextChangedListener(this)
                            binding.contentLayout.  tvValidCvvLayout.edtDate.setText(newText)
                            binding.contentLayout.  tvValidCvvLayout.edtDate.setSelection(newText.length)
                            binding.contentLayout.  tvValidCvvLayout.edtDate.addTextChangedListener(this)
                        }
                    } else {
                        if (formattedText != text) {
                            binding.contentLayout.  tvValidCvvLayout.edtDate.removeTextChangedListener(this)
                            binding.contentLayout.  tvValidCvvLayout.edtDate.setText(formattedText)
                            binding.contentLayout.  tvValidCvvLayout.edtDate.setSelection(formattedText.length)
                            binding.contentLayout.  tvValidCvvLayout.edtDate.addTextChangedListener(this)
                        }
                    }
                }
            }
        })

        binding.btnAddCard.setOnClickListener {
            val cardHolder = binding.contentLayout.edtCardholderName.text.toString()
            val cardNumber = binding.contentLayout.edtCardNumber.text.toString()
            val expiryDate = binding.contentLayout.  tvValidCvvLayout.edtDate.text.toString()
            val securityCode = binding.contentLayout.  tvValidCvvLayout.edtSecurityCode.text.toString()
            val bankName = binding.contentLayout.edtCardName.text.toString()
            val cardColor = currentSelectedColor

            val validationResult =
                validateCardDetails(cardHolder, cardNumber, expiryDate, securityCode, bankName)

            if (validationResult.isValid) {
                mainViewModel.action(
                    MainScreenAction.AddCreditCardAction(
                        Card(
                            cardHolderName = cardHolder,
                            cardNumber = cardNumber,
                            cardExpiry = expiryDate,
                            cvv = securityCode,
                            bankName = bankName,
                            cardColor = cardColor,
                            cardType = detectCardType(cardNumber),
                            cardDesignBackgroundId = cardDesignId
                        )
                    )
                )
                findNavController().navigateUp()
            } else {
                Toast.makeText(requireContext(), validationResult.errorMessage, Toast.LENGTH_LONG)
                    .show()
            }
        }
    }

    private fun setupColorList() {
        //  val colorRecyclerView = binding.colorRecyclerView
        val layoutManager = CustomFlexboxLayoutManager(requireContext())
        layoutManager.flexDirection = FlexDirection.ROW
        layoutManager.justifyContent = JustifyContent.SPACE_BETWEEN
        // colorRecyclerView.layoutManager = layoutManager
        val adapter = ColorAdapter(colorList) { selectedColor ->
            currentSelectedColor = selectedColor
        }
        // colorRecyclerView.adapter = adapter
    }

    private fun validateCardDetails(
        cardHolder: String,
        cardNumber: String,
        expiryDate: String,
        securityCode: String,
        bankName: String,
    ): ValidationResult {
        if (cardHolder.isBlank()) {
            return ValidationResult(false, "Cardholder name cannot be empty.")
        }

        if (!cardNumber.replace(" ", "").matches("\\d{16}".toRegex())) {
            return ValidationResult(false, "Invalid card number. Must be 16 digits.")
        }

        if (!expiryDate.matches("^(0[1-9]|1[0-2])/\\d{2}$".toRegex()) || !isValidExpiryDate(
                expiryDate
            )
        ) {
            return ValidationResult(
                false,
                "Invalid expiry date. Must be in MM/YY format and not expired."
            )
        }

        if (!securityCode.matches("\\d{3}".toRegex())) {
            return ValidationResult(false, "Invalid CVV. Must be 3 digits.")
        }

        if (bankName.isBlank()) {
            return ValidationResult(false, "Bank name cannot be empty.")
        }

        return ValidationResult(true, "")
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