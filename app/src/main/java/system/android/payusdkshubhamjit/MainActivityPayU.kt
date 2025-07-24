package system.android.payusdkshubhamjit

import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.webkit.WebView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.payu.base.models.ErrorResponse
import com.payu.base.models.PayUPaymentParams
import com.payu.checkoutpro.PayUCheckoutPro
import com.payu.checkoutpro.utils.PayUCheckoutProConstants.CP_HASH_NAME
import com.payu.checkoutpro.utils.PayUCheckoutProConstants.CP_HASH_STRING
import com.payu.checkoutpro.utils.PayUCheckoutProConstants.CP_UDF1
import com.payu.checkoutpro.utils.PayUCheckoutProConstants.CP_UDF2
import com.payu.checkoutpro.utils.PayUCheckoutProConstants.CP_UDF3
import com.payu.checkoutpro.utils.PayUCheckoutProConstants.CP_UDF4
import com.payu.checkoutpro.utils.PayUCheckoutProConstants.CP_UDF5
import com.payu.ui.model.listeners.PayUCheckoutProListener
import com.payu.ui.model.listeners.PayUHashGenerationListener
import java.security.MessageDigest

class MainActivityPayU : AppCompatActivity() {


    var merchantSalt = "ZKGE2Vu3zBtBtwdwfgXuCB3mvhkxiTUh"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main_pay_u)

        findViewById<TextView>(R.id.pay).setOnClickListener {
            startPayment()
        }


    }


    private fun startPayment() {


        val udf1Json = """{"appId":"1001","blkId":"1","flrId":"1","houseNo":"101","purpose":"maintenance","platformCharges":"5"}"""

        val additionalParams = hashMapOf<String, Any?>(
            CP_UDF1 to udf1Json,
            CP_UDF2 to "",
            CP_UDF3 to "",
            CP_UDF4 to "",
            CP_UDF5 to ""
        )


        val payUPaymentParams = PayUPaymentParams.Builder()
            .setAmount("55.0")
            .setKey("OuOa2g")
            .setIsProduction(false)
            .setProductInfo("maintenance")
            .setFirstName("Shubham") // ✅ required
            .setEmail("ettech1840@gmail.com") // ✅ required
            .setPhone("9999999999")
            .setTransactionId("1TXNID000000572")
            .setSurl("https://cbjs.payu.in/sdk/success")
            .setFurl("https://cbjs.payu.in/sdk/failure")
            .setUserCredential("OuOa2g:ettech1840@gmail.com")
            .setAdditionalParams(additionalParams)
            .build()



        PayUCheckoutPro.open(this, payUPaymentParams, object : PayUCheckoutProListener {
            override fun onPaymentSuccess(response: Any) {
                Log.d("PayU", "Payment Success: $response")
            }

            override fun onPaymentFailure(response: Any) {
                Log.d("PayU", "Payment Failure: $response")
            }

            override fun onPaymentCancel(isTxnInitiated: Boolean) {
                Log.d("PayU", "Payment Cancelled: $isTxnInitiated")
            }

            override fun onError(errorResponse: ErrorResponse) {
                Log.d("PayU", "Error: ${errorResponse.errorMessage}")
            }

            override fun setWebViewProperties(webView: WebView?, bank: Any?) {}

            override fun generateHash(
                map: HashMap<String, String?>,
                hashGenerationListener: PayUHashGenerationListener
            ) {
                val hashData = map[CP_HASH_STRING]
                val hashName = map[CP_HASH_NAME]

                Log.d("PayU", "Hash Input: $hashData")
                val hash = sha512(hashData + merchantSalt)
                Log.d("PayU", "Generated Hash: $hash")


                if (!TextUtils.isEmpty(hash)) {
                    val hashMap = HashMap<String, String?>()
                    hashMap[hashName!!] = hash
                    hashGenerationListener.onHashGenerated(hashMap)
                }
            }
        })
    }

    private fun sha512(input: String): String {
        Log.d("PayU", "sha512 Input: $input")
        val digest = MessageDigest.getInstance("SHA-512")
        val bytes = digest.digest(input.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }

}