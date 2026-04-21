package com.scelio.brainest.presentation.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.background
import androidx.compose.ui.graphics.Color
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * A small search bar used in Study Sets screen. Emits query changes through [onQueryChanged].
 */
@Composable
fun StudySetsSearchBar(
    modifier: Modifier = Modifier,
    placeholder: String = "Search",
    initialQuery: String = "",
    onQueryChanged: (String) -> Unit = {}
) {
    var query by remember { mutableStateOf(initialQuery) }

    OutlinedTextField(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .background(Color(0xFFF1F2F4), RoundedCornerShape(24.dp)),
        value = query,
        onValueChange = {
            query = it
            onQueryChanged(it)
        },
        leadingIcon = { Icon(imageVector = Icons.Default.Search, contentDescription = null) },
        placeholder = { Text(text = placeholder, style = MaterialTheme.typography.bodyMedium) },
        singleLine = true,
        shape = RoundedCornerShape(24.dp)
    )
}

