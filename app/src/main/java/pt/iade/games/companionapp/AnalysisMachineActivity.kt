package pt.iade.games.companionapp

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import pt.iade.games.companionapp.ui.data.ActivityData
import pt.iade.games.companionapp.ui.theme.CompanionAppTheme

data class Circle(
    var x: Float,
    var y: Float,
    val radius: Float,
    var speed: Float,
    val color: Color,
    var visible: Boolean = true
)

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
    var isTimerRunning by remember { mutableStateOf(false) }

    if (gameStarted) {
        AnalysisMachine(
            onStartClick = { gameEnded = true },
            onTimerEnd = { gameEnded = true },
            isTimerRunning = isTimerRunning,
            data = data
        )
    } else
    {
        StartScreen(onStartClick = {
            gameStarted = true
            isTimerRunning = true
        }, data = data)
    }

    if (gameEnded){
        EndScreen(onStartClick = {
            gameEnded = false
            gameStarted = false
            isTimerRunning = false
          }, data = data)
    }

}

@Composable
fun AnalysisMachine(
    onStartClick: () -> Unit,
    onTimerEnd: () -> Unit,
    isTimerRunning: Boolean,
    data: ActivityData
) {
    val context = LocalContext.current
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(data.darkColor),
        contentAlignment = Alignment.Center
    ){
        Column(
            modifier = Modifier.align(Alignment.Center),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Analysis Machine Running",
                color = data.lightColor,
            )
            RoundTimer(data = data, onTimerEnd = onTimerEnd)
            if (isTimerRunning) {
                //ItemRainGame(data)
            }
            Button(
                onClick = {
                    context.startActivity(Intent(context, MainActivity::class.java))
                    onStartClick()
                          },
                colors = ButtonDefaults.buttonColors(
                    containerColor = data.lightColor,
                    contentColor = data.darkColor
                )
            ) {
                Text("End Game", color = data.darkColor)
            }
        }
    }
}

@Composable
fun StartScreen(onStartClick: () -> Unit, data: ActivityData) {
    val context = LocalContext.current
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(data.darkColor),
    ) {
        Button(
            onClick = onStartClick,
            colors = ButtonDefaults.buttonColors(
                containerColor = data.lightColor,
                contentColor = data.darkColor
            ),
            modifier = Modifier.align(Alignment.Center)
            ) {
            Text("Start Game", color = data.darkColor)
        }
        Button(
            onClick = {val intent = Intent(context, MainActivity::class.java)
                context.startActivity(intent) },
            colors = ButtonDefaults.buttonColors(
                containerColor = data.lightColor,
                contentColor = data.darkColor
            ),
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(16.dp)
        ) {
            Text("Back", color = data.darkColor)
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

@Composable
fun RoundTimer(data: ActivityData, onTimerEnd: () -> Unit){
    val totalTime = 60
    var timeLeftInRound by remember { mutableIntStateOf(totalTime) }
    var isTimerRunning by remember { mutableStateOf(true) }

    LaunchedEffect(isTimerRunning) {
        while (isTimerRunning && timeLeftInRound > 0) {
            delay(1000L) // Wait for 1 second
            timeLeftInRound--
        }
        if (timeLeftInRound <= 0) {
            onTimerEnd()
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "Time Left: ${timeLeftInRound / 60}:${(timeLeftInRound % 60).toString().padStart(2, '0')}",
                color = data.lightColor
            )
        }
    }
}

@Composable
fun itemRain(){
    val context = LocalContext.current
    Toast.makeText(context, "hi", Toast.LENGTH_SHORT).show()
}




@Preview(showBackground = true)
@Composable
fun AnalysisMachinePreview() {
    AnalysisMachine(
        onStartClick = {},
        data = ActivityData(
            lightColor = Color.LightGray,
            darkColor = Color.Red
        ),
        onTimerEnd = {},
        isTimerRunning = true
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
