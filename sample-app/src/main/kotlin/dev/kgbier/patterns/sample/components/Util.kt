package dev.kgbier.patterns.sample.components

import androidx.compose.material3.LocalTextStyle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.Dp

@Composable
fun rememberLocalTextSizeDp(
    style: TextStyle = LocalTextStyle.current,
    textMeasurer: TextMeasurer = rememberTextMeasurer(),
): Dp {
    val density = LocalDensity.current

    val textLayoutResult = remember(density, style) {
        textMeasurer.measure(
            text = AnnotatedString("M"),
            style = style,
        )
    }

    return with(density) { textLayoutResult.size.height.toDp() }
}
