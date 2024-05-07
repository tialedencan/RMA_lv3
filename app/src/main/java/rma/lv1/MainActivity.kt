package rma.lv1

import android.content.ContentValues.TAG
import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import rma.lv1.ui.theme.LV1Theme
import java.security.AllPermission
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.util.Log
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.platform.LocalView
import kotlin.math.sqrt

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {

            LV1Theme {
                // A surface container using the 'background' color from the theme

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background

                ) {
                    //BackgroundImage(modifier = Modifier)
                    val navController = rememberNavController()
                    NavHost(navController = navController, startDestination = "main_screen") {
                        composable("main_screen") {
                            MainScreen(navController = navController)
                        }
                        composable("step_counter") {
                            StepCounter(navController = navController)
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun BackgroundImage(modifier: Modifier) {

    Box (modifier){ Image(
        painter = painterResource(id = R.drawable.fitness),
        contentDescription = null,
        contentScale = ContentScale.Crop,
        alpha = 0.1F
    )

    }

}

@Composable
fun MainScreen(navController: NavController) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        BackgroundImage(modifier = Modifier.fillMaxSize())
        UserPreview()
        // Button to navigate to StepCounter
        Button(
            onClick = {
                // Navigate to OtherScreen when button clicked
                navController.navigate("step_counter")
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            Text(text = "Step Counter")
        }
    }
}

@Composable
fun UserPreview() {
    val name = "Tia"
    val visina = 1.91f
    val tezina = 1000f
    val bmi = tezina / (visina * visina)
    val formattedBmi = String.format("%.2f", bmi)
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(16.dp)
    ) {
        Text(
            text = "Pozdrav $name!",
            fontSize = 20.sp,
            lineHeight = 56.sp,
            modifier = Modifier.padding(top = 8.dp)
        )
        Text(
            text = "Tvoj BMI je:",
            fontSize = 55.sp,
            lineHeight = 61.sp,
            textAlign = TextAlign.Center,
        )
        Text(
            text = formattedBmi,
            fontSize = 70.sp,
            lineHeight = 72.sp,
            fontWeight = FontWeight.Bold,
        )
    }
}
@Composable
fun StepCounter(navController: NavController) {

    val db = Firebase.firestore

    val sensorManager = (LocalContext.current.getSystemService(Context.SENSOR_SERVICE) as SensorManager)
    val sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

    var x by remember { mutableStateOf(0f) }
    var y by remember { mutableStateOf(0f) }
    var z by remember { mutableStateOf(0f) }

    val sensorEventListener = remember {
        object : SensorEventListener{
            override fun onAccuracyChanged(p0: Sensor?, p1: Int) {}
            override fun onSensorChanged(event: SensorEvent?) {
                event?.let {
                    if(it.sensor.type == Sensor.TYPE_ACCELEROMETER) {
                        x = it.values[0]
                        y = it.values[1]
                        z = it.values[2]
                    }
                }
            }
        }
    }
    val lifecycleOwner = LocalLifecycleOwner.current
    val view = LocalView.current

    DisposableEffect(key1 = sensorManager, key2 = lifecycleOwner) {
        sensorManager.registerListener(sensorEventListener, sensor, SensorManager.SENSOR_DELAY_NORMAL)
        onDispose {
            sensorManager.unregisterListener(sensorEventListener)
        }
    }

    var treshold = sqrt(x*x + y*y + z*z)
    var counter by remember { mutableStateOf(0) }
    if(treshold > 13){
        counter++
    }
    val docRef = db.collection("BMI").document("pKhPETZOXce1aATvCSGY") //
    docRef.update("Koraci", counter)
        .addOnSuccessListener {
            // Update successful (optional: show a success message)
        }
        .addOnFailureListener { e ->
            // Update failed (handle error, e.g., show an error message)
            Log.e("MainActivity", "Error updating Koraci: $e")
        }

    Box(modifier = Modifier.fillMaxSize()) {
        BackgroundImage(modifier = Modifier.fillMaxSize())
        Column {
            Text(
                text = counter.toString(),
                fontSize = 20.sp
            )
        }
        // Back button
        Button(
            onClick = { navController.popBackStack() },
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(16.dp)
        ) {
            Text("User Info")
        }
    }
}
/*
@Preview(showBackground = false)
@Composable
fun UserPreview() {
    LV1Theme {
        BackgroundImage(modifier = Modifier)   }
}*/