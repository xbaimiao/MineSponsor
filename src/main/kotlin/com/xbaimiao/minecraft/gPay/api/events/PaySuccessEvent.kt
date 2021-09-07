package com.xbaimiao.minecraft.gPay.api.events

import com.xbaimiao.minecraft.gPay.deposit.Deposit
import org.bukkit.entity.Player
import org.bukkit.event.Event
import org.bukkit.event.HandlerList

class PaySuccessEvent(val deposit: Deposit) : Event(true) {

    override fun getHandlers(): HandlerList {
        return handlerList
    }

    val player: Player
        get() = deposit.player
    val amount: Double
        get() = deposit.price

    companion object {
        @JvmStatic
        val handlerList = HandlerList()
    }

}