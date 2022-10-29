package com.xbaimiao.mine.sponsor.core.kit

import com.xbaimiao.mine.sponsor.MineSponsor
import com.xbaimiao.mine.sponsor.core.deposit.Sponsor
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import taboolib.common.platform.function.submit
import taboolib.module.kether.KetherShell
import taboolib.platform.BukkitAdapter

data class KitSponsor(
    val name: String,
    val executes: List<String>,
    val cny: Double
) {

    companion object {
        val cache = ArrayList<KitSponsor>()

        fun reload() {
            cache.clear()
            MineSponsor.kit.reload()
            for (key in MineSponsor.kit.getKeys(false)) {
                cache.add(
                    KitSponsor(
                        key,
                        MineSponsor.kit.getStringList("$key.commands"),
                        MineSponsor.kit.getDouble("$key.cny")
                    )
                )
            }
        }

    }

    fun givePlayer(player: Player, sponsor: Sponsor) {
        submit {
            for (execute in executes) {
                val rawCommand = execute.replace("%player_name%", player.name)
                    .replace("%pay_cny%", sponsor.price.toInt().toString())

                if (rawCommand.startsWith("[tell] ")) {
                    val tell = rawCommand.substring(7)
                    player.sendMessage(tell)
                }
                if (rawCommand.startsWith("[kether] ")) {
                    val kether = rawCommand.substring(9)
                    KetherShell.eval(
                        kether,
                        sender = BukkitAdapter().adaptCommandSender(player),
                        namespace = arrayListOf("minesponsor")
                    )
                }
                if (rawCommand.startsWith("[console] ")) {
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), rawCommand.substring(10))
                }
                if (rawCommand.startsWith("[player] ")) {
                    Bukkit.dispatchCommand(player, rawCommand.substring(9))
                }
            }
        }
    }

}