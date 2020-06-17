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
import mx.ipn.escom.tienditappclient.utils.data
import mx.ipn.escom.tienditappclient.utils.serverConnection

import producto

//@author github.com/equetzal -> Enya Quetzalli
class adapter_cart(var list:ArrayList<cart_item>, context: Context, total:TextView) : RecyclerView.Adapter<adapter_cart.ViewHolder>(){
    val context = context
    val db = data(context)
    val total = total

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view){
        fun bindItems(dt: cart_item){
            val card:CardView = itemView.findViewById(R.id.item_card)
            val name:TextView = itemView.findViewById(R.id.cart_item_name)
            val sku:TextView = itemView.findViewById(R.id.cart_item_SKU)
            val amount:TextView = itemView.findViewById(R.id.cart_item_amount)
            val buttonPlus:ImageButton = itemView.findViewById(R.id.cart_item_addToCart)
            val buttonMinus:ImageButton = itemView.findViewById(R.id.cart_item_quitFromCart)

            name.text = dt.name
            sku.text = "SKU: ${dt.sku}"
            amount.text = dt.amount.toString()

            buttonPlus.setOnClickListener {
                Log.d("Cart", "Plus pressed")
                updateCart(true, amount, dt.productId, card).execute(1)
                notifyDataSetChanged()
            }
            buttonMinus.setOnClickListener {
                Log.d("Cart", "Quit pressed")
                updateCart(false, amount, dt.productId, card).execute(1)
                notifyDataSetChanged()
            }

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.card_cart_item, parent,false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return  list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItems(list[position])
    }


    inner class updateCart(op:Boolean, amount:TextView, productId:Int, card:CardView) : AsyncTask<Int, Void, Boolean>(){
        val op = op
        val amount = amount
        val productId = productId
        val card = card

        override fun doInBackground(vararg params: Int?): Boolean {
            return if(op)
                serverConnection(context).addToCart(productId, params[0]!!)
            else
                serverConnection(context).quitFromCart(productId, params[0]!!)
        }

        override fun onPostExecute(result: Boolean?) {
            super.onPostExecute(result)

            if(result!!){
                val newAmount = db.cart.getData()[productId]!!
                if(newAmount == 0){
                    TransitionManager.beginDelayedTransition(card.parent.parent as ViewGroup, AutoTransition())
                    card.visibility = View.GONE
                }
                else
                    amount.text = newAmount.toString()
                total.text = "Total: ${db.total.getData()}"
            }
        }
    }
}

