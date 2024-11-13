package pt.iade.games.companionapp.ui.components

import android.content.Intent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.gson.Gson
import pt.iade.games.companionapp.ui.data.ActivityData

@Composable
fun OptionCard(
    modifier: Modifier = Modifier,
    name: String = "option",
    data: ActivityData,
    intent: Intent
){
    val context = LocalContext.current
    val dataJson = Gson().toJson(data)
    Card(
        onClick = {
            context.startActivity(
                intent.apply
                {
                    putExtra("DATA", dataJson)
                }
            )
        },
        modifier = modifier
            .padding(bottom = 50.dp),
        colors = CardDefaults.cardColors(
            containerColor = data.lightColor,
        ),
    ){
        Column (
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ){
            Text(
                text = name,
                fontSize = 25.sp,
                textAlign = TextAlign.Center,
                color = data.darkColor,
                lineHeight = 50.sp
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun OptionCardPreview(){
    //OptionCard(name = "Very long name thing")
}