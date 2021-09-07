package com.xbaimiao.minecraft.gPay.utils

import com.xbaimiao.minecraft.gPay.Main
import com.xbaimiao.minecraft.gPay.deposit.Deposit
import taboolib.common.platform.function.submit

object Log {

    val record = Main.record

    @Synchronized
    fun add(deposit: Deposit) {
        submit(async = true) {
            val time = System.currentTimeMillis()
            record.set("$time.player", deposit.player.name)
            record.set("$time.amount", deposit.price)
            record.set("$time.type", deposit.type)
            record.saveToFile()
        }
    }

}