package com.xbaimiao.mine.sponsor

import com.xbaimiao.mine.sponsor.api.hook.PlaceholderAPI
import com.xbaimiao.mine.sponsor.core.deposit.MineSponsorService
import com.xbaimiao.mine.sponsor.core.deposit.Sponsor
import org.bukkit.Bukkit
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerSwapHandItemsEvent
import taboolib.common.platform.Plugin
import taboolib.common.platform.event.SubscribeEvent
import taboolib.common.platform.function.submit
import taboolib.module.configuration.Config
import taboolib.module.configuration.ConfigFile
import taboolib.platform.util.sendLang

object MineSponsor : Plugin() {

    @Config(value = "config.yml", migrate = true)
    lateinit var config: ConfigFile
        private set

    override fun onEnable() {
        load()
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            PlaceholderAPI().register()
        }
    }

    fun load() {
        config.reload()
        MineSponsorService.reload()
    }

    @SubscribeEvent
    fun join(event: PlayerJoinEvent) {
        Sponsor.wait.remove(event.player.uniqueId)?.let {
            submit(delay = 20) {
                it.player = event.player
                it.callback.invoke(it)
            }
        }
    }

    @SubscribeEvent
    fun a(event: PlayerSwapHandItemsEvent) {
        Sponsor.cache.remove(event.player.uniqueId)?.let {
            event.player.sendLang("pay-close")
            event.player.updateInventory()
        }
    }

}