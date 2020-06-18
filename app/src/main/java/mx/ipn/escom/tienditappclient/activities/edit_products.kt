package mx.ipn.escom.tienditappclient.activities

import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RelativeLayout
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import mx.ipn.escom.tienditappclient.R
import mx.ipn.escom.tienditappclient.adapters.adapter_edit
import mx.ipn.escom.tienditappclient.utils.data
import mx.ipn.escom.tienditappclient.utils.serverConnection
import producto

class edit_products : Fragment() {
    lateinit var dt:data

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_edit_products, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dt = data(requireContext())

        view?.findViewById<SwipeRefreshLayout>(R.id.edit_list_swipe)?.setOnRefreshListener {
            loadProducts().execute()
        }

        view?.findViewById<Button>(R.id.edit_list_addProduct).setOnClickListener{
            startActivity(Intent(requireContext(), edit_product::class.java))
        }

    }

    override fun onResume() {
        super.onResume()
        loadProducts().execute()
    }

    inner class loadProducts : AsyncTask<String, Void, ArrayList<producto>>(){
        override fun onPreExecute(){

        }
        override fun doInBackground(vararg params: String?): ArrayList<producto> {
            return serverConnection(requireContext()).getProductsList()
        }

        override fun onPostExecute(result: ArrayList<producto>) {
            val recyclerView = view?.findViewById<RecyclerView>(R.id.edit_list_recyclerView)
            recyclerView?.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            recyclerView?.itemAnimator = DefaultItemAnimator()
            recyclerView?.adapter = adapter_edit(result, requireContext())
            view!!.findViewById<RelativeLayout>(R.id.edit_list_progress).visibility = View.GONE
            view!!.findViewById<SwipeRefreshLayout>(R.id.edit_list_swipe)?.isRefreshing = false
        }
    }

}