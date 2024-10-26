package pt.iade.games.companionapp.ui.components

import android.graphics.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private infix fun Unit.clickable(function: () -> Unit) {

}

@Composable
fun OptionCard(
    modifier: Modifier = Modifier,
    name: String = "option",
    onClick: () -> Unit = {}
){
    val configuration = LocalConfiguration.current

    Card(
        modifier = modifier
            .padding(20.dp)
            .clickable { onClick() }
    ){
        Column (
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ){
            Text(
                text = name,
                fontSize = 40.sp,
                textAlign = TextAlign.Center,
                lineHeight = 50.sp
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun OptionCardPreview(){
    OptionCard(name = "Very long name thing")
}