package com.example.ui.common.component.screen

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.example.ui.common.component.bar.TopAppBar
import com.example.ui.common.component.icon.AppIcons

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBarScaffold(
    title: String,
    navigationIcon: ImageVector? = AppIcons.ArrowBack,
    navigationIconContentDescription: String? = "Back Icon",
    onNavigationClick: () -> Unit = {},
    actionIcon: ImageVector? = null,
    actionIconContentDescription: String? = null,
    onActionClick: () -> Unit = {},
    content: @Composable (PaddingValues) -> Unit
) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.surface,
        topBar = {
            TopAppBar(
                title = title,
                modifier = Modifier
                    .zIndex(1F)
                    .shadow(
                        elevation = 5.dp,
                        spotColor = MaterialTheme.colorScheme.onBackground
                    ),
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                ),
                navigationIcon = navigationIcon,
                onNavigationClick = onNavigationClick,
                navigationIconContentDescription = navigationIconContentDescription,
                actionIcon = actionIcon,
                actionIconContentDescription = actionIconContentDescription,
                onActionClick = onActionClick,
            )
        },
        content = content
    )
}