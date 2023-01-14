package com.xbaimiao.mine.sponsor

import com.xbaimiao.mine.sponsor.api.hook.PlaceholderAPI
import com.xbaimiao.mine.sponsor.core.deposit.MineSponsorService
import com.xbaimiao.mine.sponsor.core.deposit.Sponsor
import com.xbaimiao.mine.sponsor.core.kit.KitSponsor
import com.xbaimiao.mine.sponsor.datacenter.DataCenter
import com.xbaimiao.mine.sponsor.datacenter.impl.MysqlDataCenter
import com.xbaimiao.mine.sponsor.datacenter.impl.SQLiteDataCenter
import org.bukkit.Bukkit
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerSwapHandItemsEvent
import taboolib.common.platform.Plugin
import taboolib.common.platform.event.SubscribeEvent
import taboolib.common.platform.function.submit
import taboolib.module.configuration.Config
import taboolib.module.configuration.ConfigFile
import taboolib.module.configuration.Configuration
import taboolib.platform.BukkitPlugin
import taboolib.platform.util.sendLang
import java.io.File

object MineSponsor : Plugin() {

    @Config(value = "config.yml", migrate = true)
    lateinit var config: ConfigFile
        private set

    @Config(value = "key.yml")
    lateinit var key: ConfigFile
        private set

    @Config(value = "kit.yml")
    lateinit var kit: Configuration

    lateinit var dataCenter: DataCenter
        private set

    override fun onEnable() {
        val file = File(BukkitPlugin.getInstance().dataFolder, "pem${File.separator}readme.txt")
        if (!file.exists()) {
            BukkitPlugin.getInstance().saveResource("pem${File.separator}readme.txt", false)
        }
        dataCenter = if (config.getBoolean("mysql.enable")) {
            MysqlDataCenter()
        } else {
            SQLiteDataCenter()
        }
        load()
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            PlaceholderAPI().register()
        }
    }

    fun load() {
        key.reload()
        MineSponsorService.reload()
        config.reload()
        KitSponsor.reload()
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