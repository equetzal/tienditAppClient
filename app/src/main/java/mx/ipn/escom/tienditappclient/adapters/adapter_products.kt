package mx.ipn.escom.tienditappclient.adapters

import android.graphics.drawable.Drawable
import android.util.Log
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.google.gson.Gson
import mx.ipn.escom.tienditappclient.R
import mx.ipn.escom.tienditappclient.utils.data

import producto

//@author github.com/equetzal -> Enya Quetzalli
class adapter_products(var list:ArrayList<producto>) : RecyclerView.Adapter<adapter_products.ViewHolder>(){

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view){
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
                val cart = data(itemView.context).cart.getData()
                var actAmount:Int = if(cart[id]==null) 0 else cart[id]!!
                actAmount++
                cart[id] = actAmount

                Log.d("Cart", Gson().toJson(cart))
                data(itemView.context).cart.save(cart)

                Toast.makeText(itemView.context, "${dt.nombre} agregado a al carrito", Toast.LENGTH_LONG).show()
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
}

