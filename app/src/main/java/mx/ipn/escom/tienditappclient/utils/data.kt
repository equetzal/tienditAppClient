package mx.ipn.escom.tienditappclient.utils

import android.content.Context
import producto

//@author github.com/equetzal -> Enya Quetzalli
class data(appContext: Context) {
    val context = appContext

    val serverIP = dataModule< String >(context, "serverIP", "192.186.100.26")
    val serverPort = dataModule< String >(context, "serverPort", "12345")
    val clientId = dataModule< Int >(context, "clientId", "-1")
    val clientName = dataModule< String >(context, "clientName", "")
    val productRelation = dataModule< Map<Int, Pair<producto, String>> >(context, "productRelation", "{}")
    val cart = dataModule< MutableMap<Int,Int> > (context, "cart", "{}")
    val total = dataModule< Double > (context, "totalOnCart", "0.0")

    init{
        serverIP.setType<String>()
        serverPort.setType<String>()
        clientId.setType<Int>()
        productRelation.setType<MutableMap<Int, Pair<producto, String>>>()
        cart.setType<MutableMap<Int,Int>>()
        total.setType<Double>()
    }

    fun deleteSavedData(){
        serverIP.delete()
        serverPort.delete()
        clientId.delete()
        productRelation.delete()
        cart.delete()
        total.delete()
    }
}
