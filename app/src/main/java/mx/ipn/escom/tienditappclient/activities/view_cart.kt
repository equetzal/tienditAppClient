package mx.ipn.escom.tienditappclient.activities

import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import cart_item
import mx.ipn.escom.tienditappclient.R
import mx.ipn.escom.tienditappclient.adapters.adapter_cart
import mx.ipn.escom.tienditappclient.adapters.adapter_products
import mx.ipn.escom.tienditappclient.utils.data
import mx.ipn.escom.tienditappclient.utils.serverConnection

class view_cart : Fragment() {
    lateinit var dt:data

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_view_cart, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dt = data(requireContext())

        view?.findViewById<SwipeRefreshLayout>(R.id.cart_swipe)?.setOnRefreshListener {
            loadCart().execute()
        }

        loadCart().execute()
    }

    inner class loadCart: AsyncTask<String, Void, Void>(){
        override fun onPreExecute(){

        }
        override fun doInBackground(vararg params: String?): Void? {
            serverConnection(requireContext()).getCart()
            return null
        }

        override fun onPostExecute(result: Void?) {
            val listOfProducts = ArrayList<cart_item>()

            val rel = dt.productRelation.getData()
            dt.cart.getData().forEach{
                listOfProducts.add(cart_item((
                        rel[it.key] ?: error("")).first.idProducto,
                    (rel[it.key] ?: error("")).first.nombre,
                    (rel[it.key] ?: error("")).first.sku,
                    it.value
                ))
            }

            view!!.findViewById<TextView>(R.id.cart_total).text = "Total: ${dt.total.getData()}"

            val recyclerView = view?.findViewById<RecyclerView>(R.id.cart_recyclerView)
            recyclerView?.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            recyclerView?.itemAnimator = DefaultItemAnimator()
            recyclerView?.adapter = adapter_cart(listOfProducts, requireContext(), view!!.findViewById<TextView>(R.id.cart_total))
            view!!.findViewById<RelativeLayout>(R.id.cart_progress).visibility = View.GONE
            view!!.findViewById<SwipeRefreshLayout>(R.id.cart_swipe)?.isRefreshing = false
        }
    }
}