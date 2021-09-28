package com.xbaimiao.minecraft.gPay

import com.xbaimiao.minecraft.gPay.command.Commands
import com.xbaimiao.minecraft.gPay.kit.Kit
import com.xbaimiao.minecraft.gPay.kit.KitListener
import com.xbaimiao.minecraft.gPay.utils.Service
import org.bukkit.Bukkit
import taboolib.common.env.RuntimeDependency
import taboolib.common.platform.Plugin
import taboolib.common.platform.function.info
import taboolib.module.chat.colored
import taboolib.module.configuration.Config
import taboolib.module.configuration.SecuredFile
import taboolib.platform.BukkitPlugin
import java.net.URL

@RuntimeDependency("!com.google.zxing:core:3.4.1")
object Main : Plugin() {

    @Config(value = "config.yml")
    lateinit var config: SecuredFile
        private set

    @Config(value = "key.yml")
    lateinit var key: SecuredFile
        private set

    @Config(value = "kits.yml")
    lateinit var kits: SecuredFile
        private set

    @Config(value = "record.yml")
    lateinit var record: SecuredFile
        private set

    val prefix: String get() = config.getString("prefix").colored()

    val maxPrice: Double get() = config.getDouble("maxPrice")

    val minPrice: Double get() = config.getDouble("minPrice")

    val kitList = ArrayList<Kit>()

    val exchange get() = config.getInt("setting.exchange")

    val cmds: List<String> get() = config.getStringList("setting.commands").colored()

    val title get() = config.getString("title").colored().split("/")

    val signLines get() = config.getString("gui.sign").colored().split("\n").toTypedArray()

    override fun onEnable() {
        info("  __________________               ____  ___")
        info(" /  _____/\\______   \\_____  ___.__.\\   \\/  /")
        info("/   \\  ___ |     ___/\\__  \\<   |  | \\     / ")
        info("\\    \\_\\  \\|    |     / __ \\\\___  | /     \\ ")
        info(" \\______  /|____|    (____  / ____|/___/\\  \\")
        info("        \\/                \\/\\/           \\_/")
        val state = sendGet(URL("http://www.xbaimiao.com/check?text=${config.getString("key")}"))
        if (!state.contains("true")) {
            onDisable()
            info("激活码不存在")
            info("服务器将在5秒后重启")
            Thread.sleep(5000)
            Bukkit.getServer().shutdown()
            return
        }
        Commands.register()
        load()
    }

    override fun onDisable() {
        KitListener.sqlite.close()
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