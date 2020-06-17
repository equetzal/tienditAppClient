package mx.ipn.escom.tienditappclient.adapters

import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.AsyncTask
import android.util.Log
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.google.gson.Gson
import mx.ipn.escom.tienditappclient.R
import mx.ipn.escom.tienditappclient.activities.dashboard
import mx.ipn.escom.tienditappclient.utils.data
import mx.ipn.escom.tienditappclient.utils.serverConnection

import producto

//@author github.com/equetzal -> Enya Quetzalli
class adapter_products(var list:ArrayList<producto>, context: Context) : RecyclerView.Adapter<adapter_products.ViewHolder>(){
    val context = context
    var productName = ""

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view){
        fun bindItems(dt: producto){
            val name:TextView = itemView.findViewById(R.id.product_name)
            val productSKU:TextView = itemView.findViewById(R.id.product_SKU)
            val productAmount:TextView = itemView.findViewById(R.id.product_amount)
            val productPrice:TextView = itemView.findViewById(R.id.product_price)
            val productImage:ImageView = itemView.findViewById(R.id.product_image)
            val id = dt.idProducto

            name.text = dt.nombre
            productSKU.text = "ID de Producto: ${dt.sku}"
            productAmount.text = "Cantidad disponible${dt.cantidadDisponible}"
            productPrice.text = "Precio: ${dt.precio}"
            val imagePath = (data(itemView.context).productRelation.getData()[dt.idProducto] ?: error("")).second
            productImage.setImageDrawable(Drawable.createFromPath(imagePath))

            itemView.findViewById<TableRow>(R.id.product_addToCart).setOnClickListener {
                productName = dt.nombre
                updateCart().execute(dt.idProducto, 1)
            }

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.card_product, parent,false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return  list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItems(list[position])
    }

    inner class updateCart : AsyncTask<Int, Void, Boolean>(){
        override fun doInBackground(vararg params: Int?): Boolean {
            return serverConnection(context).addToCart(params[0]!!, params[1]!!)
        }

        override fun onPostExecute(result: Boolean?) {
            super.onPostExecute(result)

            if(result!!){
                Toast.makeText(context, "$productName agregado a al carrito.", Toast.LENGTH_LONG).show()
            }else{
                Toast.makeText(context, "$productName no pudo ser agregado.\nIntente mas tarde.", Toast.LENGTH_LONG).show()
            }
        }
    }
}

