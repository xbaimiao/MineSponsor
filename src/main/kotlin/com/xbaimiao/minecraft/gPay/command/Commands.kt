package com.xbaimiao.minecraft.gPay.command

import com.xbaimiao.minecraft.gPay.GPayX
import com.xbaimiao.minecraft.gPay.GPayX.load
import com.xbaimiao.minecraft.gPay.core.Config
import com.xbaimiao.minecraft.gPay.datacenter.FindTask
import com.xbaimiao.minecraft.gPay.deposit.Deposit
import com.xbaimiao.minecraft.gPay.deposit.DepositType
import com.xbaimiao.minecraft.gPay.isNumber
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import taboolib.common.platform.command.PermissionDefault
import taboolib.common.platform.command.command
import taboolib.platform.util.sendLang
import java.text.SimpleDateFormat

internal object Commands {

    fun register() {
        command(
            name = "gpayx", permissionDefault = PermissionDefault.TRUE, aliases = arrayListOf("pp")
        ) {
            literal("find", optional = true, permission = "gpayx.find") {
                dynamic {
                    suggestion<CommandSender>(uncheck = true) { _, _ ->
                        listOf(SimpleDateFormat("yyyy").format(System.currentTimeMillis()))
                    }
                    execute<CommandSender> { sender, _, argument ->
                        FindTask(FindTask.Type.ALL, argument.toInt()).start(sender)
                    }
                    dynamic {
                        suggestion<CommandSender>(uncheck = true) { _, _ ->
                            listOf(SimpleDateFormat("MM").format(System.currentTimeMillis()))
                        }
                        execute<CommandSender> { sender, args, argument ->
                            FindTask(FindTask.Type.ALL, args.argument(-1).toInt(), argument.toInt()).start(sender)
                        }
                        dynamic {
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
            literal("finduser", optional = true, permission = "gpayx.finduser") {
                dynamic {
                    suggestion<CommandSender>(uncheck = true) { _, _ ->
                        listOf(SimpleDateFormat("yyyy").format(System.currentTimeMillis()))
                    }
                    execute<CommandSender> { sender, _, argument ->
                        FindTask(FindTask.Type.PLAYER, argument.toInt()).start(sender)
                    }
                    dynamic {
                        suggestion<CommandSender>(uncheck = true) { _, _ ->
                            listOf(SimpleDateFormat("MM").format(System.currentTimeMillis()))
                        }
                        execute<CommandSender> { sender, args, argument ->
                            FindTask(
                                FindTask.Type.PLAYER, args.argument(-1).toInt(), argument.toInt()
                            ).start(sender)
                        }
                        dynamic {
                            suggestion<CommandSender>(uncheck = true) { _, _ ->
                                listOf(SimpleDateFormat("dd").format(System.currentTimeMillis()))
                            }
                            execute<CommandSender> { sender, args, day ->
                                val year = args.argument(-2).toInt()
                                val month = args.argument(-1).toInt()
                                FindTask(FindTask.Type.PLAYER, year, month, day.toInt()).start(sender)
                            }
                        }
                    }
                }

            }
            literal("reload", optional = true, permission = "gpayx.reload") {
                execute<CommandSender> { sender, _, _ ->
                    load()
                    sender.sendLang("reload")
                }
            }
            literal("w", optional = true) {
                dynamic {
                    execute<Player> { sender, _, argument ->
                        if (argument.isNumber()) {
                            val num = argument.toDouble()
                            Deposit.create(sender, num, DepositType.WX)
                        } else {
                            sender.sendLang("input-error")
                        }
                    }
                }
            }
            literal("a", optional = true) {
                dynamic {
                    execute<Player> { sender, _, argument ->
                        if (argument.isNumber()) {
                            val num = argument.toDouble()
                            Deposit.create(sender, num, DepositType.ALIPAY)
                        } else {
                            sender.sendLang("input-error")
                        }
                    }
                }
            }
            literal("help", optional = true) {
                execute<CommandSender> { sender, _, _ ->
                    sender.sendMessage(Config.prefix + "/gpayx reload  -> 重载插件")
                    sender.sendMessage(Config.prefix + "/gpayx w <数量>  -> 微信充值指定金额")
                    sender.sendMessage(Config.prefix + "/gpayx a <数量>  -> 支付宝充值指定金额")
                    sender.sendMessage(Config.prefix + "/gpayx find <年> <月(可选)> <日(可选)>  -> 列出指定的充值记录")
                    sender.sendMessage(Config.prefix + "/gpayx finduser <玩家ID(需在线)> <年> <月(可选)> <日(可选)>  -> 列出指定的充值记录")
                    sender.sendMessage(Config.prefix + "/gpayx -> 打开自助充值页面")
                }
            }
            execute<Player> { sender, _, _ ->
                Auto.open(sender)
            }
        }
    }
}