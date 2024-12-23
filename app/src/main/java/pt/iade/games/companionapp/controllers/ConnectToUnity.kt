package pt.iade.games.companionapp.controllers

import pt.iade.games.companionapp.MainActivity

class ConnectToUnity : APIRequests() {
    fun ConnectWithCode(code: Int, function: () -> Unit){
        val params = listOf("android_connection_code" to code)

        Post(params, {
            ConnectionSuccessful(function)
        })
    }

    fun ConnectionSuccessful(function: () -> Unit) {
        if(postResponse?.obj()!!.getBoolean("connection_successful")){
            function()
        }else{
         //Code not found
        }
    }
}