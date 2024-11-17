package pt.iade.games.companionapp

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import pt.iade.games.companionapp.ui.data.ActivityData
import pt.iade.games.companionapp.ui.theme.CompanionAppTheme

class MetalDetectorActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CompanionAppTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    innerPadding
                    val data = intent.getSerializableExtra("DATA", ActivityData::class.java)!!
                    MetalDetectorScreenManager(data)
                }
            }
        }
    }
}

@Composable
fun MetalDetectorScreenManager(
    data: ActivityData
) {
    var initiating by remember { mutableStateOf(false)}
    var scaning by remember { mutableStateOf(false)}
    var treasureFound by remember { mutableStateOf(false)}

    if (initiating) {
        scaning = true
    } else{
        MetalDetectorStartScreen(onStartClick = {
            initiating = true
        }, data = data)
    }

    if(scaning) {
        MetalDetectorScanningScreen(onTreasureFound = {
            initiating = false
            treasureFound = true
        }, data = data)
    }

    if (treasureFound) {
        MetalDetectorTreasureFoundScreen(data = data)
    }
}


@Composable
fun MetalDetectorStartScreen(onStartClick: () -> Unit, data: ActivityData) {
    val context = LocalContext.current
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(data.darkColor)
            .padding(0.dp, 25.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        Button(
            onClick = onStartClick,
            colors = ButtonDefaults.buttonColors(
                containerColor = data.lightColor,
                contentColor = data.darkColor,
                )
        ) {
            Text("Start Game", color = data.darkColor)
        }
    }

    Box (
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(R.drawable.metal_detector_image),
            contentDescription = null,
            alignment = Alignment.Center
        )

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
fun MetalDetectorScanningScreen(onTreasureFound: () -> Unit, data: ActivityData){
    var timeLeftInSeconds by remember { mutableStateOf(5) }
    var isDoneScanning by remember { mutableStateOf(false) }

    LaunchedEffect(timeLeftInSeconds) {
        while (timeLeftInSeconds > 0) {
            isDoneScanning = false
            delay(1000L)
            timeLeftInSeconds -= 1
        }
        isDoneScanning = true
        onTreasureFound()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(data.darkColor)
            .padding(0.dp, 25.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Scanning... $timeLeftInSeconds",
            color = data.lightColor,
            fontSize = 40.sp
            )
    }
}

@Composable
fun MetalDetectorTreasureFoundScreen(data: ActivityData) {
    val context = LocalContext.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(data.darkColor)
            .padding(0.dp, 25.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Treasure Found!",
            color = data.lightColor,
            fontSize = 40.sp
        )

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

@Preview(showBackground = true)
@Composable
fun MetalDetectorPreview() {
}