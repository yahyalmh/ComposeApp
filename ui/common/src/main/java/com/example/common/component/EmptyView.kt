package com.example.common.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.common.component.icon.AppIcons
import com.example.ui.common.R

@Composable
fun EmptyView(
    modifier: Modifier = Modifier,
    isVisible: Boolean = true,
    icon: ImageVector,
    message: String,
) {
    ContentView(isVisible, modifier, icon) {
        Text(
            modifier = Modifier.padding(top = 10.dp),
            color = MaterialTheme.colorScheme.onBackground,
            fontSize = 16.sp,
            fontStyle = FontStyle.Normal,
            text = message
        )
    }
}

@Composable
fun EmptyView(
    modifier: Modifier = Modifier,
    isVisible: Boolean = true,
    icon: ImageVector,
    message: AnnotatedString,
) {
    ContentView(isVisible, modifier, icon) {
        Text(
            modifier = Modifier.padding(top = 10.dp),
            color = MaterialTheme.colorScheme.onBackground,
            fontSize = 16.sp,
            text = message
        )
    }
}

@Composable
private fun ContentView(
    isVisible: Boolean,
    modifier: Modifier,
    icon: ImageVector,
    message: @Composable () -> Unit
) {
    AnimatedVisibility(
        visible = isVisible,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        Column(
            modifier = modifier
                .background(MaterialTheme.colorScheme.background)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Icon(
                modifier = Modifier
                    .padding(12.dp)
                    .size(80.dp),
                imageVector = icon,
                contentDescription = stringResource(id = R.string.emptyIcon),
                tint = MaterialTheme.colorScheme.onBackground
            )

            message()
        }
    }
}

@Preview(showSystemUi = true)
@Composable
fun Preview() {
    EmptyView(isVisible = true, icon = AppIcons.Search, message = "Not Found")
}