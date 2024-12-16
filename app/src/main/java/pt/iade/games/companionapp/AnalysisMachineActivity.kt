package pt.iade.games.companionapp

import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import pt.iade.games.companionapp.ui.data.ActivityData
import pt.iade.games.companionapp.ui.theme.CompanionAppTheme
import kotlin.math.hypot


data class Pills(
    var x: Float,
    var y: Float,
    val radius: Float,
    val color: Color,
    var visible: Boolean = true,
    val isGood: Boolean = true,
    val velocityY: Float = 2f
)

var playersFlasks = 0

//todo: add incremental speed when pills are tapped

class AnalysisMachineActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CompanionAppTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    innerPadding
                    val data = intent.getSerializableExtra("DATA")!! as ActivityData
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
    var score by rememberSaveable { mutableStateOf(0) }
    var speedMultiplier by remember { mutableStateOf(1.0f) }

    var onScoreChange: (Int) -> Unit = { newScore ->
        Log.i("ScoreUpdate", "New score: $newScore")
        score = (score + newScore).coerceAtLeast(0)

    }

    if (gameStarted) {
        AnalysisMachine(
            onStartClick = { },
            onTimerEnd = { gameEnded = true },
            isTimerRunning = isTimerRunning,
            data = data,
            score = score,
            onScoreChange = onScoreChange,
            speedMultiplier = speedMultiplier,
            onSpeedMultiplierChange = { speedMultiplier += it}
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
            score = 0
            speedMultiplier = 1.0f
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
    data: ActivityData,
    speedMultiplier: Float,
    onSpeedMultiplierChange: (Float) -> Unit

) {
    val maxPills = 40
    var timeLeftInSeconds by remember { mutableStateOf(60) }
    var isSkipTimerClicked by remember { mutableStateOf(false) }
    var screenWidth = LocalContext.current.resources.displayMetrics.widthPixels.toFloat()
    var screenHeight = LocalContext.current.resources.displayMetrics.heightPixels.toFloat()
    var pills by remember { mutableStateOf(generatePills(data, screenWidth, screenHeight)) }

    val pillImage = ImageBitmap.imageResource(id = R.drawable.pill)
    val radiationImage = ImageBitmap.imageResource(id = R.drawable.radiation)

    LaunchedEffect(isTimerRunning, isSkipTimerClicked) {
        while (isTimerRunning && timeLeftInSeconds > 0) {
            if (isSkipTimerClicked) {
                timeLeftInSeconds = 5
                isSkipTimerClicked = false
            } else {
                delay(1000L)
                timeLeftInSeconds -= 1
            }

            if (timeLeftInSeconds == 0) {
                onTimerEnd()
            }
        }
    }

    LaunchedEffect(Unit) {
        while (true) {
            delay(1000L) // This will control how often new pills spawn, every 1 second in this case
            if (pills.size < maxPills) {
                pills = pills + generatePills(data, screenWidth, screenHeight) // Append new pills
            }
        }
    }

    LaunchedEffect(pills) {
        Log.d("pills", "$speedMultiplier")
        while (true) {
            delay(16L) // ~60 FPS
            pills = pills.map { pill ->
                if (pill.y > screenHeight) {
                    null // Remove pills that fall out of the screen
                } else {
                    pill.copy(y = pill.y + pill.velocityY * speedMultiplier)
                }
            }.filterNotNull()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(data.darkColor),
        contentAlignment = Alignment.Center
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectTapGestures { offset ->
                        // Check if any pill was tapped
                        pills = pills.map { pill ->
                            if (pill.visible && isTapped(offset, pill)) {
                                // Update score based on the type of pill
                                val updatedScore = if (pill.isGood) score + 1 else score - 2
                                onScoreChange(updatedScore)
                                onSpeedMultiplierChange(0.1f)
                                pill.copy(visible = false) // Mark pill as invisible
                            } else {
                                pill
                            }
                        }
                    }
                }
        ){
            pills.forEach { pill ->
                // Calculate scaling factor
                val scale = (pill.radius * 2) / pillImage.width.toFloat()
                // Draw the image with the scale applied
                if (pill.visible) {
                    withTransform({
                        scale(scale, scale, Offset(pill.x - pill.radius, pill.y - pill.radius)) // Scaling around the pill's position
                    }) {
                        if (pill.isGood) {
                            drawImage(
                                pillImage,
                                topLeft = Offset(pill.x, pill.y),
                            )
                        } else if (pill.isGood == false) {
                            drawImage(
                                radiationImage,
                                topLeft = Offset(pill.x, pill.y),
                            )
                        }
                    }
                    if(pill.isGood) {
                    //drawCircle(
                        //color = pill.color,
                        //center = Offset(pill.x, pill.y),
                        //radius = pill.radius
                    //)
                    if(pill.isGood == false){
                        drawRect(
                        color = pill.color,
                        topLeft = Offset(pill.x - pill.radius, pill.y - pill.radius), // This will center the rectangle
                        size = Size(pill.radius * 2, pill.radius * 2 * 2)  // Size of the rectangle, equivalent to the circle's diameter
                        )
                    }
                        //if(pill.isGood == false) {
                            //drawRect(
                                //color = pill.color,
                                //center = Offset(pill.x, pill.y),
                                //radius = pill.radius
                           //)

                        //}
                    }
                }
            }
        }
        Column(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 30.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
        ){Text(
            text = "Score: $score",
            color = data.lightColor,
            modifier = Modifier
                .padding(2.dp)
        )

            Text(
                text = "Time Left: $timeLeftInSeconds seconds",
                color = data.lightColor,
                modifier = Modifier
                    .padding(2.dp)
            )}

        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(
                onClick = {
                    onScoreChange(10)
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = data.lightColor,
                    contentColor = data.darkColor
                )
            ) {
                Text("Adds +10 to score", color = data.darkColor)
            }
            Button(
                onClick = {
                    isSkipTimerClicked = true
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = data.lightColor,
                    contentColor = data.darkColor
                )
            ) {
                Text("Skip to Last 5 Seconds", color = data.darkColor)
            }
        }

    }
}

