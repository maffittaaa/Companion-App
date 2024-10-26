package pt.iade.games.companionapp

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntSize
import pt.iade.games.companionapp.ui.components.Logo
import pt.iade.games.companionapp.ui.components.LogoPreview
import pt.iade.games.companionapp.ui.components.OptionCard
import pt.iade.games.companionapp.ui.theme.CompanionAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CompanionAppTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                innerPadding
                    Greeting()
                }
            }
        }
    }
}

@Composable
fun Greeting() {
    val context = LocalContext.current
    var size by remember { mutableStateOf(IntSize.Zero) }

    Column (
        modifier = Modifier.fillMaxSize()
            .onSizeChanged {
                size = it
            },
        verticalArrangement = Arrangement.Bottom,
        horizontalAlignment = Alignment.CenterHorizontally
    ){

        with(LocalDensity.current) {
            val modifier = Modifier.size(
                width = size.width.toDp() * 9/10,
                height = size.width.toDp() * 9/10
            )
            OptionCard(
                name = "Analysis Machine",
                modifier = modifier,
                onClick = {
                    Toast.makeText(context, "Analysis Machine", Toast.LENGTH_LONG).show()
                    val intent = Intent(context, AnalysisMachineActivity::class.java)
                    context.startActivity(intent)
                }
            )
            OptionCard(
                name = "Metal detector",
                modifier = modifier,
                onClick = {
                    Toast.makeText(context, "Metal Detector", Toast.LENGTH_LONG).show()
                    val intent = Intent(context, MetalDetectorActivity::class.java)
                    context.startActivity(intent)
                }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    CompanionAppTheme {
        Greeting()
    }
}