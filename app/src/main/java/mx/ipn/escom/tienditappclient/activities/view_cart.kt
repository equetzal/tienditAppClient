package mx.ipn.escom.tienditappclient.activities

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import cart_item
import mx.ipn.escom.tienditappclient.BuildConfig
import mx.ipn.escom.tienditappclient.R
import mx.ipn.escom.tienditappclient.adapters.adapter_cart
import mx.ipn.escom.tienditappclient.utils.data
import mx.ipn.escom.tienditappclient.utils.serverConnection
import java.io.File

private const val MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE: Int = 7474

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

        view?.findViewById<Button>(R.id.cart_buy).setOnClickListener {
            if (ContextCompat.checkSelfPermission(requireContext(),
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(),
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                } else {
                    ActivityCompat.requestPermissions(requireActivity(),
                        arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                        MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE)
                }
            } else {
                buyCart().execute()
            }
        }

        loadCart().execute()
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE -> {
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    buyCart().execute()
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(requireContext(), "No se podrá guardar el recibo de compra", Toast.LENGTH_LONG).show()
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

    inner class buyCart: AsyncTask<String, Void, File>(){
        override fun onPreExecute(){

        }
        override fun doInBackground(vararg params: String?): File? {
            return serverConnection(requireContext()).buyCart()
        }

        override fun onPostExecute(pdfFile: File?) {
            if(pdfFile != null)
                openPDFFile(pdfFile)
            else
                Toast.makeText(requireContext(), "Hubo un error con la compra", Toast.LENGTH_LONG).show()
        }
    }

    fun openPDFFile(pdfFile:File) {
        if (pdfFile.exists()) {
            val pdfURI: Uri = FileProvider.getUriForFile(
                requireContext(), BuildConfig.APPLICATION_ID.toString() + ".fileprovider",
                pdfFile
            )
            Log.d("URI:", pdfURI.toString())
            val Go = Intent(Intent.ACTION_VIEW)
            Go.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            Go.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
            Go.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            Go.setDataAndType(pdfURI, "application/pdf")
            startActivity(Go)
        }
    }

}