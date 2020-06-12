package mx.ipn.escom.tienditappclient.utils

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

//@author github.com/equetzal -> Enya Quetzalli
class dataModule <T> (context : Context, saveId : String, defaultAnswer : String){
    val context = context
    val saveId = saveId
    val statusId = "are" + saveId + "Saved"
    var objectType = object : TypeToken<T>() {}.type
    val defaultAnswer = defaultAnswer

    fun setSavedStatus(status : Boolean){
        context.getSharedPreferences(statusId, Context.MODE_PRIVATE).edit().putBoolean(statusId, status).apply()
    }
    fun isDataSaved() : Boolean{
        return context.getSharedPreferences(statusId, Context.MODE_PRIVATE).getBoolean(statusId, false)
    }
    fun save(data : T){
        context.getSharedPreferences(saveId, Context.MODE_PRIVATE).edit().putString(saveId, Gson().toJson(data)).apply()
    }
    fun getData() : T{
        return Gson().fromJson(context.getSharedPreferences(saveId, Context.MODE_PRIVATE).getString(saveId,defaultAnswer), objectType)
    }
    fun delete(){
        context.getSharedPreferences(statusId, Context.MODE_PRIVATE).edit().remove(saveId).apply()
        context.getSharedPreferences(statusId, Context.MODE_PRIVATE).edit().remove(statusId).apply()
    }
    inline fun <reified A> setType(){
        objectType = object : TypeToken<A>() {}.type
    }
}