package uz.murodjon_sattorov.firebaseauth

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import uz.murodjon_sattorov.firebaseauth.databinding.ActivityVerifyBinding
import java.util.concurrent.TimeUnit

class VerifyActivity : AppCompatActivity() {

    lateinit var verifyBinding: ActivityVerifyBinding

    private var verificationId: String? = null
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        verifyBinding = ActivityVerifyBinding.inflate(layoutInflater)
        setContentView(verifyBinding.root)

        val phoneNumber = intent.getStringExtra("phone_number")

        auth = Firebase.auth

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
            "+$phoneNumber",
            60,
            TimeUnit.SECONDS,
            this,
            callbacks
        )

        verifyBinding.verifyButton.setOnClickListener {
            if (!verifyBinding.otpTextField.text.isNullOrEmpty()) {
                verifyVerificationCode(verifyBinding.otpTextField.text.toString())
            }
        }

    }

    private val callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks =
        object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            override fun onVerificationCompleted(p0: PhoneAuthCredential) {
                val code = p0.smsCode
                if (code != null) {
                    verifyVerificationCode(code)
                }
            }

            override fun onVerificationFailed(e: FirebaseException) {
                if (e is FirebaseAuthInvalidCredentialsException) {
                    Toast.makeText(this@VerifyActivity, e.message, Toast.LENGTH_LONG).show()
                } else if (e is FirebaseTooManyRequestsException) {
                    Toast.makeText(this@VerifyActivity, e.message, Toast.LENGTH_LONG).show()
                }
            }

            override fun onCodeSent(
                s: String,
                forceResendingToken: PhoneAuthProvider.ForceResendingToken
            ) {
                super.onCodeSent(s, forceResendingToken)
                verificationId = s
                //mResendToken = forceResendingToken
            }

        }

    private fun verifyVerificationCode(code: String) {
        val credential = PhoneAuthProvider.getCredential(verificationId!!, code)
        signInWithPhoneAuthCredential(credential)
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener(
                this,
                OnCompleteListener<AuthResult> { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this, "Success", Toast.LENGTH_LONG).show()
                        val i = Intent(this, SuccessActivity::class.java)
                        startActivity(i)
                    } else {
                        Toast.makeText(this, "Failed", Toast.LENGTH_LONG).show()
                    }
                }
            )
    }
}