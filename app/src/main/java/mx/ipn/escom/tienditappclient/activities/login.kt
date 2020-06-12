package mx.ipn.escom.tienditappclient.activities

import android.content.Intent
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import mx.ipn.escom.tienditappclient.R
import mx.ipn.escom.tienditappclient.utils.data
import mx.ipn.escom.tienditappclient.utils.serverConnection

class login : AppCompatActivity() {
    val dt = data(this)
    var clientId:String? = null

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
            if(findViewById<Button>(R.id.login_signin).isClickable){
                clientId = findViewById<EditText>(R.id.login_userId).text.toString().trim()
                signin().execute(clientId)
            }
        }
    }

    inner class signin : AsyncTask<String, Void, Boolean>(){
        override fun onPreExecute(){
            findViewById<Button>(R.id.login_signin).isClickable = false
            findViewById<Button>(R.id.login_signin).background = getDrawable(R.drawable.button_shaded)
        }
        override fun doInBackground(vararg params: String?): Boolean {
            return serverConnection(this@login).login(params[0]!!)
        }

        override fun onPostExecute(result: Boolean?) {
            super.onPostExecute(result)

            if(result!!){
                Toast.makeText(this@login, "Loged In", Toast.LENGTH_LONG).show()
                startActivity(Intent(this@login, dashboard::class.java))
                finish()
            }else{
                Toast.makeText(this@login, "Login Fallido", Toast.LENGTH_LONG).show()
            }

            findViewById<Button>(R.id.login_signin).isClickable = true
            findViewById<Button>(R.id.login_signin).background = getDrawable(R.drawable.button_burning)
        }
    }
}