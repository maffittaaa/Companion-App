package pt.iade.games.companionapp.controllers

import android.util.Log
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.json.responseJson


class UpdateAntiRadiationFlasks {
    val serverBase = "https://the-rumble-server.vercel.app"

    fun ConnectToComputer(
        code: Int = 0,
    ){
        val params = listOf(
            "code" to code,
        )

        Fuel.post("$serverBase/connect", params)
            .responseJson { request, response, result ->
                Log.e("INFO REQUEST", request.toString())
                Log.e("INFO RESPONSE", response.toString())
                val (json, error) = result
                if (json != null) {
                    Log.e("NO ERROR","[response bytes] $result")
                }else{
                    Log.e("ERROR","FAILED BECAUSE: $error")
                    //onFailure()
                }
            }

    }

    fun GetNewConnection(
        onSuccess: (Int) -> Unit = {},
        onFailure: () -> Unit = {}
    ) : Int{
        var code = -1
        Fuel.get("$serverBase/create")
            .responseJson { request, response, result ->
                val (json, error) = result
                if (json != null) {
                    Log.e("NO ERROR","[response bytes] $result")
                    val jsonCode = json.obj().getInt("num")
                    code = jsonCode
                }else{
                    Log.e("ERROR","FAILED BECAUSE: $error")
                    return@responseJson
                    //onFailure()
                }
            }
        return code
    }
}
