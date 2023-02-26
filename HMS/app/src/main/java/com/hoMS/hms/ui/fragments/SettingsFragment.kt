package com.hoMS.hms.ui.fragments

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import com.hoMS.hms.R
import com.hoMS.hms.databinding.FragmentSettingsBinding
import com.hoMS.hms.ui.activities.ActivitySignIn
import com.razorpay.*
import org.json.JSONObject


class SettingsFragment : Fragment(R.layout.fragment_settings), PaymentResultWithDataListener,
    ExternalWalletListener {
    private lateinit var builder: AlertDialog.Builder
    private var binding: FragmentSettingsBinding? = null
    private lateinit var sharedPref: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentSettingsBinding.inflate(inflater, container, false)


        binding!!.logoutUserBtn.setOnClickListener {
//            Snackbar.make(binding!!.root, "Woah!", Snackbar.LENGTH_SHORT).show()
            confirmingLogOut()
        }


        binding!!.paymentTesting.setOnClickListener {
            paymentTesting()
        }

        return binding!!.root
    }

    private fun paymentTesting() {
        Checkout.preload(requireContext().applicationContext)
        val co = Checkout()
        // apart from setting it in AndroidManifest.xml, keyId can also be set
        // programmatically during runtime
        co.setKeyID("rzp_live_XXXXXXXXXXXXXX")
        Checkout.sdkCheckIntegration(activity)
        paymentTestingOrders()
    }

    private fun paymentTestingOrders() {
        val razorpay = RazorpayClient("[YOUR_KEY_ID]", "[YOUR_KEY_SECRET]")
        val orderRequest = JSONObject()
        orderRequest.put("amount", 50000); // amount in the smallest currency unit
        orderRequest.put("currency", "INR");
        orderRequest.put("receipt", "order_rcptid_11");

//        val order = razorpay.Orders
        try {
            val order = razorpay.orders.create(orderRequest)

        } catch (e: RazorpayException) {
            println(e.message);
        }

        paymentTestingInitiate()
    }

    private fun paymentTestingInitiate() {
        val payloadHelper = PayloadHelper("INR", 100, "order_XXXXXXXXX")
        payloadHelper.name = "Gaurav Kumae"
        payloadHelper.description = "Description"
        payloadHelper.prefillEmail = "gaurav.kumar@example.com"
        payloadHelper.prefillContact = "9000090000"
        payloadHelper.prefillCardNum = "4111111111111111"
        payloadHelper.prefillCardCvv = "111"
        payloadHelper.prefillCardExp = "11/24"
        payloadHelper.prefillMethod = "netbanking"
        payloadHelper.prefillName = "MerchantName"
        payloadHelper.sendSmsHash = true
        payloadHelper.retryMaxCount = 4
        payloadHelper.retryEnabled = true
        payloadHelper.color = "#000000"
        payloadHelper.allowRotation = true
        payloadHelper.rememberCustomer = true
        payloadHelper.timeout = 10
        payloadHelper.redirect = true
        payloadHelper.recurring = "1"
        payloadHelper.subscriptionCardChange = true
        payloadHelper.customerId = "cust_XXXXXXXXXX"
        payloadHelper.callbackUrl = "https://accepts-posts.request"
        payloadHelper.subscriptionId = "sub_XXXXXXXXXX"
        payloadHelper.modalConfirmClose = true
        payloadHelper.backDropColor = "#ffffff"
        payloadHelper.hideTopBar = true
        payloadHelper.notes = JSONObject("{\"remarks\":\"Discount to cusomter\"}")
        payloadHelper.readOnlyEmail = true
        payloadHelper.readOnlyContact = true
        payloadHelper.readOnlyName = true
        payloadHelper.image = "https://www.razorpay.com"
        // these values are set mandatorily during object initialization. Those values can be overridden like this
        payloadHelper.amount=100
        payloadHelper.currency="INR"
        payloadHelper.orderId = "order_XXXXXXXXXXXXXX"
    }

    private fun confirmingLogOut() {
        builder = AlertDialog.Builder(requireContext())
        builder.setTitle(getString(R.string.alert_message))
            .setMessage("Do you wish to Log Out?")
            .setCancelable(true)
            .setPositiveButton("Yes") { dialogInterface, it ->
                logout()
                val intent = Intent(requireContext(), ActivitySignIn::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
                dialogInterface.cancel()
            }
            .setNegativeButton("No") { dialogInterface, it ->
                dialogInterface.cancel()
            }
            .show()
    }

    private fun logout() {
        sharedPref = requireActivity().getSharedPreferences(
            "sharingLoginDataUsingSP#02",
            AppCompatActivity.MODE_PRIVATE
        )
        val editor = sharedPref.edit()
        editor.putString("user_status", "code#400")
        editor.apply()
    }

    override fun onPaymentSuccess(p0: String?, p1: PaymentData?) {
        TODO("Not yet implemented")
    }

    override fun onPaymentError(p0: Int, p1: String?, p2: PaymentData?) {
        TODO("Not yet implemented")
    }

    override fun onExternalWalletSelected(p0: String?, p1: PaymentData?) {
        TODO("Not yet implemented")
    }
}