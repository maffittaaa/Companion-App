package pt.iade.games.companionapp.controllers

import android.content.Context
import android.content.Intent
import android.util.Log
import org.json.JSONObject
import pt.iade.games.companionapp.MainActivity
import kotlin.math.sqrt

class Vector(@JvmField var x: Float, @JvmField var y: Float)

class Treasure : APIRequests() {
    fun GetPlayerStats(success: () -> Unit, failure: () -> Unit, notConnected: () -> Unit, context: Context){
        val params = listOf(
            "unity_android_connection_id" to unity_android_connection_id,
            "android_connection_id" to android_connection_id,
        )
        Post(params,
            "/playerStats/getPositions",
            {
                if(postResponse?.obj()!!.getBoolean("connection_successful")){
                    GetNearbyCollectibles(success, failure)
                }else{
                    notConnected()
                }
            })
    }

    fun GetNearbyCollectibles(success: () -> Unit, failure: () -> Unit){
        if(!postResponse?.obj()!!.getBoolean("player_isdead")){

            val player = postResponse?.obj()!!.getJSONObject("player")
            val player_regionOfMap = player.getString("player_region_of_map_id")

            val params = listOf(
                "player_regionOfMap" to player_regionOfMap,
            )
            Post(params,
                "/collectiblesStats/getPositions"
            ) {
                Log.d("Found Collectibles", postResponse?.obj()!!.getBoolean("found_collectibles").toString())
                if (postResponse?.obj()!!.getBoolean("found_collectibles")) {
                    val collectibleOne = postResponse?.obj()!!.getJSONObject("collectibleOne")
                    val collectibleTwo = postResponse?.obj()!!.getJSONObject("collectibleTwo")
                    val collectibleThree = postResponse?.obj()!!.getJSONObject("collectibleThree")

                    val minDistance = 20

                    val found = FoundByPlayer(player, collectibleOne, collectibleTwo, collectibleThree, minDistance)
                    Log.d("Found Nearby", found.toString())

                    if(found != 0){
                        UpdateCollectibleFound(found)
                        success()
                    }
                }
            }
        }else{
            Log.d("MESSAGE", postResponse?.obj()!!.getString("message"))
            failure()
        }
    }

    fun UpdateCollectibleFound( id:Int ){
        val params = listOf(
            "collectible_id" to id,
            "unity_android_connection_id" to unity_android_connection_id,
        )
        Post(params,
            "/collectiblesStats/updateCollectibleFound"
        ){Log.d("SUCESSO", "SUCESSO")}
    }

    fun FoundByPlayer(
        player: JSONObject,
        collectibleOne: JSONObject,
        collectibleTwo: JSONObject,
        collectibleThree: JSONObject,
        dist: Int
    ) : Int{
        var found = 0
        val list = listOf(collectibleOne, collectibleTwo, collectibleThree)

        var i = 0
        while (3 > i){
            val collectibleX = Math.sqrt(Math.pow(list[i].getDouble("collectibles_position_x"), 2.0))
            val collectibleY = Math.sqrt(Math.pow(list[i].getDouble("collectibles_position_y"), 2.0))
            val playerX = Math.sqrt(Math.pow(player.getDouble("player_position_x"), 2.0))
            val playerY = Math.sqrt(Math.pow(player.getDouble("player_position_y"), 2.0))

            val vectorBetween = SubtractingVectors(Vector(playerX.toFloat(), playerY.toFloat()), Vector(collectibleX.toFloat(), collectibleY.toFloat()))
            val distanceBetween = MagnitudeVector(vectorBetween)

            if( 0 > (distanceBetween - dist)){
                found = list[i].getInt("collectibles_id")
                break
            }

            i++
        }

        return found
    }

    fun MagnitudeVector(
        vec: Vector
    ) : Float{
        return sqrt((vec.x * vec.x) + (vec.y * vec.y))
    }

    fun SubtractingVectors(
        vec1: Vector,
        vec2: Vector
    ) : Vector{
        return Vector(vec1.x - vec2.x, vec1.y - vec2.y)
    }
}