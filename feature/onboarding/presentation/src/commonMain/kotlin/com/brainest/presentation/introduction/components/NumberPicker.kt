package com.brainest.presentation.introduction.components

import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.scelio.brainest.designsystem.BrainestTheme
import com.scelio.brainest.designsystem.BricolageGrotesq
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun NumberPicker(
    value: Int,
    onValueChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
    range: IntRange = 0..18,
    itemHeight: Int = 80,
    visibleItemsCount: Int = 3,
    selectedTextStyle: TextStyle = TextStyle(
        fontFamily = BricolageGrotesq,
        fontWeight = FontWeight.ExtraBold,
        fontSize = 64.sp
    ),
    unselectedTextStyle: TextStyle = TextStyle(
        fontFamily = BricolageGrotesq,
        fontWeight = FontWeight.SemiBold,
        fontSize = 40.sp
    ),
    selectedColor: Color = Color(0xFF2C201F),
    unselectedColor: Color = Color(0xFFBDBDBD)
) {
    val halfVisibleItems = visibleItemsCount / 2
    
    // Calculate initial index (offset by halfVisibleItems for spacers)
    val initialIndex = remember(range, value) {
        (value - range.first + halfVisibleItems).coerceIn(0, range.count() + visibleItemsCount - 1)
    }
    
    val listState = rememberLazyListState(initialFirstVisibleItemIndex = initialIndex)
    
    // Snap to center behavior
    val flingBehavior = rememberSnapFlingBehavior(lazyListState = listState)
    
    // Calculate center item based on scroll position (subtract halfVisibleItems to get actual value)
    val centerItem by remember {
        derivedStateOf {
            val layoutInfo = listState.layoutInfo
            val viewportCenter = (layoutInfo.viewportStartOffset + layoutInfo.viewportEndOffset) / 2
            val centerItem = layoutInfo.visibleItemsInfo.minByOrNull { item ->
                kotlin.math.abs((item.offset + item.size / 2) - viewportCenter)
            }
            val rawIndex = centerItem?.index ?: initialIndex
            // Adjust for spacers: subtract halfVisibleItems to get actual value index
            (rawIndex - halfVisibleItems).coerceIn(0, range.count() - 1)
        }
    }
    
    // Notify value change when center item changes
    LaunchedEffect(centerItem) {
        val newValue = range.elementAt(centerItem)
        if (newValue != value) {
            onValueChange(newValue)
        }
    }
    
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height((itemHeight * visibleItemsCount).dp),
        contentAlignment = Alignment.Center
    ) {
        LazyColumn(
            state = listState,
            flingBehavior = flingBehavior,
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top spacer - allows first item to scroll to center
            items(halfVisibleItems) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(itemHeight.dp)
                        .alpha(0f)
                )
            }
            
            // Actual number items
            items(
                count = range.count(),
                key = { it }
            ) { index ->
                val number = range.elementAt(index)
                val distanceFromCenter = kotlin.math.abs(index - centerItem)
                
                // Calculate alpha based on distance from center
                val alpha = when {
                    distanceFromCenter == 0 -> 1f
                    distanceFromCenter == 1 -> 0.5f
                    else -> 0.2f
                }
                
                val isSelected = index == centerItem
                
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(itemHeight.dp)
                        .alpha(alpha),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = number.toString(),
                        style = if (isSelected) selectedTextStyle else unselectedTextStyle,
                        color = if (isSelected) selectedColor else unselectedColor
                    )
                }
            }
            
            // Bottom spacer - allows last item to scroll to center
            items(halfVisibleItems) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(itemHeight.dp)
                        .alpha(0f)
                )
            }
        }
    }
}

@Preview
@Composable
private fun NumberPickerPreview() {
    BrainestTheme {
        val state = remember { androidx.compose.runtime.mutableIntStateOf(9) }
        val value by state
        NumberPicker(
            value = value,
            onValueChange = { state.value = it },
            range = 0..18
        )
    }
}

@Preview
@Composable
private fun NumberPickerWithValuePreview() {
    BrainestTheme {
        NumberPicker(
            value = 5,
            onValueChange = {},
            range = 0..18
        )
    }
}
