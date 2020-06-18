package mx.ipn.escom.tienditappclient.adapters

import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.AsyncTask
import android.transition.AutoTransition
import android.transition.TransitionManager
import android.util.Log
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.cardview.widget.CardView
import cart_item
import com.google.gson.Gson
import mx.ipn.escom.tienditappclient.R
import mx.ipn.escom.tienditappclient.activities.dashboard
import mx.ipn.escom.tienditappclient.activities.edit_product
import mx.ipn.escom.tienditappclient.utils.data
import mx.ipn.escom.tienditappclient.utils.serverConnection

import producto

//@author github.com/equetzal -> Enya Quetzalli
class adapter_edit(var list:ArrayList<producto>, context: Context) : RecyclerView.Adapter<adapter_edit.ViewHolder>(){
    val context = context

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view){
        fun bindItems(dt: producto){
            val card:CardView = itemView.findViewById(R.id.edit_card)
            val name:TextView = itemView.findViewById(R.id.edit_item_name)
            val sku:TextView = itemView.findViewById(R.id.edit_item_SKU)
            val buttonEdit:ImageButton = itemView.findViewById(R.id.edit_item_editProduct)

            name.text = dt.nombre
            sku.text = "SKU: ${dt.sku}"

            buttonEdit.setOnClickListener {
                Log.d("Edit", "Edit burron pressed")
                val intent = Intent(itemView.context, edit_product::class.java)
                intent.putExtra("productId", dt.idProducto)
                itemView.context.startActivity(intent)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.card_edit_product, parent,false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return  list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItems(list[position])
    }
}

