package com.example.generictemplate

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.generictemplate.ui.theme.GenericTemplateTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            GenericTemplateTheme {
                LemonadeApp()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LemonadeApp() {
    // Tracks which of the 4 stages (1..4) the user is on
    var currentStep by remember { mutableStateOf(1) }
    // Random squeeze count for step 2
    var squeezeCount by remember { mutableStateOf(0) }
    // Whether the current step is completed by tapping the image
    var stepComplete by remember { mutableStateOf(false) }

    // Move to a given step, resetting stepComplete (and randomizing squeezeCount if step 2)
    fun goToStep(step: Int) {
        currentStep = step
        stepComplete = false
        if (step == 2) {
            squeezeCount = (2..4).random()
        }
    }

    // Decide text, image, and how image-taps behave based on the current step
    val textLabelResourceId: Int
    val drawableResourceId: Int
    val contentDescriptionResourceId: Int
    val onImageClick: () -> Unit

    when (currentStep) {
        1 -> {
            textLabelResourceId = R.string.lemon_select
            drawableResourceId = R.drawable.lemon_tree
            contentDescriptionResourceId = R.string.lemon_tree_content_description
            onImageClick = {
                // Step 1 requires just 1 tap
                stepComplete = true
            }
        }
        2 -> {
            textLabelResourceId = R.string.lemon_squeeze
            drawableResourceId = R.drawable.lemon_squeeze
            contentDescriptionResourceId = R.string.lemon_content_description
            onImageClick = {
                // Step 2 requires multiple squeezes
                squeezeCount--
                if (squeezeCount <= 0) {
                    stepComplete = true
                }
            }
        }
        3 -> {
            textLabelResourceId = R.string.lemon_drink
            drawableResourceId = R.drawable.lemon_drink
            contentDescriptionResourceId = R.string.lemonade_content_description
            onImageClick = {
                // One tap finishes step 3
                stepComplete = true
            }
        }
        else -> {
            textLabelResourceId = R.string.lemon_empty_glass
            drawableResourceId = R.drawable.lemon_restart
            contentDescriptionResourceId = R.string.empty_glass_content_description
            onImageClick = {
                // One tap finishes step 4
                stepComplete = true
            }
        }
    }

    // Decide label for the button
    val buttonLabel = when (currentStep) {
        1, 2, 3 -> "Next"
        else -> "Restart"
    }

    // The Next/Restart button logic
    val onNextClick: () -> Unit = {
        when (currentStep) {
            1 -> goToStep(2)
            2 -> goToStep(3)
            3 -> goToStep(4)
            4 -> goToStep(1)
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Lemonade", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.largeTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    ) { innerPadding ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.tertiaryContainer),
            color = MaterialTheme.colorScheme.background
        ) {
            // Single composable that shows image, text, AND the next button
            LemonTextAndImage(
                textLabelResourceId = textLabelResourceId,
                drawableResourceId = drawableResourceId,
                contentDescriptionResourceId = contentDescriptionResourceId,
                onImageClick = onImageClick,
                stepComplete = stepComplete,
                onNextClick = onNextClick,
                buttonLabel = buttonLabel
            )
        }
    }
}

/**
 * A single composable that:
 * 1) Shows the lemon image in a button (clickable).
 * 2) Displays the text label below it.
 * 3) Shows the Next/Restart button, which is enabled only if stepComplete = true.
 */
@Composable
fun LemonTextAndImage(
    textLabelResourceId: Int,
    drawableResourceId: Int,
    contentDescriptionResourceId: Int,
    onImageClick: () -> Unit,
    stepComplete: Boolean,
    onNextClick: () -> Unit,
    buttonLabel: String,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxSize()) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            // Image (clickable via onClick)
            Button(
                onClick = onImageClick,
                shape = RoundedCornerShape(dimensionResource(R.dimen.button_corner_radius)),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.tertiaryContainer
                )
            ) {
                Image(
                    painter = painterResource(drawableResourceId),
                    contentDescription = stringResource(contentDescriptionResourceId),
                    modifier = Modifier
                        .size(100.dp)
                        .padding(16.dp)
                )
            }

            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.padding_vertical)))

            // Label text
            Text(
                text = stringResource(textLabelResourceId),
                style = MaterialTheme.typography.bodyLarge
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Next/Restart button (enabled only if stepComplete is true)
            Button(
                onClick = onNextClick,
                enabled = stepComplete,
                shape = RoundedCornerShape(dimensionResource(R.dimen.button_corner_radius)),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text(buttonLabel)
            }
        }
    }
}

@Preview
@Composable
fun LemonPreview() {
    GenericTemplateTheme {
        LemonadeApp()
    }
}
