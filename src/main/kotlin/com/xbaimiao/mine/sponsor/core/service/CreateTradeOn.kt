package com.xbaimiao.mine.sponsor.core.service

import java.util.*

interface CreateTradeOn {

    fun createOutTradeNo(): String {
        val calendar = Calendar.getInstance()
        return "${calendar.get(Calendar.YEAR)}${calendar.get(Calendar.MONTH) + 1}${calendar.get(Calendar.DATE)}" + (System.currentTimeMillis() + (Math.random() * 10000000L).toLong()).toString()
    }

}