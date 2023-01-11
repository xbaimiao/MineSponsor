package com.xbaimiao.mine.sponsor.core.command

import com.xbaimiao.mine.sponsor.MineSponsor
import com.xbaimiao.mine.sponsor.core.Check.isNumber
import com.xbaimiao.mine.sponsor.core.deposit.Sponsor
import com.xbaimiao.mine.sponsor.core.deposit.SponsorType
import com.xbaimiao.mine.sponsor.core.kit.KitSponsor
import com.xbaimiao.mine.sponsor.datacenter.FindTask
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import taboolib.common.platform.command.*
import taboolib.expansion.createHelper
import taboolib.platform.util.onlinePlayers
import taboolib.platform.util.sendLang
import java.text.SimpleDateFormat

@Suppress("ALL")
@CommandHeader("minesponsor", aliases = ["gpayx", "minepay", "pp"], permissionDefault = PermissionDefault.TRUE)
internal object Commands {

    @CommandBody
    val main = mainCommand {
        createHelper()
    }

    @CommandBody(optional = true)
    val open = subCommand {
        execute<Player> { sender, _, _ ->
            AutoSponsor.open(sender, AutoSponsor.AutoType.POINTS)
        }
    }

    @CommandBody(optional = true, permission = "minesponsor.reload")
    val reload = subCommand {
        execute<CommandSender> { sender, _, _ ->
            MineSponsor.load()
            sender.sendLang("reload")
        }
    }

    @CommandBody(optional = true, permissionDefault = PermissionDefault.TRUE)
    val kit = subCommand {
        dynamic("礼包名") {
            suggestion<Player> { _, _ ->
                KitSponsor.cache.map { it.name }
            }
            execute<Player> { sender, context, argument ->
                val kit = KitSponsor.cache.first { it.name == argument }
                AutoSponsor.open(sender, AutoSponsor.AutoType.KIT, kit)
            }
        }
    }

    @CommandBody(aliases = ["w", "wechat", "wx"], optional = true)
    val wechat = subCommand {
        dynamic {
            execute<Player> { sender, _, argument ->
                if (argument.isNumber()) {
                    val num = argument.toDouble()
                    Sponsor.create(sender, num, SponsorType.WX)
                } else {
                    sender.sendLang("input-error")
                }
            }
        }
    }

    @CommandBody(aliases = ["a", "alipay", "zfb"], optional = true)
    val alipay = subCommand {
        dynamic {
            execute<Player> { sender, _, argument ->
                if (argument.isNumber()) {
                    val num = argument.toDouble()
                    Sponsor.create(sender, num, SponsorType.ALIPAY)
                } else {
                    sender.sendLang("input-error")
                }
            }
        }
    }

    @CommandBody(optional = true, permission = "minesponsor.find")
    val find = subCommand {
        dynamic("年") {
            suggestion<CommandSender>(uncheck = true) { _, _ ->
                listOf(SimpleDateFormat("yyyy").format(System.currentTimeMillis()))
            }
            execute<CommandSender> { sender, _, argument ->
                FindTask(FindTask.Type.ALL, argument.toInt()).start(sender)
            }
            dynamic("月") {
                suggestion<CommandSender>(uncheck = true) { _, _ ->
                    listOf(SimpleDateFormat("MM").format(System.currentTimeMillis()))
                }
                execute<CommandSender> { sender, args, argument ->
                    FindTask(FindTask.Type.ALL, args.argument(-1).toInt(), argument.toInt()).start(sender)
                }
                dynamic("日") {
                    suggestion<CommandSender>(uncheck = true) { _, _ ->
                        listOf(SimpleDateFormat("dd").format(System.currentTimeMillis()))
                    }
                    execute<CommandSender> { sender, args, day ->
                        val year = args.argument(-2).toInt()
                        val month = args.argument(-1).toInt()
                        FindTask(FindTask.Type.ALL, year, month, day.toInt()).start(sender)
                    }
                }
            }
        }
    }

    @CommandBody(optional = true, permission = "minesponsor.find")
    val findUser = subCommand {
        dynamic("玩家") {
            suggestion<CommandSender> { _, _ ->
                onlinePlayers.map { it.name }
            }
            dynamic("年") {
                suggestion<CommandSender>(uncheck = true) { _, _ ->
                    listOf(SimpleDateFormat("yyyy").format(System.currentTimeMillis()))
                }
                execute<CommandSender> { sender, args, argument ->
                    FindTask(FindTask.Type.PLAYER, argument.toInt()).start(
                        sender,
                        Bukkit.getPlayerExact(args.argument(-1))
                    )
                }
                dynamic("月") {
                    suggestion<CommandSender>(uncheck = true) { _, _ ->
                        listOf(SimpleDateFormat("MM").format(System.currentTimeMillis()))
                    }
                    execute<CommandSender> { sender, args, argument ->
                        FindTask(FindTask.Type.PLAYER, args.argument(-1).toInt(), argument.toInt()).start(
                            sender,
                            Bukkit.getPlayerExact(args.argument(-2))
                        )
                    }
                    dynamic("日") {
                        suggestion<CommandSender>(uncheck = true) { _, _ ->
                            listOf(SimpleDateFormat("dd").format(System.currentTimeMillis()))
                        }
                        execute { sender, args, day ->
                            val year = args.argument(-2).toInt()
                            val month = args.argument(-1).toInt()
                            FindTask(FindTask.Type.PLAYER, year, month, day.toInt()).start(
                                sender,
                                Bukkit.getPlayerExact(args.argument(-3))
                            )
                        }
                    }
                }
            }
        }
    }

}