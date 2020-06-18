package mx.ipn.escom.tienditappclient.activities

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.util.Log
import android.view.View
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import mx.ipn.escom.tienditappclient.R
import mx.ipn.escom.tienditappclient.utils.data
import mx.ipn.escom.tienditappclient.utils.serverConnection
import java.io.File

private const val MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE: Int = 7474
private const val READ_REQUEST_CODE: Int = 42

class edit_product : AppCompatActivity() {
    val dt = data(this)
    var isEdit = true
    var productId:Int? = -1
    var host = ""
    var port = 0
    var dataPath:String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_product)

        productId = intent.extras?.getInt("productId", -1)
        if(productId == null || productId == -1) isEdit = false

        Log.d("edit", "ProductId=$productId")

        if(isEdit){
            findViewById<TextView>(R.id.edit_product_titulo).text = "Editar Producto"

            //findViewById<EditText>(R.id.edit_product_name).setReadOnly(true)
            findViewById<EditText>(R.id.edit_product_sku).setReadOnly(true)
            //findViewById<EditText>(R.id.edit_product_cantidad).setReadOnly(true)
            //findViewById<EditText>(R.id.edit_product_precio).setReadOnly(true)
            findViewById<Button>(R.id.edit_product_changeImage).isClickable = false
            findViewById<Button>(R.id.edit_product_changeImage).setBackgroundResource(R.drawable.button_shaded)

            val prod = dt.productRelation.getData()[productId]
            Log.d("edit", "ProductId=$productId")
            if(prod == null){
                Toast.makeText(this, "El producto no existe", Toast.LENGTH_LONG).show()
            }else{
                findViewById<EditText>(R.id.edit_product_name).text = Editable.Factory.getInstance().newEditable(prod.first.nombre)
                findViewById<EditText>(R.id.edit_product_sku).text = Editable.Factory.getInstance().newEditable(prod.first.sku)
                findViewById<EditText>(R.id.edit_product_cantidad).text = Editable.Factory.getInstance().newEditable(prod.first.cantidadDisponible.toString())
                findViewById<EditText>(R.id.edit_product_precio).text = Editable.Factory.getInstance().newEditable(prod.first.precio.toString())
                findViewById<ImageButton>(R.id.edit_product_image).setImageDrawable(Drawable.createFromPath(prod.second))
            }

        }else{
            findViewById<Button>(R.id.edit_product_changeImage).isClickable = true
            findViewById<ImageButton>(R.id.edit_product_delete).visibility = View.GONE
        }

        findViewById<ImageButton>(R.id.edit_product_exit).setOnClickListener{
            finish()
        }

        findViewById<Button>(R.id.edit_product_changeImage).setOnClickListener{
            if(!isEdit){
                Log.d("edit", "Carga de imagen")
                if (ContextCompat.checkSelfPermission(this,
                        Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                            Manifest.permission.READ_CONTACTS)) {
                    } else {
                        ActivityCompat.requestPermissions(this,
                            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                            MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE)
                    }
                } else {
                    performFileSearch()
                }
            }
        }

        findViewById<Button>(R.id.edit_product_save).setOnClickListener {
            val productName = findViewById<EditText>(R.id.edit_product_name).text.toString()
            val productSku = findViewById<EditText>(R.id.edit_product_sku).text.toString()
            val productAmount = findViewById<EditText>(R.id.edit_product_cantidad).text.toString()
            val productPrice = findViewById<EditText>(R.id.edit_product_precio).text.toString()

            sendProduct().execute(productName, productSku, productAmount, productPrice)

        }

        findViewById<ImageButton>(R.id.edit_product_delete).setOnClickListener{
            deleteProduct().execute()
        }

    }

    fun EditText.setReadOnly(value: Boolean, inputType: Int = InputType.TYPE_NULL) {
        isFocusable = !value
        isFocusableInTouchMode = !value
        this.inputType = inputType
    }


    fun performFileSearch() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "image/*"
        }

        startActivityForResult(intent, READ_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, resultData: Intent?) {
        super.onActivityResult(requestCode, resultCode, resultData)

        // The ACTION_OPEN_DOCUMENT intent was sent with the request code
        // READ_REQUEST_CODE. If the request code seen here doesn't match, it's the
        // response to some other intent, and the code below shouldn't run at all.

        if (requestCode == READ_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            // The document selected by the user won't be returned in the intent.
            // Instead, a URI to that document will be contained in the return intent
            // provided to this method as a parameter.
            // Pull that URI using resultData.getData().
            resultData?.data?.also { uri ->
                Log.d("data", "Uri: $uri")
                Log.d("data", "Path: " + FilePath.getPath(this, uri))
                val path = FilePath.getPath(this, uri)
                dataPath = path
                findViewById<ImageButton>(R.id.edit_product_image).setImageDrawable(Drawable.createFromPath(dataPath))
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE -> {
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    performFileSearch()
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(this, "No se puede seleccionar el archivo", Toast.LENGTH_LONG).show()
                }
                return
            }

            // Add other 'when' lines to check for other
            // permissions this app might request.
            else -> {
                // Ignore all other requests.
            }
        }
    }

    inner class sendProduct : AsyncTask<String, Void, Boolean>(){

        override fun doInBackground(vararg params: String?): Boolean {
            val productName = params[0]!!
            val productSku = params[1]!!
            val productAmount = params[2]!!.toInt()
            val productPrice = params[3]!!.toDouble()

            Log.d("IsEdit", "isEdit=$isEdit")
            Log.d("productId On Back", "$productId")

            if(isEdit)
                return serverConnection(this@edit_product).updateProduct(productId!!, productName, productPrice, productAmount)
            else
                return serverConnection(this@edit_product).addProduct(productName, productSku, productPrice, productAmount, dataPath!!)
        }

        override fun onPostExecute(result: Boolean?) {
            super.onPostExecute(result)

            if(result!!){
                if(isEdit)
                    Toast.makeText(this@edit_product, "El producto ha sido actulizado", Toast.LENGTH_LONG).show()
                else
                    Toast.makeText(this@edit_product, "El producto ha sido agregado", Toast.LENGTH_LONG).show()
                finish()
            }else{
                Toast.makeText(this@edit_product, "Hubo un error", Toast.LENGTH_LONG).show()
            }

        }
    }

    inner class deleteProduct : AsyncTask<String, Void, Boolean>(){

        override fun doInBackground(vararg params: String?): Boolean {
            return serverConnection(this@edit_product).deleteProduct(productId!!)
        }

        override fun onPostExecute(result: Boolean?) {
            super.onPostExecute(result)

            if(result!!){
                Toast.makeText(this@edit_product, "Producto borrado", Toast.LENGTH_LONG).show()
                finish()
            }else{
                Toast.makeText(this@edit_product, "Hubo un error", Toast.LENGTH_LONG).show()
            }

        }
    }
}