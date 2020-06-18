package mx.ipn.escom.tienditappclient.utils

import android.content.Context
import android.os.Environment
import android.util.Log
import com.google.gson.Gson
import jsonRequest
import jsonResponse
import producto
import java.io.*
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

    fun addToCart(productId: Int, amount:Int) : Boolean {
        var ans = false
        try {
            val socket = Socket(ip, port)
            val dataOutputStream = DataOutputStream(socket.getOutputStream())
            val dataInputStream = DataInputStream(socket.getInputStream())

            val request = jsonRequest()
            request.operationId = 8
            request.clientId = dt.clientId.getData()
            request.productId = productId
            request.productAmount = amount

            dataOutputStream.writeUTF(Gson().toJson(request))
            dataOutputStream.flush()

            val json = dataInputStream.readUTF()
            val response = Gson().fromJson(json, jsonResponse::class.java)
            if (response.isCartUpdateSuccessful!!) {
                ans = true
                val cart = dt.cart.getData()
                var actAmount: Int = if (cart[productId] == null) 0 else cart[productId]!!
                actAmount++
                cart[productId] = actAmount
                Log.d("Cart", Gson().toJson(cart))
                dt.cart.save(cart)
                dt.total.save((response.total!!))
            }

            dataInputStream.close()
            dataOutputStream.close()
            socket.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return ans
    }

    fun quitFromCart(productId: Int, amount:Int) : Boolean {
        var ans = false
        try {
            val socket = Socket(ip, port)
            val dataOutputStream = DataOutputStream(socket.getOutputStream())
            val dataInputStream = DataInputStream(socket.getInputStream())

            val request = jsonRequest()
            request.operationId = 9
            request.clientId = dt.clientId.getData()
            request.productId = productId
            request.productAmount = amount

            dataOutputStream.writeUTF(Gson().toJson(request))
            dataOutputStream.flush()

            val json = dataInputStream.readUTF()
            val response = Gson().fromJson(json, jsonResponse::class.java)
            if (response.isCartUpdateSuccessful!!) {
                ans = true
                val cart = dt.cart.getData()
                var actAmount: Int = if (cart[productId] == null) 0 else cart[productId]!!
                actAmount--
                cart[productId] = actAmount
                Log.d("Cart", Gson().toJson(cart))
                dt.cart.save(cart)
                dt.total.save((response.total!!))
            }

            dataInputStream.close()
            dataOutputStream.close()
            socket.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return ans
    }

    fun getCart() : Boolean {
        var ans = false
        try {
            val socket = Socket(ip, port)
            val dataOutputStream = DataOutputStream(socket.getOutputStream())
            val dataInputStream = DataInputStream(socket.getInputStream())

            val request = jsonRequest()
            request.operationId = 11
            request.clientId = dt.clientId.getData()

            dataOutputStream.writeUTF(Gson().toJson(request))
            dataOutputStream.flush()

            val json = dataInputStream.readUTF()
            val response = Gson().fromJson(json, jsonResponse::class.java)
            if (response.cartList != null) {
                ans = true
                dt.cart.save(response.cartList as MutableMap<Int, Int>)
                dt.total.save((response.total!!))
            }

            dataInputStream.close()
            dataOutputStream.close()
            socket.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return ans
    }

    fun updateProduct(productId: Int, productName:String, productPrice:Double, productAmount:Int) : Boolean{
        var ans = false
        try {
            val socket = Socket(ip, port)
            val dataOutputStream = DataOutputStream(socket.getOutputStream())
            val dataInputStream =  DataInputStream(socket.getInputStream())

            val request = jsonRequest()
            request.operationId = 6
            request.productId = productId
            request.productName = productName
            request.productPrice = productPrice
            request.productAmount = productAmount

            dataOutputStream.writeUTF(Gson().toJson(request))
            dataOutputStream.flush()

            val json = dataInputStream.readUTF()
            val response = Gson().fromJson(json, jsonResponse::class.java)
            if(response.productUpdateSuccessful!!){
                ans = true
                dt.productRelation.getData()[productId]?.first?.nombre = productName
                dt.productRelation.getData()[productId]?.first?.precio = productPrice
                dt.productRelation.getData()[productId]?.first?.cantidadDisponible = productAmount
            }

            dataInputStream.close()
            dataOutputStream.close()
            socket.close()
        }catch (e:Exception){
            e.printStackTrace()
        }

        return ans
    }

    fun addProduct(productName:String, productSKU:String,  productPrice:Double, productAmount:Int, productImagePath:String) : Boolean{
        var ans = false
        try {
            val socket = Socket(ip, port)
            val dataOutputStream = DataOutputStream(socket.getOutputStream())
            val dataInputStream =  DataInputStream(socket.getInputStream())

            val request = jsonRequest()
            request.operationId = 4
            request.productName = productName
            request.productSku = productSKU
            request.productPrice = productPrice
            request.productAmount = productAmount

            dataOutputStream.writeUTF(Gson().toJson(request))
            dataOutputStream.flush()

            val extension: String = productImagePath.substring(productImagePath.lastIndexOf("."))
            dataOutputStream.writeUTF(extension)
            dataOutputStream.flush()

            val file = File(productImagePath)
            val size = file.length()
            dataOutputStream.writeLong(size)
            dataOutputStream.flush()

            val fileInput = DataInputStream(FileInputStream(file))
            val bytes = ByteArray(1024)
            var sent:Long = 0
            var n = 0

            while(sent < size){
                n = fileInput.read(bytes)
                dataOutputStream.write(bytes, 0, n)
                dataOutputStream.flush()
                sent += n
                Log.d("Sending File", "Progress= " + (sent/size)*100)
            }

            Log.d("Sending File", "Archivo enviado con éxito")

            val json = dataInputStream.readUTF()
            val response = Gson().fromJson(json, jsonResponse::class.java)
            if(response.productId != -1){
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

    fun deleteProduct(productId: Int) : Boolean{
        var ans = false
        try {
            val socket = Socket(ip, port)
            val dataOutputStream = DataOutputStream(socket.getOutputStream())
            val dataInputStream =  DataInputStream(socket.getInputStream())

            val request = jsonRequest()
            request.operationId = 5
            request.productId = productId

            dataOutputStream.writeUTF(Gson().toJson(request))
            dataOutputStream.flush()

            val json = dataInputStream.readUTF()
            val response = Gson().fromJson(json, jsonResponse::class.java)
            if(response.productRemoveSuccessful!!){
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

    fun newPurchase() : Int{
        var ans = -1
        try {
            val socket = Socket(ip, port)
            val dataOutputStream = DataOutputStream(socket.getOutputStream())
            val dataInputStream =  DataInputStream(socket.getInputStream())

            val request = jsonRequest()
            request.operationId = 7
            request.clientId = dt.clientId.getData()
            request.cartList = dt.cart.getData()

            dataOutputStream.writeUTF(Gson().toJson(request))
            dataOutputStream.flush()

            val json = dataInputStream.readUTF()
            val response = Gson().fromJson(json, jsonResponse::class.java)
            if(response.purchaseId!! != -1){
                ans = response.purchaseId!!
            }

            dataInputStream.close()
            dataOutputStream.close()
            socket.close()
        }catch (e:Exception){
            e.printStackTrace()
        }
        return ans
    }

    fun downloadReceipt(purchaseId:Int) : File?{
        try {
            val socket = Socket(ip, port)
            val dataOutputStream = DataOutputStream(socket.getOutputStream())
            val dataInputStream =  DataInputStream(socket.getInputStream())

            val request = jsonRequest()
            request.operationId = 51
            request.purchaseId = purchaseId

            dataOutputStream.writeUTF(Gson().toJson(request))
            dataOutputStream.flush()

            val json = dataInputStream.readUTF()
            Log.d("Downloads", "On puchase #$purchaseId -> $json")
            val response = Gson().fromJson(json, jsonResponse::class.java)
            if(response.isPurchasePDFAvailable == null || !response.isPurchasePDFAvailable!!)
                return null

            //Receipt PDF is Available, so lets download it
            val fileName = dataInputStream.readUTF()
            //File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString() + "/tienditApp/").mkdirs()
            val filePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                .toString() + "/$fileName"
            val size:Long = dataInputStream.readLong()
            Log.d("Downloads", "Creating file $filePath")

            val file = File(filePath)

            val success = true
            file.createNewFile()
            val outputFile = DataOutputStream(FileOutputStream(file))
            val bytes = ByteArray(1024)

            var received:Long = 0
            var segment:Int = 0
            Log.d("Download Receipt PDF", "Reading file $fileName \nSize = $size Bytes")
            while (received < size){
                segment = dataInputStream.read(bytes)
                outputFile.write(bytes, 0, segment)
                outputFile.flush()
                received += segment
                Log.d("Download Receipt PDF","Received $received of $size Bytes")
            }
            Log.d("Download Receipt PDF","File received successfully")
            outputFile.close()
            dataInputStream.close()
            dataOutputStream.close()
            socket.close()

            return file
        }catch (e:Exception){
            e.printStackTrace()
            return null
        }
    }

    fun buyCart() : File?{
        val purchaseId = newPurchase()
        if(purchaseId != -1){
            return  downloadReceipt(purchaseId)
        }
        return null
    }
}