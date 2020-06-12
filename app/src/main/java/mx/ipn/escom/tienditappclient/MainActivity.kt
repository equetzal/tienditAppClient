package mx.ipn.escom.tienditappclient

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import mx.ipn.escom.tienditappclient.activities.login
import mx.ipn.escom.tienditappclient.utils.data

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //data(this).serverIP.save("1.1.1.1")
        //data(this).serverPort.save("123")

        Handler().postDelayed({
            val intent = Intent(this, login::class.java)
            startActivity(intent)
            finish()
        }, 3000)

    }
}