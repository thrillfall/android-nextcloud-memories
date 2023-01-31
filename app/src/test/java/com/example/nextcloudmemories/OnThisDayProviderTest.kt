package com.example.nextcloudmemories

import com.example.nextcloudmemories.OnThisDayProvider.generateDayIds
import org.junit.Test
import kotlin.test.assertEquals

internal class OnThisDayProviderTest {

    @Test
    fun testIdsForRecentDaysAreGenerated() {
        val expectedStrings: List<String> = listOf("19000", "18999", "18998", "18637", "18636", "18635", "18634", "18633", "18272", "18271", "18270", "18269", "18268")
        assertEquals(expectedStrings, generateDayIds(19000, 2));
    }
}

