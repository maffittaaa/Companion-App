package pt.iade.games.companionapp

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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
                    val dataJson = intent.getStringExtra("DATA")
                    AnalysisMachine(dataJson)
                }
            }
        }
    }
}

@Composable
fun AnalysisMachine(
    dataJson: String?
) {
    val data = Gson().fromJson(dataJson, ActivityData::class.java)
    val backgroundColor = Color(34, 37, 58, 255)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
    ){

    }

}

@Preview(showBackground = true)
@Composable
fun AnalysisMachinePreview() {

}