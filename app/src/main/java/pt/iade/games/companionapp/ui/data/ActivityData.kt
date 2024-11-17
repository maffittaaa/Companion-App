package pt.iade.games.companionapp.ui.data

import androidx.compose.ui.graphics.Color
import java.io.Serializable

data class ActivityData(
    val darkColor: Color,
    val lightColor: Color,
) : Serializable

// Intent(context, Scene::class.java).apply {  Send Data
//                    putExtra("NAMEOFSTRINGTOSEND", stringToSend)
//                }

//intent.getStringExtra("SEMESTER") Get data