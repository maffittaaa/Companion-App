package pt.iade.games.companionapp.controllers

import android.util.Log
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.json.responseJson

class UpdateAntiRadiationFlasks : APIRequests() {
   fun insertFlasks(flaskCount: Int, unity_android_connection_id: Int){
       val params = listOf(
           "unity_android_connection_id" to pt.iade.games.companionapp.controllers.unity_android_connection_id,
           "flask_count" to flaskCount
       )

       Post(params, "/flasks/createFlasks",) {
           // success
           val success = postResponse?.obj()?.getBoolean("update_successful")
           if (success == true) {
               Log.d("flask", "Flask inserted successfully.")
           }
           //failure
           else {
               Log.d(
                   "flask",
                   "Flask insertion failed: ${postResponse?.obj()?.getString("message")}"
               )
           }
       }
   }

   fun checkRows(unity_android_connection_id: Int, onResult: (Boolean) -> Unit){
       val params = listOf(
           "unity_android_connection_id" to pt.iade.games.companionapp.controllers.unity_android_connection_id,
       )

       Post(params, "/flasks/checkIfRowsExist",) {
           // success
           val success = postResponse?.obj()?.getBoolean("update_successful")
           if (success == true) {
               val rowsExist = postResponse?.obj()!!.getBoolean("update_successful")
               Log.d("flask", "Flask table checked successfully.")
               onResult(true)
           }
           //failure
           else {
               Log.d(
                   "flask",
                   "Flask check failed: ${postResponse?.obj()?.getString("message")}"
               )
               onResult(false)
           }
       }
   }

    fun updateFlasks(flaskCount: Int) {
        //if theres no connection then show fail
        if (unity_android_connection_id == 0) {
            Log.d("CONNECTION ERROR", "No unity connection ID. Cannot send flask update.")
            return
        }

        val params = listOf(
            "unity_android_connection_id" to unity_android_connection_id,
            "flask_count" to flaskCount
        )

        Post(params, "/flasks/updateFlasks",) {
            // success
            val success = postResponse?.obj()?.getBoolean("update_successful")
            if (success == true) {
                Log.d("flask", "Flask update sent successfully.")
            }
            //failure
            else {
                Log.d(
                    "flask",
                    "Flask update failed: ${postResponse?.obj()?.getString("message")}"
                )
            }
        }
    }
}