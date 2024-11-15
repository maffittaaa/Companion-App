package pt.iade.games.companionapp

import android.content.Context.MODE_PRIVATE
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
import kotlin.math.hypot
import pt.iade.games.companionapp.ui.data.ActivityData
import pt.iade.games.companionapp.ui.theme.CompanionAppTheme

data class Pills(
    var x: Float,
    var y: Float,
    val radius: Float,
    var speed: Float,
    val color: Color,
    var visible: Boolean = true,
    val isGood: Boolean = true,
    val velocityY: Float = 2f
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
    var score by remember { mutableIntStateOf(0) }

    if (gameStarted) {
        AnalysisMachine(
            onStartClick = { gameEnded = true },
            onTimerEnd = { gameEnded = true },
            isTimerRunning = isTimerRunning,
            data = data,
            score = score,
            onScoreChange = { newScore -> score = newScore }
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
        }, data = data,
            score = score)
    }

}

@Composable
fun AnalysisMachine(
    onStartClick: () -> Unit,
    onTimerEnd: () -> Unit,
    isTimerRunning: Boolean,
    score: Int,
    onScoreChange: (Int) -> Unit,
    data: ActivityData
) {
    var pills by remember { mutableStateOf(generatePills(data)) }
    val context = LocalContext.current
    val screenHeight = LocalContext.current.resources.displayMetrics.heightPixels.toFloat()
    val maxPills = 40

    LaunchedEffect(isTimerRunning) {
        while (isTimerRunning) {
            delay(500L)

            if (pills.size < maxPills) {
                pills = pills + generatePills(data)
            }
        }
    }


    LaunchedEffect(pills) {
        while (true) {
            delay(16L)
            pills = pills.map { pill ->
                if (pill.y > screenHeight) {
                    null
                } else {
                    pill.copy(y = pill.y + pill.velocityY)
                }
            }.filterNotNull()
        }
    }


    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(data.darkColor),
        contentAlignment = Alignment.Center
    ){
        pills.forEachIndexed { index, pill ->
            if (pill.visible) {
                if (pill.isGood) {
                    Box(
                        modifier = Modifier
                            .offset(x = pill.x.dp, y = pill.y.dp)
                            .size(pill.radius.dp)
                            .background(pill.color, CircleShape)
                            .clickable {
                                pills = pills.mapIndexed { i, c ->
                                    if (i == index) c.copy(visible = false) else c
                                }
                                onScoreChange(score + 1)
                            }
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .offset(x = pill.x.dp, y = pill.y.dp)
                            .size(pill.radius.dp)
                            .background(pill.color)
                            .clickable {
                                pills = pills.mapIndexed { i, c ->
                                    if (i == index) c.copy(visible = false) else c
                                }
                                onScoreChange((score - 5).coerceAtLeast(0))
                            }
                    )
                }
            }
        }

        Text(
            text = "Score: $score",
            color = data.lightColor,
            modifier = Modifier.align(Alignment.TopCenter).padding(16.dp)
        )

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
    val sharedPreferences = context.getSharedPreferences("GamePrefs", MODE_PRIVATE)
    val totalFlasks = sharedPreferences.getInt("antiRadiationFlasks", 0).coerceAtMost(8) // Ensure max is 8
    val cooldownEndTime = sharedPreferences.getLong("cooldownEndTime", 0L)

    val currentTime = System.currentTimeMillis()
    var remainingTime by remember { mutableIntStateOf(((cooldownEndTime - currentTime) / 1000).toInt()) }
    val isInCooldown = totalFlasks >= 8 && remainingTime > 0

    LaunchedEffect(isInCooldown) {
        if (isInCooldown) {
            while (remainingTime > 0) {
                delay(1000L)
                remainingTime -= 1
            }
        } else if (cooldownEndTime > 0) {
            sharedPreferences.edit().putInt("antiRadiationFlasks", 0).apply()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(data.darkColor),
    ) {
        Column(
            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (isInCooldown) {
                val minutes = (remainingTime / 60).toString().padStart(2, '0')
                val seconds = (remainingTime % 60).toString().padStart(2, '0')

                Text(
                    text = "Cooldown active! Wait $minutes:$seconds before playing again.",
                    color = data.lightColor,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            } else {
                Button(
                    onClick = onStartClick,
                    enabled = !isInCooldown,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = data.lightColor,
                        contentColor = data.darkColor
                    )
                ) {
                    Text("Start Game", color = data.darkColor)
                }
            }

            Text(
                text = "Total Anti-Radiation Flasks: $totalFlasks",
                color = data.lightColor,
                modifier = Modifier.padding(top = 16.dp)
            )
        }

        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(
                onClick = {
                    val newCooldownTime = System.currentTimeMillis() + 5000L
                    sharedPreferences.edit().putLong("cooldownEndTime", newCooldownTime).apply()
                    remainingTime = 5
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = data.lightColor,
                    contentColor = data.darkColor
                )
            ) {
                Text("Cheats: Skip Cooldown", color = Color.Black)
            }
        }

        Button(
            onClick = {
                val intent = Intent(context, MainActivity::class.java)
                context.startActivity(intent)
            },
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
fun EndScreen(onStartClick: () -> Unit, data: ActivityData, score: Int) {
    val context = LocalContext.current
    val antiRadiationFlasks = (score * 0.1).toInt()

    LaunchedEffect(antiRadiationFlasks) {
        val sharedPreferences = context.getSharedPreferences("GamePrefs", MODE_PRIVATE)
        val currentFlasks = sharedPreferences.getInt("antiRadiationFlasks", 0)

        var newTotalFlasks = currentFlasks + antiRadiationFlasks
        if (newTotalFlasks > 8) {
            newTotalFlasks = 8
        }

        val editor = sharedPreferences.edit()
        editor.putInt("antiRadiationFlasks", newTotalFlasks)


        if (newTotalFlasks >= 8) {
            val cooldownEndTime = System.currentTimeMillis() + 3600000L // 1 hour in milliseconds
            editor.putLong("cooldownEndTime", cooldownEndTime)
        }

        editor.apply()
    }


    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(data.darkColor),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "Final Score: $score",
                color = data.lightColor,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Text(
                text = "Anti-Radiation Flasks: $antiRadiationFlasks",
                color = data.lightColor,
                modifier = Modifier.padding(bottom = 32.dp)
            )

            Button(
                onClick = {
                    val intent = Intent(context, MainActivity::class.java)
                    context.startActivity(intent)
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = data.lightColor,
                    contentColor = data.darkColor
                )
            ) {
                Text("Back to Main", color = data.darkColor)
            }
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
            delay(1000L)
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

@Composable
fun PillsClicker() {

}

fun generatePills(data: ActivityData): List<Pills> {
    val pills = mutableListOf<Pills>()
    val minDistance = 50f
    val pillRadius = 50f

    repeat(1) {
        var newPill: Pills
        var isPositionValid: Boolean
        var attempts = 0
        val maxAttempts = 100

        do {
            val x = (-200..200).random().toFloat()
            val y = (-1000..-500).random().toFloat()

            newPill = Pills(
                x = x,
                y = y,
                radius = pillRadius,
                speed = 0f,
                color = data.lightColor,
                visible = true,
                isGood = (0..3).random() <= 1,
                velocityY = 3f
            )

            isPositionValid = pills.all { existingPill ->
                val distance = hypot((existingPill.x - newPill.x), (existingPill.y - newPill.y))
                distance >= (existingPill.radius + newPill.radius + minDistance)
            }

            attempts++
        } while (!isPositionValid && attempts < maxAttempts)

        if (isPositionValid) {
            pills.add(newPill)
        }
    }

    return pills
}



@Preview(showBackground = true)
@Composable
fun AnalysisMachinePreview() {
    // example score state
    var score by remember { mutableIntStateOf(10) }

    AnalysisMachine(
        onStartClick = {},
        data = ActivityData(
            lightColor = Color.LightGray,
            darkColor = Color.Red
        ),
        onTimerEnd = {},
        isTimerRunning = true,
        score = score,
        onScoreChange = { newScore -> score = newScore }
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
    EndScreen(
        onStartClick = {},
        data = ActivityData(
            lightColor = Color.LightGray,
            darkColor = Color.Red
        ),
        score = 25
    )
}
