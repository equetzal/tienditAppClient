package mx.ipn.escom.tienditappclient.utils

import android.content.Context
import android.util.Log
import android.webkit.MimeTypeMap
import com.google.gson.Gson
import jsonRequest
import jsonResponse
import java.io.*
import java.net.InetAddress
import java.net.Socket

class serverConnection(context:Context){
    val dt = data(context)
    val ip = dt.serverIP.getData()
    val port = dt.serverPort.getData().toInt()

    fun pingServer(newIp:String, newPort:String) : Boolean{
        var ans = false
        try {
            val socket = Socket(newIp, newPort.toInt())
            val dataOutputStream = DataOutputStream(socket.getOutputStream())
            val dataInputStream =  DataInputStream(socket.getInputStream())

            val request = jsonRequest()
            request.operationId = 0

            dataOutputStream.writeUTF(Gson().toJson(request))
            dataOutputStream.flush()

            val json = dataInputStream.readUTF()
            val response = Gson().fromJson(json, jsonResponse::class.java)
            if(response.pingOk!!){
                ans = true
            }

            dataInputStream.close()
            dataOutputStream.close()
            socket.close()
        }catch (e:Exception){
            e.printStackTrace()
        }

        return ans
    }


}