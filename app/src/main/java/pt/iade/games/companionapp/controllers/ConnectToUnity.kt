package pt.iade.games.companionapp.controllers

import pt.iade.games.companionapp.MainActivity

class ConnectToUnity : APIRequests() {
    fun ConnectWithCode(code: Int, function: () -> Unit){
        val params = listOf("android_connection_code" to code)
        android_connection_code = code
        Post(params, {
            ConnectionSuccessful(function)
        })
    }

    fun ConnectionSuccessful(function: () -> Unit) {
        if(postResponse?.obj()!!.getBoolean("connection_successful")){
            function()
            unity_connection_id = postResponse?.obj()!!.getInt("unity_connection_id")
            android_connection_id = postResponse?.obj()!!.getInt("android_connection_id")
            unity_android_connection_id = postResponse?.obj()!!.getInt("unity_android_connection_id")

            println("unity = ${unity_connection_id}, both = ${unity_android_connection_id}, android = ${android_connection_id}")
        }else{
         //Code not found
        }
    }
}