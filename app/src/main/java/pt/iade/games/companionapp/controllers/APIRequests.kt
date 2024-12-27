package pt.iade.games.companionapp.controllers

import android.util.Log
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.json.FuelJson
import com.github.kittinunf.fuel.json.responseJson

open class APIRequests {
    val serverBase = "https://the-rumble-server.vercel.app"
    var postResponse: FuelJson? = null
    var unity_connection_id = 0
    var unity_android_connection_id = 0
    var android_connection_id = 0
    var android_connection_code = 0

    fun Get() : FuelJson? {
        var res: FuelJson? = null
        Fuel.get("$serverBase/androidConnection")
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
        callback: () -> Unit
        ){
        Fuel.post("$serverBase/androidConnection", params)
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
}