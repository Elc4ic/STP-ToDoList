package dev.stp.app.presentation.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle

@Composable
fun AppTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholderText: String,
    textStyle: TextStyle,
    modifier: Modifier = Modifier
) {
    TextField(
        modifier = modifier,
        value = value,
        onValueChange = onValueChange,
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
        ),
        placeholder = {
            Text(
                text = placeholderText,
                color = MaterialTheme.colorScheme.onPrimary,
                fontWeight = textStyle.fontWeight,
                fontSize = textStyle.fontSize
            )
        },
        textStyle = textStyle.copy(color = MaterialTheme.colorScheme.onPrimary)
    )
}