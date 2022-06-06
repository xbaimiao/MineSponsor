package com.xbaimiao.minecraft.gPay

import com.xbaimiao.minecraft.gPay.command.Commands
import com.xbaimiao.minecraft.gPay.core.Service
import com.xbaimiao.minecraft.gPay.datacenter.DataCenter
import com.xbaimiao.minecraft.gPay.datacenter.impl.SQLiteDataCenter
import com.xbaimiao.minecraft.gPay.hook.PlaceholderAPI
import com.xbaimiao.minecraft.gPay.kit.Kit
import com.xbaimiao.minecraft.gPay.kit.KitListener
import com.xbaimiao.minecraft.gPay.logger.FileLogger
import com.xbaimiao.minecraft.gPay.logger.Logger
import org.bukkit.Bukkit
import taboolib.common.platform.Plugin
import taboolib.common.platform.function.info
import taboolib.module.chat.colored
import taboolib.module.configuration.Config
import taboolib.module.configuration.ConfigFile

object GPayX : Plugin() {

    @Config(value = "config.yml", migrate = true)
    lateinit var config: ConfigFile
        private set

    @Config(value = "key.yml")
    lateinit var key: ConfigFile
        private set

    @Config(value = "kits.yml")
    lateinit var kits: ConfigFile
        private set

    val logger: Logger get() = FileLogger.getInstance()

    var hasBot = false
        private set

    lateinit var dataCenter: DataCenter
        private set

    val kitList = ArrayList<Kit>()

    override fun onEnable() {
        info("GPay-X 已启用，感谢你支持正版插件")
        dataCenter = SQLiteDataCenter()
        Commands.register()
        load()
        hasBot = Bukkit.getPluginManager().getPlugin("AmazingBot") != null
        if (hasBot) {
            info("已支持amazingBot订单通知")
        }
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            PlaceholderAPI().register()
        }
    }

    override fun onDisable() {
        KitListener.sqlite.close()
        FileLogger.cache.forEach { it.writer.close() }
    }

    fun load() {
        key.reload()
        Service.reload()
        config.reload()
        kits.reload()
        kitList.clear()
        for ((a, key) in kits.getKeys(false).withIndex()) {
            val enable = kits.getBoolean("$key.Enable")
            val maxAmount = kits.getInt("$key.MaxAmount")
            val premise = kits.getInt("$key.Premise")
            val commands = kits.getStringList("$key.Commands").colored()
            kitList.add(Kit(key, enable, maxAmount, premise, commands))
            info("加载了 ${a + 1} 个礼包")
        }
    }

}