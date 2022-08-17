package com.xbaimiao.mine.sponsor.core.command

import com.xbaimiao.mine.sponsor.MineSponsor
import com.xbaimiao.mine.sponsor.core.Check.isNumber
import com.xbaimiao.mine.sponsor.core.deposit.Sponsor
import com.xbaimiao.mine.sponsor.core.deposit.SponsorType
import com.xbaimiao.mine.sponsor.datacenter.FindTask
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import taboolib.common.platform.command.*
import taboolib.expansion.createHelper
import taboolib.platform.util.sendLang
import java.text.SimpleDateFormat

@CommandHeader("minesponsor", aliases = ["gpayx", "minepay", "pp"], permissionDefault = PermissionDefault.TRUE)
internal object Commands {

    @CommandBody
    val main = mainCommand {
        createHelper()
    }

    @CommandBody(optional = true)
    val open = subCommand {
        execute<Player> { sender, _, _ ->
            AutoSponsor.open(sender)
        }
    }

    @CommandBody(optional = true, permission = "minesponsor.reload")
    val reload = subCommand {
        execute<CommandSender> { sender, _, _ ->
            MineSponsor.load()
            sender.sendLang("reload")
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

//    fun register() {
//        command(
//            name = "gpayx", permissionDefault = PermissionDefault.TRUE, aliases = arrayListOf("pp")
//        ) {
//            literal("finduser", optional = true, permission = "gpayx.finduser") {
//                dynamic {
//                    suggestion<CommandSender>(uncheck = true) { _, _ ->
//                        listOf(SimpleDateFormat("yyyy").format(System.currentTimeMillis()))
//                    }
//                    execute<CommandSender> { sender, _, argument ->
//                        FindTask(FindTask.Type.PLAYER, argument.toInt()).start(sender)
//                    }
//                    dynamic {
//                        suggestion<CommandSender>(uncheck = true) { _, _ ->
//                            listOf(SimpleDateFormat("MM").format(System.currentTimeMillis()))
//                        }
//                        execute<CommandSender> { sender, args, argument ->
//                            FindTask(
//                                FindTask.Type.PLAYER, args.argument(-1).toInt(), argument.toInt()
//                            ).start(sender)
//                        }
//                        dynamic {
//                            suggestion<CommandSender>(uncheck = true) { _, _ ->
//                                listOf(SimpleDateFormat("dd").format(System.currentTimeMillis()))
//                            }
//                            execute<CommandSender> { sender, args, day ->
//                                val year = args.argument(-2).toInt()
//                                val month = args.argument(-1).toInt()
//                                FindTask(FindTask.Type.PLAYER, year, month, day.toInt()).start(sender)
//                            }
//                        }
//                    }
//                }
//            }
//        }
//    }
}