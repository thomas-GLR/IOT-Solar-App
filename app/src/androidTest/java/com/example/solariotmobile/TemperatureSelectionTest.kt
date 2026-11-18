package com.example.solariotmobile

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.performClick
import com.example.solariotmobile.ui.screens.temperatureScreen.TemperaturesEvolution
import java.time.LocalDateTime
import org.junit.Assert.assertEquals
import com.example.solariotmobile.data.AggregationType
import com.example.solariotmobile.ui.screens.temperatureScreen.ButtonForDialog
import org.junit.Rule
import org.junit.Test

class TemperatureSelectionTest {
    @get:Rule
    val compose = createComposeRule()

    @Test
    fun periodSelectionsSurviveSwitching() {
        val requests = mutableListOf<Pair<AggregationType, LocalDateTime>>()
        compose.setContent {
            TemperaturesEvolution(true, "", emptyList()) { period, start, _ ->
                requests.add(period to start)
            }
        }
        compose.onNodeWithText("Mois").performClick()
        compose.onAllNodesWithText("Mois")[1].performClick()
        compose.onNodeWithText("Janvier").performClick()
        compose.onNodeWithText("OK").performClick()
        compose.onNodeWithText("Ann\u00e9es").performClick()
        compose.onNodeWithText("Ann\u00e9e").performClick()
        compose.onNodeWithText("2025").performClick()
        compose.onNodeWithText("OK").performClick()
        compose.onAllNodesWithText("Mois")[0].performClick()
        compose.onNodeWithText("Janvier").assertIsDisplayed()
        compose.runOnIdle { assertEquals(1, requests.last().second.monthValue) }
        compose.onNodeWithText("Ann\u00e9es").performClick()
        compose.onNodeWithText("2025").assertIsDisplayed()
        compose.runOnIdle { assertEquals(2025, requests.last().second.year) }
    }

    @Test
    fun invalidMonthIndexDoesNotCrashYearSelector() {
        compose.setContent {
            ButtonForDialog(10, AggregationType.YEARS) {}
        }
        compose.onNodeWithText("Ann\u00e9e").assertIsDisplayed()
    }

    @Test
    fun switchingPeriodsUpdatesDisplayedOption() {
        val period = mutableStateOf(AggregationType.MONTHS)
        val index = mutableStateOf(10)
        compose.setContent {
            ButtonForDialog(index.value, period.value) {}
        }
        repeat(3) {
            compose.onNodeWithText("Novembre").assertIsDisplayed()
            compose.runOnIdle {
                index.value = 0
                period.value = AggregationType.YEARS
            }
            compose.onNodeWithText("2025").assertIsDisplayed()
            compose.runOnIdle {
                index.value = 10
                period.value = AggregationType.MONTHS
            }
        }
    }
}

