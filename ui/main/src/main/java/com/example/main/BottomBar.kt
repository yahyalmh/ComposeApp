package com.example.main

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import com.example.common.component.icon.IconType
import com.example.main.NiaNavigationDefaults.navigationContentColor
import com.example.main.NiaNavigationDefaults.navigationSelectedItemColor
import com.example.ui.main.R

@Composable
fun AppBottomBar(
    destinations: List<BottomBarTab>,
    onNavigateToDestination: (BottomBarTab) -> Unit,
    currentDestination: NavDestination?,
    modifier: Modifier = Modifier
) {
    NavigationBar(
        modifier = modifier,
        contentColor = navigationContentColor(),
        tonalElevation = 0.dp
    ) {
        destinations.forEach { destination ->
            val selected = currentDestination.isInHierarchy(destination)
            NavigationBarItem(
                enabled = true,
                selected = selected,
                onClick = { onNavigateToDestination(destination) },
                icon = {
                    val icon = if (selected) {
                        destination.selectedIcon
                    } else {
                        destination.unselectedIcon
                    }
                    when (icon) {
                        is IconType.ImageVectorIcon -> Icon(
                            imageVector = icon.imageVector,
                            contentDescription = null
                        )
                        is IconType.DrawableResourceIcon -> Icon(
                            painter = painterResource(id = icon.id),
                            contentDescription = null
                        )
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = navigationSelectedItemColor(),
                    unselectedIconColor = navigationContentColor(),
                    selectedTextColor = navigationSelectedItemColor(),
                    unselectedTextColor = navigationContentColor(),
                    indicatorColor = NiaNavigationDefaults.navigationIndicatorColor()
                ),
                label = { Text(stringResource(destination.iconTextId)) }
            )
        }
    }
}

private fun NavDestination?.isInHierarchy(destination: BottomBarTab) =
    this?.hierarchy?.any {
        it.route?.contains(destination.name, true) ?: false
    } ?: false

object NiaNavigationDefaults {
    @Composable
    fun navigationContentColor() = MaterialTheme.colorScheme.onSurfaceVariant

    @Composable
    fun navigationSelectedItemColor() = MaterialTheme.colorScheme.onPrimaryContainer

    @Composable
    fun navigationIndicatorColor() = MaterialTheme.colorScheme.primaryContainer
}

enum class BottomBarTab(
    val selectedIcon: IconType,
    val unselectedIcon: IconType,
    val iconTextId: Int,
    val titleTextId: Int
) {
    Home(
        selectedIcon = IconType.ImageVectorIcon(Icons.Default.Home),
        unselectedIcon = IconType.ImageVectorIcon(Icons.Default.Home),
        iconTextId = R.string.home,
        titleTextId = R.string.home
    ),
    FAVORITE(
        selectedIcon = IconType.ImageVectorIcon(Icons.Default.Favorite),
        unselectedIcon = IconType.ImageVectorIcon(Icons.Default.FavoriteBorder),
        iconTextId = R.string.favorite,
        titleTextId = R.string.favorite
    ),
    SETTING(
        selectedIcon = IconType.ImageVectorIcon(Icons.Default.Settings),
        unselectedIcon = IconType.ImageVectorIcon(Icons.Default.Settings),
        iconTextId = R.string.setting,
        titleTextId = R.string.setting
    )
}