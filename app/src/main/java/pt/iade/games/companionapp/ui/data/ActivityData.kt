package pt.iade.games.companionapp.ui.data

import androidx.compose.ui.graphics.Color
import java.io.Serializable

data class ActivityData(
    val darkColor: Color,
    val lightColor: Color,
) : Serializable

// val sem = Gson().fromJson(semJson, ActivityData::class.java) from Json
// val projectJson = Gson().toJson(project) to Json

// Intent(context, Scene::class.java).apply {  Send Data
//                    putExtra("NAMEOFSTRINGTOSEND", stringToSend)
//                }

//val semJson = intent.getStringExtra("SEMESTER") Get data