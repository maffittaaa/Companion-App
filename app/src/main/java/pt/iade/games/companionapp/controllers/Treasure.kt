package pt.iade.games.companionapp.controllers

import android.util.Log

class Treasure : APIRequests() {
    fun GetPlayerStats(success: () -> Unit, failure: () -> Unit){
        val params = listOf(
            "unity_android_connection_id" to unity_android_connection_id,
        )
        Post(params,
            "/playerStats/getPositions",
            { GetNearbyCollectibles(success, failure) })
    }

    fun GetNearbyCollectibles(success: () -> Unit, failure: () -> Unit){
        if(!postResponse?.obj()!!.getBoolean("player_isdead")){

            val player_position_x = postResponse?.obj()!!.getInt("player_position_x")
            val player_position_y = postResponse?.obj()!!.getInt("player_position_y")
            val player_regionOfMap = postResponse?.obj()!!.getInt("player_regionOfMap")

            val params = listOf(
                "player_regionOfMap" to player_regionOfMap,
            )
            Post(params,
                "/collectiblesStats/getPositions",
                {
                    val collectibles = postResponse?.obj()!!.getInt("collectibles")
                    Log.d(postResponse?.obj()!!.getString("message"), collectibles.toString())
                    //success()
                })
        }else{
            Log.d("MESSAGE", postResponse?.obj()!!.getString("message"))
            failure()
        }
    }
}