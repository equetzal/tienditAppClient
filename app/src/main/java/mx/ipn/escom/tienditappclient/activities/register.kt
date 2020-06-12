package mx.ipn.escom.tienditappclient.activities

import android.content.Intent
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.transition.TransitionManager
import android.view.View
import android.view.ViewGroup
import android.widget.*
import mx.ipn.escom.tienditappclient.R
import mx.ipn.escom.tienditappclient.utils.data
import mx.ipn.escom.tienditappclient.utils.serverConnection

class register : AppCompatActivity() {
    val dt = data(this)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        TransitionManager.beginDelayedTransition(findViewById<ViewGroup>(R.id.register_background))
        findViewById<TableRow>(R.id.register_nameField).visibility = View.VISIBLE
        findViewById<Button>(R.id.register_signup).visibility = View.VISIBLE
        findViewById<TextView>(R.id.register_generatedId).visibility = View.GONE
        findViewById<TextView>(R.id.register_clientId).visibility = View.GONE

        findViewById<ImageButton>(R.id.register_exit).setOnClickListener{
            finish()
        }

        findViewById<Button>(R.id.register_signup).setOnClickListener{
            if(findViewById<Button>(R.id.register_signup).isClickable){
                val clientName = findViewById<EditText>(R.id.register_clientName).text.toString().trim()
                signup().execute(clientName)
            }
        }
    }

    inner class signup : AsyncTask<String, Void, Boolean>(){
        override fun onPreExecute(){
            findViewById<Button>(R.id.register_signup).isClickable = false
            findViewById<Button>(R.id.register_signup).background = getDrawable(R.drawable.button_shaded)
        }
        override fun doInBackground(vararg params: String?): Boolean {
            return serverConnection(this@register).signup(params[0]!!)
        }

        override fun onPostExecute(result: Boolean?) {
            super.onPostExecute(result)

            if(result!!){
                Toast.makeText(this@register, "¡Registro creado!\nGuarda tu ID", Toast.LENGTH_LONG).show()

                findViewById<TextView>(R.id.register_clientId).text = dt.clientId.getData().toString()
                findViewById<TableRow>(R.id.register_nameField).visibility = View.GONE
                findViewById<Button>(R.id.register_signup).visibility = View.GONE
                findViewById<TextView>(R.id.register_generatedId).visibility = View.VISIBLE
                findViewById<TextView>(R.id.register_clientId).visibility = View.VISIBLE
            }else{
                Toast.makeText(this@register, "Registro Fallido", Toast.LENGTH_LONG).show()
            }

            findViewById<Button>(R.id.register_signup).isClickable = true
            findViewById<Button>(R.id.register_signup).background = getDrawable(R.drawable.button_burning)
        }
    }
}