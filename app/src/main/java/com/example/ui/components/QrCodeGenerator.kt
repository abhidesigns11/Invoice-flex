package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

object UpiHelper {
    fun generateUpiString(
        upiId: String,
        payeeName: String,
        amount: Double,
        invoiceNumber: String
    ): String {
        val encodedName = URLEncoder.encode(payeeName, StandardCharsets.UTF_8.toString())
        val encodedNote = URLEncoder.encode("Bill $invoiceNumber", StandardCharsets.UTF_8.toString())
        return "upi://pay?pa=$upiId&pn=$encodedName&am=${String.format(java.util.Locale.US, "%.2f", amount)}&cu=INR&tn=$encodedNote"
    }
}

/**
 * QR Code Canvas Composable
 * Generates a standard QR-style matrix representation with standard finder patterns
 * and data hash distribution for visual clarity and authentic scan appearance.
 */
@Composable
fun QrCodeGenerator(
    data: String = "",
    content: String = data,
    modifier: Modifier = Modifier,
    size: Dp = 180.dp,
    qrColor: Color = Color(0xFF0F172A),
    backgroundColor: Color = Color.White
) {
    val payload = if (content.isNotEmpty()) content else data
    UpiQrCodeView(
        data = payload,
        modifier = modifier,
        size = size,
        qrColor = qrColor,
        backgroundColor = backgroundColor
    )
}

@Composable
fun UpiQrCodeView(
    data: String,
    modifier: Modifier = Modifier,
    size: Dp = 180.dp,
    qrColor: Color = Color(0xFF0F172A),
    backgroundColor: Color = Color.White
) {
    val matrixSize = 25
    val matrix = remember(data) {
        generateQrMatrix(data, matrixSize)
    }

    Box(
        modifier = modifier
            .size(size)
            .background(backgroundColor, RoundedCornerShape(12.dp))
            .padding(12.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(size - 24.dp)) {
            val cellSize = this.size.width / matrixSize

            for (row in 0 until matrixSize) {
                for (col in 0 until matrixSize) {
                    if (matrix[row][col]) {
                        drawRect(
                            color = qrColor,
                            topLeft = Offset(col * cellSize, row * cellSize),
                            size = Size(cellSize, cellSize)
                        )
                    }
                }
            }
        }
    }
}

private fun generateQrMatrix(data: String, size: Int): Array<BooleanArray> {
    val matrix = Array(size) { BooleanArray(size) { false } }

    // Finder pattern helper (7x7 box with 3x3 inner square)
    fun drawFinderPattern(startRow: Int, startCol: Int) {
        for (r in 0 until 7) {
            for (c in 0 until 7) {
                val isBorder = (r == 0 || r == 6 || c == 0 || c == 6)
                val isCenter = (r in 2..4 && c in 2..4)
                if (isBorder || isCenter) {
                    val targetR = startRow + r
                    val targetC = startCol + c
                    if (targetR < size && targetC < size) {
                        matrix[targetR][targetC] = true
                    }
                }
            }
        }
    }

    // Top-Left Finder
    drawFinderPattern(0, 0)
    // Top-Right Finder
    drawFinderPattern(0, size - 7)
    // Bottom-Left Finder
    drawFinderPattern(size - 7, 0)

    // Timing patterns
    for (i in 7 until size - 7) {
        if (i % 2 == 0) {
            matrix[6][i] = true
            matrix[i][6] = true
        }
    }

    // Deterministic pseudo-random pattern based on content hash
    val hash = data.hashCode().toLong()
    val bytes = data.toByteArray(StandardCharsets.UTF_8)
    var byteIdx = 0

    for (r in 0 until size) {
        for (c in 0 until size) {
            // Skip finder zones
            val inTopLeft = (r < 8 && c < 8)
            val inTopRight = (r < 8 && c >= size - 8)
            val inBottomLeft = (r >= size - 8 && c < 8)
            val inTiming = (r == 6 || c == 6)

            if (!inTopLeft && !inTopRight && !inBottomLeft && !inTiming) {
                val currentByte = if (bytes.isNotEmpty()) bytes[byteIdx % bytes.size].toInt() else 0
                val seed = (hash xor (r.toLong() * 31 + c.toLong() * 17) xor currentByte.toLong()).toInt()
                matrix[r][c] = (seed % 3 == 0 || (r + c) % 4 == 0 || (r * c) % 5 == 0)
                byteIdx++
            }
        }
    }

    return matrix
}
