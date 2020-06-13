package mx.ipn.escom.tienditappclient.activities

import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import mx.ipn.escom.tienditappclient.R
import mx.ipn.escom.tienditappclient.adapters.adapter_products
import mx.ipn.escom.tienditappclient.utils.data
import mx.ipn.escom.tienditappclient.utils.serverConnection
import producto

class view_products : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_view_products, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view?.findViewById<TextView>(R.id.view_products_name)?.text = "Hola, " + data(requireContext()).clientName.getData()

        view?.findViewById<SwipeRefreshLayout>(R.id.view_products_swipe)?.setOnRefreshListener {
            loadProducts().execute()
        }

        loadProducts().execute()
    }

    inner class loadProducts : AsyncTask<String, Void, Void>(){
        override fun onPreExecute(){

        }
        override fun doInBackground(vararg params: String?): Void? {
            serverConnection(requireContext()).loadProducts()
            return null
        }

        override fun onPostExecute(result: Void?) {
            Log.d("Downloads", "Finished")
            val listOfProducts = ArrayList<producto>()

            data(requireContext()).productRelation.getData().forEach{
                listOfProducts.add(it.value.first)
            }

            val recyclerView = view?.findViewById<RecyclerView>(R.id.view_products_recyclerView)
            recyclerView?.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            recyclerView?.adapter = adapter_products(listOfProducts)
            view!!.findViewById<RelativeLayout>(R.id.view_products_progress).visibility = View.GONE
            view?.findViewById<SwipeRefreshLayout>(R.id.view_products_swipe)?.isRefreshing = false
        }
    }
}