// Helper function to check if a pill is tapped
fun isTapped(offset: Offset, pill: Pills): Boolean {
    val distance = hypot(offset.x - pill.x, offset.y - pill.y)
    return distance <= pill.radius
}


@Composable
fun StartScreen(onStartClick: () -> Unit, data: ActivityData) {
    val context = LocalContext.current
    val sharedPreferences = context.getSharedPreferences("GamePrefs", MODE_PRIVATE)
//    var totalFlasks = sharedPreferences.getInt("antiRadiationFlasks", 0).coerceAtMost(8)
    val cooldownEndTime = sharedPreferences.getLong("cooldownEndTime", 0L)

    val currentTime = System.currentTimeMillis()
    var remainingTime by remember { mutableIntStateOf(((cooldownEndTime - currentTime) / 1000).toInt()) }
    val isInCooldown = playersFlasks >= 8 && remainingTime > 0

    LaunchedEffect(isInCooldown) {
        if (isInCooldown) {
            while (remainingTime > 0) {
                delay(1000L)
                remainingTime -= 1
            }
            if (remainingTime == 0 && playersFlasks == 8)
                playersFlasks = 0
        } else if (cooldownEndTime > 0) {
            sharedPreferences.edit().putInt("antiRadiationFlasks", playersFlasks).apply()
        }
    }


    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(data.darkColor),
    ) {
        // Top-right corner: Flask image and counter.
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(20.dp)
                .offset(x = 15.dp, y = 5.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.flaskimage),
                contentDescription = "Anti-Radiation Flask",
                modifier = Modifier
                    .size(100.dp) // Adjust size as needed.
            )
            Text(
                text = "x$playersFlasks",
                color = data.lightColor,
                fontSize = 16.sp,
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .offset(x = (-5).dp, y = 10.dp) // Adjust for proper placement near the image.
            )
        }

        Column(
            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (isInCooldown) {
                val minutes = (remainingTime / 60).toString().padStart(2, '0')
                val seconds = (remainingTime % 60).toString().padStart(2, '0')

                Text(
                    text = "Wait $minutes:$seconds before playing again.",
                    color = data.lightColor,
                    modifier = Modifier
                        .padding(bottom = 16.dp)
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

            //Text(
                //text = "Total Anti-Radiation Flasks: $playersFlasks",
                //color = data.lightColor,
                //modifier = Modifier.padding(top = 16.dp)
            //)

        }

        if(isInCooldown){
            Button(
                onClick = {
                    val newCooldownTime = System.currentTimeMillis() + 5000L
                    sharedPreferences.edit().putLong("cooldownEndTime", newCooldownTime).apply()
                    remainingTime = 5
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = data.lightColor,
                    contentColor = data.darkColor
                ),
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 32.dp)
            ) {
                Text("Cheats: Skip Cooldown", color = data.darkColor)
            }
        }
        StartScreenAnimation()
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
                .padding(30.dp)
        ) {
            Text("Back", color = data.darkColor)
        }
    }
}

