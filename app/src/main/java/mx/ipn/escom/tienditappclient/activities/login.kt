package mx.ipn.escom.tienditappclient.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import mx.ipn.escom.tienditappclient.R

class login : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        findViewById<ImageButton>(R.id.login_setup).setOnClickListener{
            startActivity(Intent(this, setup::class.java))
        }

        findViewById<TextView>(R.id.login_signup).setOnClickListener{
            startActivity(Intent(this, register::class.java))
        }

        findViewById<Button>(R.id.login_signin).setOnClickListener{
            //Aqui se debe conectar con el server
            startActivity(Intent(this, dashboard::class.java))
            finish()
        }
    }
}