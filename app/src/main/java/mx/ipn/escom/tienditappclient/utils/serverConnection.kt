package mx.ipn.escom.tienditappclient.utils

import android.content.Context
import android.content.LocusId
import android.os.Environment
import android.util.Log
import android.webkit.MimeTypeMap
import com.google.gson.Gson
import jsonRequest
import jsonResponse
import producto
import java.io.*
import java.net.InetAddress
import java.net.Socket

class serverConnection(context:Context){
    val localDir = context.getDir(Environment.DIRECTORY_PICTURES, Context.MODE_PRIVATE).absolutePath
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

    fun login(userId:String) : Boolean{
        var ans = false
        try {
            val socket = Socket(ip, port)
            val dataOutputStream = DataOutputStream(socket.getOutputStream())
            val dataInputStream =  DataInputStream(socket.getInputStream())

            val request = jsonRequest()
            request.operationId = 2
            request.clientId = userId.toInt()

            dataOutputStream.writeUTF(Gson().toJson(request))
            dataOutputStream.flush()

            val json = dataInputStream.readUTF()
            val response = Gson().fromJson(json, jsonResponse::class.java)
            if(response.isLoginOk!!){
                ans = true
                dt.clientId.save(userId.toInt())
                Log.d("Client Name", "${response.clientName}")
                dt.clientName.save(response.clientName!!)
            }

            dataInputStream.close()
            dataOutputStream.close()
            socket.close()
        }catch (e:Exception){
            e.printStackTrace()
        }

        return ans
    }

    fun signup(clientName:String) : Boolean{
        var ans = false
        try {
            val socket = Socket(ip, port)
            val dataOutputStream = DataOutputStream(socket.getOutputStream())
            val dataInputStream =  DataInputStream(socket.getInputStream())

            val request = jsonRequest()
            request.operationId = 1
            request.clientName = clientName

            dataOutputStream.writeUTF(Gson().toJson(request))
            dataOutputStream.flush()

            val json = dataInputStream.readUTF()
            val response = Gson().fromJson(json, jsonResponse::class.java)
            if(response.clientId!! != -1){
                ans = true
                dt.clientId.save(response.clientId!!)
                dt.clientName.save(clientName)
            }

            dataInputStream.close()
            dataOutputStream.close()
            socket.close()
        }catch (e:Exception){
            e.printStackTrace()
        }

        return ans
    }

    fun getProductsList() : ArrayList<producto>{
        var ans = ArrayList<producto>()

        try {
            val socket = Socket(ip, port)
            val dataOutputStream = DataOutputStream(socket.getOutputStream())
            val dataInputStream =  DataInputStream(socket.getInputStream())

            val request = jsonRequest()
            request.operationId = 3

            dataOutputStream.writeUTF(Gson().toJson(request))
            dataOutputStream.flush()

            val json = dataInputStream.readUTF()
            Log.d("Downloads", json)
            val response = Gson().fromJson(json, jsonResponse::class.java)
            ans = response.productList!!

            dataInputStream.close()
            dataOutputStream.close()
            socket.close()
        }catch (e:Exception){
            e.printStackTrace()
        }

        return ans
    }

    fun getProductImage(productId:Int) : String?{
        try {
            val socket = Socket(ip, port)
            val dataOutputStream = DataOutputStream(socket.getOutputStream())
            val dataInputStream =  DataInputStream(socket.getInputStream())

            val request = jsonRequest()
            request.operationId = 50
            request.productId = productId

            dataOutputStream.writeUTF(Gson().toJson(request))
            dataOutputStream.flush()

            val json = dataInputStream.readUTF()
            Log.d("Downloads", "On $productId -> $json")
            val response = Gson().fromJson(json, jsonResponse::class.java)
            if(response.isProductImageAvailable == null || !response.isProductImageAvailable!!)
                return null

            //Product Image is Available, so lets download it
            val fileName = dataInputStream.readUTF()
            File("$localDir/products/").mkdirs()
            val filePath = "$localDir/products/$fileName"
            val size:Long = dataInputStream.readLong()
            Log.d("Downloads", "Creating file $filePath")

            val file = File(filePath)
            file.createNewFile()
            val outputFile = DataOutputStream(FileOutputStream(file))
            val bytes = ByteArray(1024)

            var received:Long = 0
            var segment:Int = 0
            Log.d("Download Product Image", "Reading file $fileName \nSize = $size Bytes")
            while (received < size){
                segment = dataInputStream.read(bytes)
                outputFile.write(bytes, 0, segment)
                outputFile.flush()
                received += segment
                Log.d("Download Product Image","Received $received of $size Bytes")
            }
            Log.d("Download Product Image","File received successfully")
            outputFile.close()
            dataInputStream.close()
            dataOutputStream.close()
            socket.close()

            return filePath
        }catch (e:Exception){
            e.printStackTrace()
            return null
        }
    }

    fun loadProducts(){
        val listOfProducts = getProductsList()

        //Id, product, android path
        val productRelation = mutableMapOf<Int, Pair<producto, String>>()
        for(p in listOfProducts) {
            val path = getProductImage(p.idProducto) ?: continue
            productRelation[p.idProducto] = Pair(p, path)
            Log.d("Downloads", "Product ${p.idProducto} image downloaded successfully")
        }

        dt.productRelation.save(productRelation)
    }
}