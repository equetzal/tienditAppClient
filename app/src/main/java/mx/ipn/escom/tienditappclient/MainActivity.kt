package mx.ipn.escom.tienditappclient

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import mx.ipn.escom.tienditappclient.activities.login

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Handler().postDelayed({
            val intent = Intent(this, login::class.java)
            startActivity(intent)
            finish()
        }, 3000)

    }
}