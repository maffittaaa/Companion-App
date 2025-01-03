package pt.iade.games.companionapp

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pt.iade.games.companionapp.controllers.ConnectToUnity
import pt.iade.games.companionapp.ui.components.LogoPreview
import pt.iade.games.companionapp.ui.components.OptionCard
import pt.iade.games.companionapp.ui.data.ActivityData
import pt.iade.games.companionapp.ui.theme.CompanionAppTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CompanionAppTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                innerPadding
                    val appConnected = intent.getBooleanExtra("CONNECTED", false)
                    AppConnectedCheck(appConnected)
                }
            }
        }
    }

    @Composable
    fun MainView() {
        val context = LocalContext.current
        var size by remember { mutableStateOf(IntSize.Zero) }
        Box(
            Modifier
                .background(Color.Black)
        ){
            Column(
                verticalArrangement = Arrangement.Bottom,
                horizontalAlignment = Alignment.CenterHorizontally,
            ){
                LogoPreview()
                Column (
                    modifier = Modifier.fillMaxSize()
                        .onSizeChanged {
                            size = it
                        },
                    verticalArrangement = Arrangement.Bottom,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ){
                    with(LocalDensity.current) {
                        val modifier = Modifier.size(
                            width = size.width.toDp() * 7f/10,
                            height = size.width.toDp() * 8f/10
                        )
                        OptionCard(
                            name = "Analysis Machine",
                            modifier = modifier,
                            data = ActivityData(
                                lightColor = Color(100, 110, 170, 255),
                                darkColor = Color(34, 37, 58, 255)
                            ),
                            intent = Intent(context, AnalysisMachineActivity::class.java)
                        )
                        OptionCard(
                            name = "Metal Detector",
                            modifier = modifier,
                            data = ActivityData(
                                lightColor = Color(170, 110, 100, 255),
                                darkColor = Color(51, 30, 30, 255)
                            ),
                            intent = Intent(context, MetalDetectorActivity::class.java)
                        )
                    }
                }
            }
        }
    }

    @Composable
    fun AppConnectedCheck(
        connected: Boolean
    ){
        var code by remember { mutableStateOf("") }
        var appConnected by remember { mutableStateOf(connected) }

        if (appConnected) {
            MainView()
        } else {
            code = ConnectToUnityView({ConnectToUnity().ConnectWithCode(
                code.toInt()) { appConnected = true }
            })
        }
    }

    @Composable
    fun ConnectToUnityView(onConnected: () -> Unit) : String{
        var size by remember { mutableStateOf(IntSize.Zero) }
        var code by remember { mutableStateOf("") }
        Box(
            Modifier
                .background(Color.Black)
        ){
            Column(
                verticalArrangement = Arrangement.Bottom,
                horizontalAlignment = Alignment.CenterHorizontally,
            ){
                LogoPreview()
                Column (
                    modifier = Modifier.fillMaxSize()
                        .onSizeChanged {
                            size = it
                        },
                    verticalArrangement = Arrangement.Top,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ){
                    Text(
                        modifier = Modifier.padding(50.dp, 25.dp),
                        color = Color.White,
                        textAlign = TextAlign.Justify,
                        text = "To use the app, enter the code generated on your game in the box below and hit CONNECT."
                    )
                    OutlinedTextField(
                        modifier = Modifier.padding(top = 25.dp),
                        value = code,
                        onValueChange = { code = it },
                        label = { Text("Enter Code") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                    ConnectButton(onConnected)
                }
            }
        }
        return code
    }

    @Composable
    fun ConnectButton(onConnected: () -> Unit){
        Card(
            onClick = onConnected,
            modifier = Modifier
                .padding(25.dp)
                .size(150.dp, 40.dp)
        ){
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ){
                Text(
                    text = "CONNECT",
                    fontSize = 20.sp
                )
            }
        }
    }

    @Preview(showBackground = true)
    @Composable
    fun GreetingPreview() {
        CompanionAppTheme {
            ConnectToUnityView({})
        }
    }

}

