package com.example.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R

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
                        fontSize = (size.value * 0.44f).sp
                    )
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(
                        text = "Flex",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = IfBluePrimary,
                        fontSize = (size.value * 0.44f).sp
                    )
                }
                if (subtitle != null) {
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFF64748B),
                        fontSize = (size.value * 0.22f).coerceAtLeast(9f).sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
fun InvoiceFlexIcon(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(20)
) {
    Surface(
        modifier = modifier
            .shadow(elevation = 2.dp, shape = shape, spotColor = Color(0xFF0066FF).copy(alpha = 0.15f)),
        shape = shape,
        color = Color.White
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(shape),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo_invoice_flex),
                contentDescription = "Invoice Flex Logo",
                contentScale = ContentScale.Fit,
                modifier = Modifier.fillMaxSize()
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
                .fillMaxSize()
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .background(Color.White, CircleShape)
                    .padding(3.dp),
                contentAlignment = Alignment.Center
            ) {
                InvoiceFlexIcon(
                    modifier = Modifier.size(44.dp),
                    shape = CircleShape
                )
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
                            modifier = Modifier
                                .background(Color.Transparent)
                                .padding(horizontal = 5.dp, vertical = 2.dp)
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
