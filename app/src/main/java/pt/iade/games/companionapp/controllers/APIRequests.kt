package pt.iade.games.companionapp.controllers

import android.util.Log
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.json.FuelJson
import com.github.kittinunf.fuel.json.responseJson

var unity_connection_id = 0
var unity_android_connection_id = 0
var android_connection_id = 0
var android_connection_code = 0

open class APIRequests {
    val serverBase = "https://the-rumble-server.vercel.app"
    var postResponse: FuelJson? = null


    fun Get(
        endpoint: String,
    ) : FuelJson? {
        var res: FuelJson? = null
        Fuel.get("$serverBase$endpoint")
            .responseJson { request, response, result ->
                val (json, error) = result
                if (json != null) {
                    res = json
                }else{
                    Log.e("ERROR","FAILED BECAUSE: $error")
                }
            }
        return res
    }

    fun Post(
        params: List<Pair<String, Any>>,
        endpoint: String,
        callback: () -> Unit
        ){
        Fuel.post("$serverBase$endpoint", params)
            .responseJson { request, response, result ->
                val (json, error) = result
                if (json != null) {
                    postResponse = json
                    callback()
                }else{
                    Log.e("ERROR","FAILED BECAUSE: $error")
                }
            }
    }

    fun SetVariables(
        unity_connection: Int,
        unity_android_connection: Int,
        android_connection: Int,
        connection_code: Int,
    ){
        unity_connection_id = unity_connection
        unity_android_connection_id = unity_android_connection
        android_connection_id = android_connection
        android_connection_code = connection_code
    }

    fun GetUnityAndroidConnection(
    ) : Int{
        return unity_android_connection_id
    }
}