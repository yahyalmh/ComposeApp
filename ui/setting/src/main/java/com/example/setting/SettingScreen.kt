package com.example.setting

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.common.ThemeType
import com.example.core.component.LoadingView
import com.example.core.component.bar.AppBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingScreen(
    navController: NavHostController,
    viewModel: SettingViewModel = hiltViewModel()
) {
    val uiState = viewModel.state.value
    androidx.compose.material.Scaffold(
        topBar = {
            AppBar(
                titleRes = R.string.setting,
                colors = TopAppBarDefaults
                    .centerAlignedTopAppBarColors(containerColor = MaterialTheme.colorScheme.background),
            )
        }
    ) { padding ->

        LoadingView(isLoading = uiState.isLoading)

        ContentView(
            modifier = Modifier.padding(padding),
            uiState.currentThemeType
        ) {
            viewModel.onEvent(SettingUiEvent.ChangeTheme(it))
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun ContentView(
    modifier: Modifier = Modifier,
    currentThemeType: ThemeType?,
    onChangeTheme: (theme: ThemeType) -> Unit
) {
    val themeTypes = ThemeType.values().asList()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {

        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            color = MaterialTheme.colorScheme.onBackground,
            text = stringResource(id = R.string.theme),
            style = MaterialTheme.typography.titleLarge
        )


        Row {
            themeTypes.forEach { type ->
                AssistChip(
                    modifier = Modifier.padding(8.dp),
                    onClick = { onChangeTheme(type) },
                    label = { Text(type.name) },
                    leadingIcon = {
                        AnimatedVisibility(visible = currentThemeType == type) {
                            Icon(
                                Icons.Filled.Check,
                                contentDescription = "Check Icon",
                                Modifier.size(AssistChipDefaults.IconSize)
                            )
                        }
                    }
                )
            }
        }

        Divider(modifier = Modifier.padding(top = 6.dp))
    }
}

@Preview(showSystemUi = true)
@Composable
fun ContentPreview() {
    ContentView(currentThemeType = ThemeType.SYSTEM) {}
}