package com.xbaimiao.minecraft.gPay.command

import com.xbaimiao.minecraft.gPay.Main
import com.xbaimiao.minecraft.gPay.Main.load
import com.xbaimiao.minecraft.gPay.createDeposit
import com.xbaimiao.minecraft.gPay.deposit.DepositType
import com.xbaimiao.minecraft.gPay.isNumber
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import taboolib.common.platform.command.command
import taboolib.common.platform.command.PermissionDefault
import taboolib.platform.util.sendLang

internal object Commands {

    fun register() {
        command(
            name = "gpayx",
            permissionDefault = PermissionDefault.TRUE
        ) {
            literal("reload", optional = true) {
                execute<CommandSender> { sender, _, _ ->
                    if (!sender.isOp) {
                        sender.sendLang("no-permission")
                        return@execute
                    }
                    load()
                    sender.sendLang("reload")
                }
            }
            literal("w", optional = true) {
                dynamic {
                    execute<Player> { sender, _, argument ->
                        if (argument.isNumber()) {
                            val num = argument.toDouble()
                            sender.createDeposit(num, DepositType.WX)
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
                            sender.createDeposit(num, DepositType.ALIPAY)
                        } else {
                            sender.sendLang("input-error")
                        }
                    }
                }
            }
            literal("auto", optional = true) {
                execute<Player> { sender, _, _ ->
//                    if (Bukkit.getPluginManager().getPlugin("ProtocolLib") == null) {
//                        sender.sendMessage(Main.prefix + "使用此功能须安装插件ProtocolLib")
//                        return@execute
//                    }
                    Auto.open(sender)
                }
            }
            execute<CommandSender> { sender, _, _ ->
                sender.sendMessage(Main.prefix + "/gpayx reload  -> 重载插件")
                sender.sendMessage(Main.prefix + "/gpayx w <数量>  -> 微信充值指定金额")
                sender.sendMessage(Main.prefix + "/gpayx a <数量>  -> 支付宝充值指定金额")
                sender.sendMessage(Main.prefix + "/gpayx auto  -> 打开自助充值页面")
            }
        }
    }


}