@Composable
fun StartScreenAnimation() {
    val imageFrames = listOf(
        painterResource(id = R.drawable.analysismachine1),
        painterResource(id = R.drawable.analysismachine2),
        painterResource(id = R.drawable.analysismachine3),
    )

    var currentFrame by remember { mutableStateOf(0) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(500L) // Switch frames every 500ms
            currentFrame = (currentFrame + 1) % imageFrames.size // Loop through frames
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()

    ) {
        Image(
            painter = imageFrames[currentFrame],
            contentDescription = "Animated Analysis Machine",
            modifier = Modifier
                .size(200.dp)
                .align(Alignment.Center)
                .offset(y = (-150).dp)
        )
    }
}


@Composable
fun EndScreen(onStartClick: () -> Unit, data: ActivityData, score: Int) {
    val context = LocalContext.current
    val roundFlasks = (score * 0.1).toInt().coerceAtMost(8) //how many flasks did you win depending on how many pills you clicked on (10 pills = 1 flask)

    LaunchedEffect(roundFlasks) {
        val sharedPreferences = context.getSharedPreferences("GamePrefs", MODE_PRIVATE)
        val flasksBeforeRound = sharedPreferences.getInt("antiRadiationFlasks", playersFlasks)

        if (playersFlasks < 8) {
            playersFlasks = flasksBeforeRound + roundFlasks;
        }

        if (playersFlasks >= 8) {
            playersFlasks = 8
        }

        val editor = sharedPreferences.edit()
        editor.putInt("antiRadiationFlasks", playersFlasks)


        if (playersFlasks >= 8) {
            val cooldownEndTime = System.currentTimeMillis() + 3600000L // 1 hour in milliseconds
            editor.putLong("cooldownEndTime", cooldownEndTime)
        }

        editor.apply()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(data.darkColor),
    ) {
        // Flask counter and image in the top-right corner.
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(20.dp)
                .offset(x = 15.dp, y = 5.dp)
        ) {
            // Flask image.
            Image(
                painter = painterResource(id = R.drawable.flaskimage), // Ensure resource exists.
                contentDescription = "Anti-Radiation Flask",
                modifier = Modifier
                    .size(100.dp)
            )

            // Counter positioned relative to the flask image.
            Text(
                text = "x$roundFlasks",
                color = data.lightColor,
                fontSize = 16.sp,
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .offset(x = (-5).dp, y = 10.dp) // Slightly offset for better positioning.
            )
        }

        // Main content (score and message) in the center.
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.align(Alignment.Center)
        ) {
            Text(
                text = "Final Score: $score",
                color = data.lightColor,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Text(
                text = "This round you won $roundFlasks Anti-Radiation Flasks",
                color = data.lightColor,
                modifier = Modifier.padding(bottom = 32.dp)
            )
        }

        // Back button at the bottom center.
        Button(
            onClick = onStartClick,
            colors = ButtonDefaults.buttonColors(
                containerColor = data.lightColor,
                contentColor = data.darkColor
            ),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 16.dp)
        ) {
            Text("Back", color = data.darkColor)
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
                color = Color.White,
                modifier = Modifier
                    .padding(top = 16.dp)
            )
        }
    }
}

@Composable
fun itemRain(){
    val context = LocalContext.current
    Toast.makeText(context, "hi", Toast.LENGTH_SHORT).show()
}

