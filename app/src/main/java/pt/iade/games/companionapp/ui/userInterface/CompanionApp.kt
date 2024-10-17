package pt.iade.games.companionapp.ui.userInterface

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp

@Preview(showBackground = true)
@Composable
fun UICompanionApp()
{
    Column (
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Text(
            text = "The Rumble",
            fontSize = 20.sp
        )
    }
}