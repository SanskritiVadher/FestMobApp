package com.zenith26.fest.ui.portal

import android.view.View
import android.webkit.WebView
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.zenith26.fest.ui.theme.*
import kotlinx.coroutines.delay

/**
 * Hard-edged offset sketchbook drop shadow matching CSS: box-shadow: 4px 4px 0 #2B2A28
 */
fun Modifier.sketchShadow(
    offsetX: Dp = 4.dp,
    offsetY: Dp = 4.dp,
    color: Color = InkBorder,
    radius: Dp = 12.dp
) = this.drawBehind {
    drawRoundRect(
        color = color,
        topLeft = Offset(offsetX.toPx(), offsetY.toPx()),
        size = size,
        cornerRadius = CornerRadius(radius.toPx(), radius.toPx())
    )
}

/**
 * Renders an inline SVG character asset cleanly inside Compose.
 */
@Composable
fun SvgCharacter(
    assetName: String,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val svgContent = remember(assetName) {
        try {
            context.assets.open("characters/$assetName").bufferedReader().use { it.readText() }
        } catch (e: Exception) {
            ""
        }
    }

    if (svgContent.isNotEmpty()) {
        AndroidView(
            factory = { ctx ->
                WebView(ctx).apply {
                    setBackgroundColor(android.graphics.Color.TRANSPARENT)
                    setLayerType(View.LAYER_TYPE_SOFTWARE, null)
                    isVerticalScrollBarEnabled = false
                    isHorizontalScrollBarEnabled = false
                    isClickable = false
                    isFocusable = false
                    settings.apply {
                        loadWithOverviewMode = true
                        useWideViewPort = true
                        javaScriptEnabled = false
                    }
                }
            },
            update = { webView ->
                val html = """
                    <!DOCTYPE html>
                    <html>
                    <head>
                    <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no">
                    <style>
                      * { margin:0; padding:0; box-sizing:border-box; }
                      html, body { width:100%; height:100%; background:transparent; overflow:hidden; display:flex; align-items:center; justify-content:center; }
                      svg { width:100%; height:100%; max-height:100%; object-fit:contain; }
                    </style>
                    </head>
                    <body>
                      $svgContent
                    </body>
                    </html>
                """.trimIndent()
                webView.loadDataWithBaseURL("file:///android_asset/", html, "text/html", "UTF-8", null)
            },
            modifier = modifier
        )
    }
}

/**
 * Acrobatic Mascot on the Hero Chalk Track performing cartwheels, somersaults, and cheers.
 */
