package mx.tec.rgparedes.voicecolor

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import mx.tec.rgparedes.voicecolor.ui.theme.VoiceColorTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            VoiceColorTheme {
                VoiceColorScreen()
            }
        }
    }
}

@Composable
fun VoiceColorScreen() {
    var backgroundColor by remember { mutableStateOf(Color.White) }
    var text by remember { mutableStateOf("") }
    var status by remember { mutableStateOf("Tap Listen and say a color") }
    var isListening by remember { mutableStateOf(false) }

    val context = LocalContext.current
    var tts by remember { mutableStateOf<TextToSpeech?>(null)}

    DisposableEffect(key = context){
        var engine: TextToSpeech(context) {result ->
            if (result == TextToSpeech.SUCCESS) {
                engine?.language = Locale.US
                tts = engine
            }
        }
        onDispose {
            engine?.stop()
            engine?.shutdown()
        }

    }
}

    val colorMap = mapOf(
        "red" to Color.Red,
        "green" to Color.Green,
        "blue" to Color.Blue,
        "yellow" to Color.Yellow,
        "black" to Color.Black,
        "white" to Color.White,
        "gray" to Color.Gray,
        "cyan" to Color.Cyan,
        "magenta" to Color.Magenta
    )

    fun applyColor(spoken: String) {
        val match = colorMap.entries.firstOrNull { spoken.lowercase().contains(it.key) }
        if (match != null) {
            backgroundColor = match.value
            status = "Color: ${match.key}"
        } else {
            status = "Didn't understand \"$spoken\""
        }
    }

    val recognizer = remember {SpeechRecognizer.createSpeechRecognizer(context)}
    DisposableEffect(recognizer){
        recognizer.setRecognitionListener(object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) {
                isListening = true
                val spoken = results
                    ?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                    ?.firstOrNULL()
                if (spoken != null) { applyColor(spoken) }
            }
        })
    }

    Column(
        modifier = Modifier.fillMaxSize().background(backgroundColor).padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card { Text(status, Modifier.padding(12.dp)) }

        Button(
            onClick = {
               /* Start listening */
            }
        ) {
            Text(if (isListening) "Listening…" else "Listen")
        }

        Card {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = text,
                    onValueChange = { text = it },
                    label = { Text("Type something to hear") }
                )
                Button(
                    onClick = {
                        /* Speak text */
                        if(text.isNotBlank()) {
                            tts?.speak(text, TextToSpeech.QUEUE_FLASH, null, "utterance-1")
                        }
                    }
                    , modifier = Modifier.fillMaxWidth()) {
                    Text("Speak")
                }
            }
        }
    }
}