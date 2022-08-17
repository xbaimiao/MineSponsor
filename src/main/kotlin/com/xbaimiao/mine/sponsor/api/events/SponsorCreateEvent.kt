package com.xbaimiao.mine.sponsor.api.events

import com.xbaimiao.mine.sponsor.core.deposit.Sponsor
import org.bukkit.entity.Player
import org.bukkit.event.Event
import org.bukkit.event.HandlerList

class SponsorCreateEvent(val deposit: Sponsor) : Event(true) {
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