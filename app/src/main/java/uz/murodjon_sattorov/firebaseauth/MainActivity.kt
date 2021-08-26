package uz.murodjon_sattorov.firebaseauth

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import uz.murodjon_sattorov.firebaseauth.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var mainBinding: ActivityMainBinding

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mainBinding.root)

        auth = Firebase.auth

        mainBinding.spinner.adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            CountryData.countryNames
        )

        mainBinding.button.setOnClickListener {
            val code = CountryData.countryAreaCodes[mainBinding.spinner.selectedItemPosition]
            val number = mainBinding.mobileNumber.text
            if (number.count() in 7..10) {
                val intent = Intent(this, VerifyActivity::class.java).apply {
                    putExtra("phone_number", code + number.toString())
                }
                startActivity(intent)
            } else {
                Toast.makeText(this, "Enter validate number!!!", Toast.LENGTH_SHORT).show()
            }

        }


    }

    override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        if(currentUser != null){
            startActivity(Intent(this, SuccessActivity::class.java))
        }
    }

}