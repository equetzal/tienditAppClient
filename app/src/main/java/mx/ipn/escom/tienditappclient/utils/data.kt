package mx.ipn.escom.tienditappclient.utils

import android.content.Context

//@author github.com/equetzal -> Enya Quetzalli
class data(appContext: Context) {
    val context = appContext

    val serverIP = dataModule< String >(context, "serverIP", "192.186.100.26")
    val serverPort = dataModule< String >(context, "serverPort", "12345")

    init{
        serverIP.setType<String>()
        serverPort.setType<String>()
    }

    fun deleteSavedData(){
        serverIP.delete()
        serverPort.delete()
    }
}
