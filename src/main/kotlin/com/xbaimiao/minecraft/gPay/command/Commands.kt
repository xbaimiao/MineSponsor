package com.xbaimiao.minecraft.gPay.command

import com.xbaimiao.minecraft.gPay.GPayX
import com.xbaimiao.minecraft.gPay.GPayX.load
import com.xbaimiao.minecraft.gPay.deposit.Deposit
import com.xbaimiao.minecraft.gPay.deposit.DepositType
import com.xbaimiao.minecraft.gPay.isNumber
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import taboolib.common.platform.command.PermissionDefault
import taboolib.common.platform.command.command
import taboolib.platform.util.sendLang

internal object Commands {

    fun register() {
        command(
            name = "gpayx",
            permissionDefault = PermissionDefault.TRUE,
            aliases = arrayListOf("pp")
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
                    sender.sendMessage(GPayX.prefix + "/gpayx reload  -> 重载插件")
                    sender.sendMessage(GPayX.prefix + "/gpayx w <数量>  -> 微信充值指定金额")
                    sender.sendMessage(GPayX.prefix + "/gpayx a <数量>  -> 支付宝充值指定金额")
                    sender.sendMessage(GPayX.prefix + "/gpayx find <年> <月(可选)> <日(可选)>  -> 列出指定的充值记录")
                    sender.sendMessage(GPayX.prefix + "/gpayx -> 打开自助充值页面")
                }
            }
            execute<Player> { sender, _, _ ->
                Auto.open(sender)
            }
        }
    }


}