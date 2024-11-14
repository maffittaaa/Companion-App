package pt.iade.games.companionapp

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.graphics.convertTo
import androidx.core.graphics.toColor
import androidx.core.graphics.toColorInt
import com.google.gson.Gson
import pt.iade.games.companionapp.ui.data.ActivityData
import pt.iade.games.companionapp.ui.theme.CompanionAppTheme

class AnalysisMachineActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CompanionAppTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    innerPadding
                    //val dataJson = intent.getStringExtra("DATA")
                    ScreenManager()
                }
            }
        }
    }
}

@Composable
fun ScreenManager(){
    var gameStarted by remember { mutableStateOf(false)}
    var gameEnded by remember { mutableStateOf(false)}

    if (gameStarted){
        //val dataJson = "{\"darkColor\":\"#22253A\"}"
        AnalysisMachine(onStartClick = { gameEnded = true })
    } else
    {
        StartScreen(onStartClick = { gameStarted = true })
    }
    if (gameEnded){
        EndScreen(onStartClick = { gameEnded = false })
    }
}

@Composable
fun AnalysisMachine(
    onStartClick: () -> Unit
    //dataJson: String?
) {
    //val data = Gson().fromJson(dataJson, ActivityData::class.java)
    val backgroundColor = Color(34, 37, 58, 255)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
    ){
        Text(
            text = "Analysis Machine Running",
            color = Color.White,
            modifier = Modifier.align(Alignment.Center)
        )
        Button(
            onClick = onStartClick,
        ) {
            Text("End Game", color = Color.White)
        }
    }

}

@Composable
fun StartScreen(onStartClick: () -> Unit) {
    // centered "Start Game" button
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        Button(
            onClick = onStartClick,
            ) {
            Text("Start Game", color = Color.White)
        }

    }
}

@Composable
fun EndScreen(onStartClick: () -> Unit) {
    val context = LocalContext.current
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        Button(
            onClick = {val intent = Intent(context, MainActivity::class.java)
                context.startActivity(intent) }
        ) {
            Text("Back to Main", color = Color.White)
        }
    }
}



@Preview(showBackground = true)
@Composable
fun AnalysisMachinePreview() {
    AnalysisMachine(onStartClick = {})
}

@Preview(showBackground = true)
@Composable
fun StartScreenPreview() {
    StartScreen(onStartClick = {})
}

@Preview(showBackground = true)
@Composable
fun EndScreenPreview() {
    EndScreen(onStartClick = {})
}