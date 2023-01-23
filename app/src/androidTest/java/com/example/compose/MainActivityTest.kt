package com.example.compose

import android.content.Context.MODE_PRIVATE
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.text.input.ImeAction
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.data.common.database.DbConfig.DATABASE_NAME
import com.example.data.common.database.DbConfig.FAVORITE_TABLE_NAME
import com.example.main.TestTag
import com.example.ui.common.R
import com.example.ui.common.ThemeType
import com.example.ui.common.test.getString
import com.example.ui.common.test.scrollToEnd
import com.example.ui.common.test.wait
import com.example.ui.common.test.waitUntilDisplayed
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import com.example.ui.common.R.string as CommonString
import com.example.ui.common.test.TestTag as UiCommonTestTag
import com.example.ui.detail.R.string as DetailString
import com.example.ui.home.R.string as HomeString
import com.example.ui.main.R.string as MainString


@RunWith(AndroidJUnit4::class)
class MainActivityTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Before
    fun setup() {
        val db = InstrumentationRegistry.getInstrumentation()
            .targetContext.openOrCreateDatabase(
                DATABASE_NAME,
                MODE_PRIVATE,
                null
            )
        db.delete(FAVORITE_TABLE_NAME, null, null)
    }

    @Test
    fun test_homeScreen_scrolling() {
        with(composeTestRule) {
            onNodeWithTag(TestTag.BOTTOM_BAR).assertIsDisplayed()
            waitUntilDisplayed(hasScrollToIndexAction())
            scrollToEnd(hasScrollToIndexAction(), step = 30)
            onNode(hasScrollToIndexAction()).performScrollToIndex(0)
        }
    }


    @Test
    fun test_navigationTo_detailScreen_and_return() {
        with(composeTestRule) {
            waitUntilDisplayed(hasScrollToIndexAction())
            onNodeWithTag(TestTag.BOTTOM_BAR).assertIsDisplayed()

            onAllNodes(hasScrollToIndexAction()).onFirst().performClick()

            onNodeWithTag(TestTag.BOTTOM_BAR).assertDoesNotExist()

            onNodeWithContentDescription(getString(DetailString.favoriteIcon)).performClick()

            onNodeWithContentDescription(getString(R.string.backIconContentDescription)).performClick()
            onNodeWithTag(TestTag.BOTTOM_BAR).assertIsDisplayed()
        }
    }


    @Test
    fun test_navigateTo_favoriteScreen_and_checkFavorites() {
        with(composeTestRule) {
            onNodeWithText(getString(MainString.favorite)).performClick()

            onNodeWithTag(UiCommonTestTag.EMPTY_VIEW).assertIsDisplayed()

            onNodeWithText(getString(MainString.home)).performClick()
            waitUntilDisplayed(hasScrollToIndexAction())

            onAllNodesWithContentDescription(getString(CommonString.favoriteIconDescription))
                .onFirst()
                .performClick()

            onNodeWithText(getString(MainString.favorite)).performClick()

            waitUntilDisplayed(hasScrollToIndexAction())
            onNodeWithTag(UiCommonTestTag.EMPTY_VIEW).assertDoesNotExist()
            onAllNodesWithContentDescription(getString(CommonString.favoriteIconDescription))
                .assertCountEquals(1)

            onAllNodesWithContentDescription(getString(CommonString.favoriteIconDescription))
                .onFirst()
                .performClick()
            onNodeWithTag(UiCommonTestTag.EMPTY_VIEW).assertIsDisplayed()

            onAllNodesWithContentDescription(getString(CommonString.favoriteIconDescription))
                .assertCountEquals(0)
        }
    }

    @Test
    fun test_navigateTo_searchScreen_and_return() {
        with(composeTestRule) {
            onNodeWithContentDescription(getString(HomeString.searchIconContentDescription)).performClick()

            val query = "b"
            onNode(hasImeAction(ImeAction.Search)).performTextInput(query)
            waitUntilDisplayed(hasScrollToIndexAction())

            onNode(hasScrollToIndexAction()).onChildren().onFirst().performClick()
            onNodeWithContentDescription(getString(R.string.backIconContentDescription)).performClick()

            onNodeWithText(getString(R.string.cancel)).performClick()
        }
    }

    @Test
    fun test_setting_change_app_theme() {
        with(composeTestRule) {
            onNodeWithText(getString(MainString.setting)).performClick()
            onNodeWithText(ThemeType.LIGHT.name).performClick()
            wait(200)
            onNodeWithText(ThemeType.DARK.name).performClick()
            wait(200)
            onNodeWithText(ThemeType.SYSTEM.name).performClick()
            onNodeWithText(getString(MainString.home)).performClick()
        }
    }
}