@Composable
fun AcrobaticHeroTrack(
    modifier: Modifier = Modifier
) {
    var cheerText by remember { mutableStateOf<String?>(null) }
    val cheers = remember {
        listOf(
            "Ta-da! 🤸",
            "Watch this flip! ✨",
            "Cartwheel champion! 🎪",
            "10/10 landing! 🏅",
            "Full fest energy! ⚡"
        )
    }

    LaunchedEffect(cheerText) {
        if (cheerText != null) {
            delay(2200)
            cheerText = null
        }
    }

    // Infinite routine: cartwheels, somersault tuck leap, and victory bounce
    val infiniteTransition = rememberInfiniteTransition(label = "acrobatRoutine")

    // Progress 0f -> 1f over 10 seconds
    val progress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(10000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "tumbleProgress"
    )

    // Compute translation, rotation, and elevation based on progress phases
    val (transX, transY, rotation, scaleX) = remember(progress) {
        when {
            // Phase 1 (0..0.3): Cartwheel forward across track (-40dp to +40dp)
            progress < 0.3f -> {
                val p = progress / 0.3f
                val x = -45f + (p * 90f)
                val rot = p * 360f
                val y = -kotlin.math.sin(p * Math.PI.toFloat() * 2f).coerceAtLeast(0f) * 8f
                listOf(x, y, rot, 1f)
            }
            // Phase 2 (0.3..0.4): Crouch prep & spring launch
            progress < 0.4f -> {
                val p = (progress - 0.3f) / 0.1f
                val crouch = if (p < 0.3f) 4f else -14f * p
                listOf(45f, crouch, 0f, 1f)
            }
            // Phase 3 (0.4..0.65): High Somersault flip (720° tuck rotation in the air!)
            progress < 0.65f -> {
                val p = (progress - 0.4f) / 0.25f
                val x = 45f - (p * 30f)
                val y = -kotlin.math.sin(p * Math.PI.toFloat()) * 26f
                val rot = p * 720f
                listOf(x, y, rot, 1f)
            }
            // Phase 4 (0.65..0.75): Ta-da! Triumph celebration bounce
            progress < 0.75f -> {
                val p = (progress - 0.65f) / 0.1f
                val bounce = kotlin.math.sin(p * Math.PI.toFloat() * 3f) * -5f
                listOf(15f, bounce, 0f, 1f)
            }
            // Phase 5 (0.75..1.0): Cartwheel reverse back to starting mark
            else -> {
                val p = (progress - 0.75f) / 0.25f
                val x = 15f - (p * 60f)
                val rot = -p * 360f
                val y = -kotlin.math.sin(p * Math.PI.toFloat() * 2f).coerceAtLeast(0f) * 8f
                listOf(x, y, rot, -1f) // flipped direction
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(58.dp),
        contentAlignment = Alignment.Center
    ) {
        // Dashed Chalk Track
        Box(
            modifier = Modifier
                .width(220.dp)
                .height(2.dp)
                .background(TomatoPrimary.copy(alpha = 0.35f))
        )

        // Acrobatic Mascot container
        Box(
            modifier = Modifier
                .offset(x = transX.dp, y = transY.dp)
                .graphicsLayer {
                    rotationZ = rotation
                    this.scaleX = scaleX
                }
                .size(46.dp)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) {
                    cheerText = cheers.random()
                },
            contentAlignment = Alignment.Center
        ) {
            SvgCharacter(
                assetName = "character7.svg",
                modifier = Modifier.fillMaxSize()
            )
        }

        // Comic speech bubble when tapped
        AnimatedVisibility(
            visible = cheerText != null,
            enter = fadeIn(tween(150)),
            exit = fadeOut(tween(200)),
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset(y = (-22).dp)
        ) {
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = PaperCard,
                border = androidx.compose.foundation.BorderStroke(1.5.dp, InkBorder),
                modifier = Modifier.sketchShadow(offsetX = 2.dp, offsetY = 2.dp, radius = 8.dp)
            ) {
                Text(
                    text = cheerText ?: "",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = InkPrimary,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                )
            }
        }
    }
}

/**
 * Chubby Penguin on a Skateboard gliding across the footer track on a loop.
 */
@Composable
fun SkatingPenguinFooter(
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "skaterLoop")
    val offsetX by infiniteTransition.animateFloat(
        initialValue = -120f,
        targetValue = 120f,
        animationSpec = infiniteRepeatable(
            animation = tween(8500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "skaterX"
    )

    val wobble by infiniteTransition.animateFloat(
        initialValue = -2.5f,
        targetValue = 2.5f,
        animationSpec = infiniteRepeatable(
            animation = tween(400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "skaterWobble"
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Skater on Chalk Track
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            contentAlignment = Alignment.Center
        ) {
            // Track line
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .height(2.dp)
                    .background(InkLight)
            )

            // Penguin skater
            Box(
                modifier = Modifier
                    .offset(x = offsetX.dp, y = (-12).dp)
                    .graphicsLayer { rotationZ = wobble }
                    .size(46.dp)
            ) {
                SvgCharacter(
                    assetName = "character4.svg",
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Sketchbook Footer Text
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = PaperCard,
            border = androidx.compose.foundation.BorderStroke(1.5.dp, InkBorder),
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .sketchShadow(offsetX = 3.dp, offsetY = 3.dp, radius = 12.dp)
        ) {
            Text(
                "© 2026 Zenith Fest Management System · Hand-Drawn Sketchbook Edition",
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = InkMuted,
                modifier = Modifier.padding(12.dp),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}
