package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Invoice Flex Official Brand Colors
val IfNavyDark = Color(0xFF0A2540)
val IfBluePrimary = Color(0xFF0066FF)
val IfBlueLight = Color(0xFF00A3FF)
val IfBlueCyan = Color(0xFF38BDF8)
val IfSurfaceWhite = Color(0xFFFFFFFF)

@Composable
fun InvoiceFlexLogo(
    modifier: Modifier = Modifier,
    size: Dp = 48.dp,
    showText: Boolean = false,
    subtitle: String? = null
) {
    if (!showText) {
        InvoiceFlexIcon(modifier = modifier.size(size))
    } else {
        Row(
            modifier = modifier,
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            InvoiceFlexIcon(modifier = Modifier.size(size))
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Invoice",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = IfNavyDark,
                        fontSize = (size.value * 0.42f).sp
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Flex",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = IfBluePrimary,
                        fontSize = (size.value * 0.42f).sp
                    )
                }
                if (subtitle != null) {
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFF64748B),
                        fontSize = (size.value * 0.22f).sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
fun InvoiceFlexIcon(modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(22),
        color = IfSurfaceWhite,
        shadowElevation = 2.dp
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            // Background subtle document sheet card behind the 'F'
            val docLeft = w * 0.36f
            val docTop = h * 0.42f
            val docRight = w * 0.88f
            val docBottom = h * 0.90f
            val docCorner = w * 0.08f

            // Document sheet border & fill
            val docPath = Path().apply {
                moveTo(docLeft + docCorner, docTop)
                lineTo(docRight - docCorner, docTop)
                quadraticTo(docRight, docTop, docRight, docTop + docCorner)
                lineTo(docRight, docBottom - docCorner)
                quadraticTo(docRight, docBottom, docRight - docCorner, docBottom)
                lineTo(docLeft + docCorner, docBottom)
                quadraticTo(docLeft, docBottom, docLeft, docBottom - docCorner)
                close()
            }

            // Draw document card
            drawPath(
                path = docPath,
                color = Color(0xFFF1F5F9),
                style = Fill
            )
            drawPath(
                path = docPath,
                color = IfNavyDark,
                style = Stroke(width = w * 0.055f, cap = StrokeCap.Round)
            )

            // Horizontal document text lines inside the card
            val line1Y = h * 0.65f
            val line2Y = h * 0.77f
            val lineStartX = w * 0.54f
            val lineEndX = w * 0.76f

            drawLine(
                color = IfBluePrimary,
                start = Offset(lineStartX, line1Y),
                end = Offset(lineEndX, line1Y),
                strokeWidth = w * 0.05f,
                cap = StrokeCap.Round
            )

            drawLine(
                color = IfBluePrimary,
                start = Offset(lineStartX, line2Y),
                end = Offset(lineEndX * 0.95f, line2Y),
                strokeWidth = w * 0.05f,
                cap = StrokeCap.Round
            )

            // Letter 'i' (Stem + Circle Dot)
            // Dot
            drawCircle(
                brush = Brush.linearGradient(
                    colors = listOf(IfBlueLight, IfBluePrimary),
                    start = Offset(w * 0.2f, h * 0.15f),
                    end = Offset(w * 0.38f, h * 0.35f)
                ),
                radius = w * 0.10f,
                center = Offset(w * 0.29f, h * 0.24f)
            )

            // 'i' Stem with dark-navy gradient
            val iStemPath = Path().apply {
                moveTo(w * 0.22f, h * 0.40f)
                cubicTo(w * 0.25f, h * 0.38f, w * 0.33f, h * 0.38f, w * 0.36f, h * 0.40f)
                lineTo(w * 0.33f, h * 0.82f)
                cubicTo(w * 0.33f, h * 0.88f, w * 0.25f, h * 0.90f, w * 0.18f, h * 0.88f)
                cubicTo(w * 0.16f, h * 0.82f, w * 0.20f, h * 0.45f, w * 0.22f, h * 0.40f)
                close()
            }
            drawPath(
                path = iStemPath,
                brush = Brush.verticalGradient(
                    colors = listOf(IfBluePrimary, IfNavyDark),
                    startY = h * 0.4f,
                    endY = h * 0.9f
                )
            )

            // Stylized 'F' with Folded Corner
            val fPath = Path().apply {
                // Top curved wing of 'F'
                moveTo(w * 0.42f, h * 0.40f)
                cubicTo(w * 0.44f, h * 0.24f, w * 0.58f, h * 0.21f, w * 0.85f, h * 0.22f)
                cubicTo(w * 0.88f, h * 0.27f, w * 0.82f, h * 0.37f, w * 0.72f, h * 0.44f)
                // Middle bar
                lineTo(w * 0.52f, h * 0.44f)
                lineTo(w * 0.48f, h * 0.75f)
                // Fold bottom corner
                lineTo(w * 0.37f, h * 0.84f)
                lineTo(w * 0.39f, h * 0.40f)
                close()
            }

            drawPath(
                path = fPath,
                brush = Brush.linearGradient(
                    colors = listOf(IfBlueLight, IfBluePrimary),
                    start = Offset(w * 0.35f, h * 0.2f),
                    end = Offset(w * 0.85f, h * 0.8f)
                )
            )

            // Fold accent flap on the lower stem of 'F'
            val foldPath = Path().apply {
                moveTo(w * 0.37f, h * 0.84f)
                lineTo(w * 0.48f, h * 0.75f)
                lineTo(w * 0.46f, h * 0.65f)
                close()
            }
            drawPath(
                path = foldPath,
                brush = Brush.linearGradient(
                    colors = listOf(IfBlueCyan, IfBluePrimary),
                    start = Offset(w * 0.35f, h * 0.65f),
                    end = Offset(w * 0.5f, h * 0.85f)
                )
            )
        }
    }
}

@Composable
fun InvoiceFlexHeroBadge(
    modifier: Modifier = Modifier,
    companyName: String = "Apex Stainless & Engineering",
    gstin: String = "27AAXCS8910M1Z8"
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(18.dp),
        color = IfNavyDark,
        shadowElevation = 3.dp
    ) {
        Row(
            modifier = Modifier
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(IfNavyDark, Color(0xFF0F3A66))
                    )
                )
                .fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .background(Color.White.copy(alpha = 0.12f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                InvoiceFlexIcon(modifier = Modifier.size(38.dp))
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Invoice Flex",
                        style = MaterialTheme.typography.labelMedium,
                        color = IfBlueLight,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = Color(0xFF10B981).copy(alpha = 0.25f)
                    ) {
                        Text(
                            text = "GST VERIFIED",
                            color = Color(0xFF34D399),
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.background(Color.Transparent)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = companyName,
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1
                )
                Text(
                    text = "GSTIN: $gstin",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF94A3B8),
                    fontSize = 11.sp
                )
            }
        }
    }
}
