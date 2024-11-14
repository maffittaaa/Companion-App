package pt.iade.games.companionapp

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
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
import pt.iade.games.companionapp.ui.data.ActivityData
import pt.iade.games.companionapp.ui.theme.CompanionAppTheme

class AnalysisMachineActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CompanionAppTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    innerPadding
                    val data = intent.getSerializableExtra("DATA", ActivityData::class.java)!!
                    ScreenManager(data)
                }
            }
        }
    }
}

@Composable
fun ScreenManager(
    data: ActivityData
) {
    var gameStarted by remember { mutableStateOf(false)}
    var gameEnded by remember { mutableStateOf(false)}

    if (gameStarted) {
        AnalysisMachine(
            onStartClick = { gameEnded = true },
            data = data
        )
    } else
    {
        StartScreen(onStartClick = { gameStarted = true },
            data = data)
    }
    if (gameEnded){
        EndScreen(onStartClick = { gameEnded = false },
            data = data)
    }
}

@Composable
fun AnalysisMachine(
    onStartClick: () -> Unit,
    data: ActivityData
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(data.darkColor)
    ){
        Text(
            text = "Analysis Machine Running",
            color = data.lightColor,
            modifier = Modifier.align(Alignment.Center)
        )
        Button(
            onClick = onStartClick,
            colors = ButtonDefaults.buttonColors(
                containerColor = data.lightColor,
                contentColor = data.darkColor
            )
        ) {
            Text("End Game", color = data.darkColor)
        }
    }

}

@Composable
fun StartScreen(onStartClick: () -> Unit, data: ActivityData) {
    // centered "Start Game" button
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(data.darkColor),
        contentAlignment = Alignment.Center
    ) {
        Button(
            onClick = onStartClick,
            colors = ButtonDefaults.buttonColors(
                containerColor = data.lightColor,
                contentColor = data.darkColor
            )) {
            Text("Start Game", color = data.darkColor)
        }

    }
}

@Composable
fun EndScreen(onStartClick: () -> Unit, data: ActivityData) {
    val context = LocalContext.current
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(data.darkColor),
        contentAlignment = Alignment.Center
    ) {
        Button(
            onClick = {val intent = Intent(context, MainActivity::class.java)
                context.startActivity(intent) },
            colors = ButtonDefaults.buttonColors(
                containerColor = data.lightColor,
                contentColor = data.darkColor
            )
        ) {
            Text("Back to Main", color = data.darkColor)
        }
    }
}



@Preview(showBackground = true)
@Composable
fun AnalysisMachinePreview() {
    AnalysisMachine(
        onStartClick = {},
        data = ActivityData(
            lightColor = Color.LightGray,
            darkColor = Color.Red
        )
    )
}

@Preview(showBackground = true)
@Composable
fun StartScreenPreview() {
    StartScreen(
        onStartClick = {},
        data = ActivityData(
            lightColor = Color.LightGray,
            darkColor = Color.Red
        )
    )
}

@Preview(showBackground = true)
@Composable
fun EndScreenPreview() {
    EndScreen(onStartClick = {},
        data = ActivityData(
            lightColor = Color.LightGray,
            darkColor = Color.Red))
}