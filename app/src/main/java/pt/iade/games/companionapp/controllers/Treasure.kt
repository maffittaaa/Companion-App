package pt.iade.games.companionapp.controllers

import android.util.Log

class Treasure : APIRequests() {
    fun GetPlayerStats(function: () -> Unit){
        val params = listOf(
            "unity_android_connection_id" to unity_android_connection_id,
        )
        Log.d("unity_android_connection", unity_android_connection_id.toString())
        Post(params,
            "/playerStats/getPositions",
            { GetNearbyCollectibles(function) })
    }

    fun GetNearbyCollectibles(function: () -> Unit){
        return
        val params = listOf(
            "unity_android_connection_id" to unity_android_connection_id,
        )
        Post(params,
            "/collectiblesStats/getPositions",
            { function() })
    }
}