package dev.stp.app.presentation.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import dev.stp.app.presentation.ui.theme.Typography

@Composable
fun AppTextField(
    modifier: Modifier = Modifier,
    value: String,
    label: String? = null,
    isError: Boolean = false,
    onValueChange: (String) -> Unit,
    placeholderText: String? = null,
    textStyle: TextStyle = Typography.bodyLarge,
) {
    TextField(
        modifier = modifier,
        label = label?.let { { Text(label) } },
        value = value,
        onValueChange = onValueChange,
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
        ),
        isError = isError,
        placeholder = placeholderText?.let {
            { Text(text = placeholderText) }
        },
        textStyle = textStyle.copy(color = MaterialTheme.colorScheme.onPrimary)
    )
}