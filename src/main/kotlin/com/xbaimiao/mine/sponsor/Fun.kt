package com.xbaimiao.mine.sponsor

import com.xbaimiao.mine.sponsor.core.Setting
import com.xbaimiao.mine.sponsor.core.deposit.Sponsor
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import taboolib.common.platform.function.submit
import taboolib.module.kether.KetherShell
import taboolib.platform.BukkitAdapter
import taboolib.xbaimiao.util.engine.IK

/**
 * 处理配置文件的commands
 */
fun List<String>.execute(player: Player, deposit: Sponsor) {
    //应该给的点券
    val num = deposit.price * Setting.exchange
    //主线程执行
    submit {
        for (s in this@execute) {
            val cmd = s.replace("%player_name%", player.name).replace("%pay_money%", num.toInt().toString())
                .replace("%pay_cny%", deposit.price.toInt().toString())

            if (cmd.startsWith("[tell] ")) {
                val tell = cmd.substring(7)
                player.sendMessage(tell)
            }
            if (cmd.startsWith("[kether] ")) {
                val kether = cmd.substring(9)
                KetherShell.eval(
                    kether,
                    sender = BukkitAdapter().adaptCommandSender(player),
                    namespace = arrayListOf("minesponsor")
                )
            }
            if (cmd.startsWith("[console] ")) {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd.substring(10))
            }
            if (cmd.startsWith("[player] ")) {
                Bukkit.dispatchCommand(player, cmd.substring(9))
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