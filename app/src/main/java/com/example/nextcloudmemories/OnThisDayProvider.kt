package com.example.nextcloudmemories

object OnThisDayProvider {
    fun generateDayIds(todayId: Int, goBackNYears: Int): List<String> {
        val dayIds: ArrayList<Int> = ArrayList()
        dayIds.add(todayId)
        dayIds.add(todayId - 1)
        dayIds.add(todayId - 2)

        for (i in 1..goBackNYears) {
            val todayNYearsAgo = todayId - (365 * i)
            dayIds.add(todayNYearsAgo + 2)
            dayIds.add(todayNYearsAgo + 1)
            dayIds.add(todayNYearsAgo)
            dayIds.add(todayNYearsAgo - 1)
            dayIds.add(todayNYearsAgo - 2)
        }

        return dayIds.map { it.toString() }
    }

}