fun generatePills(data: ActivityData, screenWidth: Float, screenHeight: Float): List<Pills> {
    val pills = mutableListOf<Pills>() //list of pills

    val gridSize = 100f  //each cell is 100f
    val gridWidth = (screenWidth / gridSize).toInt()
    val gridHeight = (screenHeight / gridSize).toInt()

    val grid = mutableMapOf<Pair<Int, Int>, MutableList<Pills>>()

    val minDistance = 200f //minimum distance between pills radiuses
    val pillRadius = 50f //

    repeat(1) { //eventually change this to more but bc of performance issues im keeping it at 1 spawn at a time
        var newPill: Pills
        var isPositionValid: Boolean
        var attempts = 0
        val maxAttempts = 50

        do {
            val x = (0..screenWidth.toInt()).random().toFloat() // eventually change this to be radius and screenwidth,, but it seems that i cant use floats with the random,, ask nathan
            val y = (-1000..-500).random().toFloat()

            newPill = Pills(
                x = x,
                y = y,
                radius = pillRadius,
                //speed = 0f,
                color = data.lightColor,
                visible = true,
                isGood = (0..5).random() <= 3, //4 in 6 chance of good pill
                velocityY = 8f //* speedMultiplier
            )

            val gridX = ((x + screenWidth / 2) / gridSize).toInt().coerceIn(0, gridWidth - 1) //places pill into grid, taking into account screen center and cell bounds
            val gridY = ((y + screenHeight / 2) / gridSize).toInt().coerceIn(0, gridHeight - 1)


            //val nearbyCells = listOf(
            //gridX to gridY,
            //(gridX - 1).coerceAtLeast(0) to gridY, //left cell,, doesnt go bellow 0 cell
            // (gridX + 1).coerceAtMost(gridWidth - 1) to gridY, //right cell, doesnt go between most cell
            // gridX to (gridY - 1).coerceAtLeast(0), //up cell
            //  gridX to (gridY + 1).coerceAtMost(gridHeight - 1) //down cell
            // )

            //isPositionValid = nearbyCells.all { (cx, cy) ->
            //grid[cx][cy].all { existingPill ->
            //   val distance = hypot((existingPill.x - newPill.x), (existingPill.y - newPill.y)) //pitagoras to see the distance between pills
            //     distance >= (existingPill.radius + newPill.radius + minDistance) //if distance is over min distance then its a valid pill
            //   }
            // }

            isPositionValid = isPositionValid(newPill, pills, grid, gridSize)


            attempts++
        } while (!isPositionValid && attempts < maxAttempts) //keeps trying until max number of attempts

        if (isPositionValid) { //if the pill is valid add it to the list
            pills.add(newPill)
            updateGrid(newPill, grid, gridSize) // update grid with the new valid pill

            //val gridX = ((newPill.x + screenWidth / 2) / gridSize).toInt() //place new pill into grid
            //val gridY = ((newPill.y + screenHeight / 2) / gridSize).toInt()
            //grid[gridX][gridY].add(newPill)

        }
    }
    return pills
}

fun isPositionValid(pill: Pills, existingPills: List<Pills>, grid: MutableMap<Pair<Int, Int>, MutableList<Pills>>, gridSize: Float): Boolean {
    val gridX = (pill.x / gridSize).toInt() //calculates what cellx it belongs to
    val gridY = (pill.y / gridSize).toInt() //calculates what celly it belongs to

    for (i in -1..1) { //goes through the nearby cells
        for (j in -1..1) {
            val nearbyGrid = grid[Pair(gridX + i, gridY + j)]
            if (nearbyGrid != null) { //if pills are here
                for (otherPill in nearbyGrid) {
                    if (hypot(pill.x - otherPill.x, pill.y - otherPill.y) < pill.radius + otherPill.radius) { //compare distance to see if its bigger than their radiuses added
                        return false //its false if position isnt possible
                    }
                }
            }
        }
    }
    return true //its true if positions are good
}

fun updateGrid(pill: Pills, grid: MutableMap<Pair<Int, Int>, MutableList<Pills>>, gridSize: Float) {
    val gridX = (pill.x / gridSize).toInt() //calculate what cell it is at
    val gridY = (pill.y / gridSize).toInt()
    val key = Pair(gridX, gridY) //key stores (x,y)

    if (grid.containsKey(key)) { //if grid has pills add a pill to to the list, if not create list and place pill
        grid[key]?.add(pill)
    } else {
        grid[key] = mutableListOf(pill)
    }
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
