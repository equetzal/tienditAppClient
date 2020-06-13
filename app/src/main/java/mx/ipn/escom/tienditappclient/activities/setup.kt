package mx.ipn.escom.tienditappclient.activities

import android.os.AsyncTask
import android.os.Bundle
import android.transition.TransitionManager
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import mx.ipn.escom.tienditappclient.R
import mx.ipn.escom.tienditappclient.utils.data
import mx.ipn.escom.tienditappclient.utils.serverConnection

class setup : AppCompatActivity() {
    val dt = data(this)
    var newIp:String? = null
    var newPort:String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setup)
        findViewById<Button>(R.id.setup_save).isClickable = false

        findViewById<ImageButton>(R.id.setup_exit).setOnClickListener{
            finish()
        }

        TransitionManager.beginDelayedTransition(findViewById<TextView>(R.id.setup_currentConfig).parent as ViewGroup?)
        findViewById<TextView>(R.id.setup_currentConfig).text = dt.serverIP.getData() + ":" + dt.serverPort.getData()

        findViewById<Button>(R.id.setup_test).setOnClickListener{
            if(findViewById<Button>(R.id.setup_test).isClickable){
                newIp = findViewById<EditText>(R.id.setup_serverData).text.toString().trim()
                newPort = findViewById<EditText>(R.id.setup_portData).text.toString().trim()
                pingServer().execute(newIp, newPort)
            }
        }

        findViewById<Button>(R.id.setup_save).setOnClickListener{
            if(findViewById<Button>(R.id.setup_save).isClickable){
                dt.serverIP.save(newIp!!)
                dt.serverPort.save(newPort!!)
                Toast.makeText(this@setup, "Data saved", Toast.LENGTH_LONG).show()

                findViewById<TextView>(R.id.setup_currentConfig).text = dt.serverIP.getData() + ":" + dt.serverPort.getData()
                findViewById<Button>(R.id.setup_save).isClickable = false
                findViewById<Button>(R.id.setup_save).background = getDrawable(R.drawable.button_shaded)
                findViewById<Button>(R.id.setup_test).isClickable = true
                findViewById<Button>(R.id.setup_test).background = getDrawable(R.drawable.button_burning)
            }
        }

    }

    fun isIp(ip:String):Boolean{
        val ipRegex =
            "^((0|1\\d?\\d?|2[0-4]?\\d?|25[0-5]?|[3-9]\\d?)\\.){3}(0|1\\d?\\d?|2[0-4]?\\d?|25[0-5]?|[3-9]\\d?)$"

        return ip.matches(ipRegex.toRegex())
    }

    fun isPort(port:String) : Boolean{
        val portNumber = port.toInt()
        if(portNumber in 0..65535){
            return true
        }
        return false
    }

    inner class pingServer : AsyncTask<String, Void, Boolean>(){
        override fun onPreExecute() {
            findViewById<Button>(R.id.setup_test).isClickable = false
            findViewById<Button>(R.id.setup_test).background = getDrawable(R.drawable.button_shaded)
        }
        override fun doInBackground(vararg params: String?): Boolean {
            if(isIp(params[0]!!) && isPort(params[1]!!)){
                return serverConnection(this@setup).pingServer(params[0]!!, params[1]!!)
            }
            return false
        }

        override fun onPostExecute(result: Boolean?) {
            super.onPostExecute(result)

            if(result!!){
                Toast.makeText(this@setup, "Ping Ok", Toast.LENGTH_LONG).show()
                findViewById<Button>(R.id.setup_save).isClickable = true
                findViewById<Button>(R.id.setup_save).background = getDrawable(R.drawable.button_burning)
            }else{
                Toast.makeText(this@setup, "No se puede conectar a la dirección proporcionada", Toast.LENGTH_LONG).show()
                findViewById<Button>(R.id.setup_test).isClickable = true
                findViewById<Button>(R.id.setup_test).background = getDrawable(R.drawable.button_burning)
            }
        }
    }
}