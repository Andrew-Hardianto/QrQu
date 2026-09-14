package com.drew.qrqu.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.drew.qrqu.ui.theme.BrutalBlack
import com.drew.qrqu.ui.theme.BrutalPureWhite
import com.drew.qrqu.ui.theme.BrutalRed

import androidx.compose.ui.text.TextStyle

@Composable
fun BrutalButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    backgroundColor: Color = BrutalRed,
    textColor: Color = BrutalPureWhite,
    textStyle: TextStyle = TextStyle.Default,
    contentPadding: PaddingValues = PaddingValues(horizontal = 24.dp, vertical = 14.dp)
) {
    val shape = RoundedCornerShape(0.dp) // Kaku, tanpa lengkung
    Box(
        modifier = modifier
            .padding(bottom = 6.dp, end = 6.dp) // Ruang untuk bayangan
    ) {
        // Shadow (Kotak Hitam Pejal)
        Box(
            modifier = Modifier
                .matchParentSize()
                .offset(x = 6.dp, y = 6.dp)
                .background(BrutalBlack, shape)
        )
        // Main Button
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .clip(shape)
                .background(backgroundColor)
                .border(3.dp, BrutalBlack, shape)
                .clickable(onClick = onClick)
                .padding(contentPadding)
        ) {
            Text(
                text = text.uppercase(),
                color = textColor,
                fontWeight = FontWeight.Black,
                style = textStyle
            )
        }
    }
}

@Composable
fun BrutalCard(
    modifier: Modifier = Modifier,
    backgroundColor: Color = BrutalPureWhite,
    contentPadding: PaddingValues = PaddingValues(16.dp),
    content: @Composable () -> Unit
) {
    val shape = RoundedCornerShape(0.dp)
    Box(
        modifier = modifier
            .padding(bottom = 8.dp, end = 8.dp)
    ) {
        // Shadow (Kotak Hitam Pejal)
        Box(
            modifier = Modifier
                .matchParentSize()
                .offset(x = 8.dp, y = 8.dp)
                .background(BrutalBlack, shape)
        )
        // Main Card
        Box(
            modifier = Modifier
                .background(backgroundColor, shape)
                .border(4.dp, BrutalBlack, shape)
                .padding(contentPadding)
        ) {
            content()
        }
    }
}
