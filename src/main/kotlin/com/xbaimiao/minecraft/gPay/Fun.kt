package com.xbaimiao.minecraft.gPay

import com.xbaimiao.minecraft.gPay.core.Config
import com.xbaimiao.minecraft.gPay.deposit.Deposit
import org.bukkit.Bukkit
import org.bukkit.boss.BarColor
import org.bukkit.boss.BarStyle
import org.bukkit.entity.Player
import taboolib.common.platform.function.submit
import taboolib.xbaimiao.util.engine.IK
import java.util.regex.Pattern

fun String.isNumber(): Boolean {
    if (this == "" || this.startsWith("0.00")) {
        return false
    }
    val pattern = Pattern.compile("[0-9.]*")
    var a = false
    for (c in this) {
        if (c == '.') {
            if (a) {
                return false
            }
            a = true
        }
    }
    val isNum = pattern.matcher(this)
    if (isNum.matches()) {
        val amount = this.toDouble()
        if (amount < Config.minPrice || amount > Config.maxPrice) {
            return false
        }
        return true
    }
    return false
}

class Bar(val string: String, val time: Int)

/**
 * 处理配置文件的commands
 */
fun List<String>.execute(player: Player, deposit: Deposit) {
    //应该给的点券
    val num = deposit.price * Config.exchange
    //主线程执行
    submit {
        for (s in this@execute) {
            if (s.lowercase() == "return") {
                return@submit
            }
            val cmd = s.replace("%player_name%", player.name).replace("%pay_money%", num.toInt().toString())
            if (cmd.startsWith("[tell] ")) {
                val tell = cmd.substring(7)
                player.sendMessage(tell)
            }
            if (cmd.startsWith("[bc] ")) {
                val tell = cmd.substring(5)
                for (onlinePlayer in Bukkit.getOnlinePlayers()) {
                    onlinePlayer.sendMessage(tell)
                }
            }
            if (cmd.startsWith("[bossbar] ")) {
                val string = cmd.substring(10)
                val time = string.split("&time&:")[1].toInt()
                val bar = Bar(string.split("&time&:")[0], time)
                val bossBar = Bukkit.createBossBar("", BarColor.BLUE, BarStyle.SOLID)
                for (onlinePlayer in Bukkit.getOnlinePlayers()) {
                    bossBar.addPlayer(onlinePlayer)
                }
                submit(async = true) {
                    for (c in bar.string) {
                        bossBar.setTitle(bossBar.title + c)
                        if (c != '§') {
                            Thread.sleep(100)
                        }
                    }
                }
                submit(delay = bar.time * 20L) {
                    bossBar.removeAll()
                }
            }
            if (cmd.startsWith("[console] ")) {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd.substring(10))
            }
            if (cmd.startsWith("[player] ")) {
                Bukkit.dispatchCommand(player, cmd.substring(9))
            }
            if (cmd.startsWith("[condition] ")) {
                val condition = cmd.substring(12).replace(" ", "")
                if (condition.contains(">")) {
                    val args = condition.split(">")
                    if (args[0].toDouble() > args[1].toDouble()) {
                        continue
                    } else {
                        break
                    }
                }
                if (condition.contains("<")) {
                    val args = condition.split("<")
                    if (args[0].toDouble() < args[1].toDouble()) {
                        continue
                    } else {
                        break
                    }
                }
                if (condition.contains("=")) {
                    val args = condition.split("=")
                    if (args[0].toDouble() == args[1].toDouble()) {
                        continue
                    } else {
                        break
                    }
                }
            }
            if (cmd.startsWith("[points] ")) {
                val reward = IK.eval<Int>(cmd.substring(9))
                for (onlinePlayer in Bukkit.getOnlinePlayers()) {
                    val name = onlinePlayer.name
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "points give $name $reward")
                }
            }

        }
    }

}