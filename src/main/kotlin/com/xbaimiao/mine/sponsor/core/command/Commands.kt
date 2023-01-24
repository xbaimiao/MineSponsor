package com.xbaimiao.mine.sponsor.core.command

import com.xbaimiao.mine.sponsor.MineSponsor
import com.xbaimiao.mine.sponsor.core.deposit.Sponsor
import com.xbaimiao.mine.sponsor.core.deposit.SponsorType
import com.xbaimiao.mine.sponsor.isNumber
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import taboolib.common.platform.command.*
import taboolib.expansion.createHelper
import taboolib.platform.util.sendLang

@CommandHeader("minesponsor", aliases = ["gpayx", "minepay", "pp"], permissionDefault = PermissionDefault.TRUE)
internal object Commands {

    @CommandBody
    val main = mainCommand {
        createHelper()
    }

    @CommandBody(optional = true)
    val open = subCommand {
        execute { sender: Player, _, _ ->
            AutoSponsor.open(sender, AutoSponsor.AutoType.POINTS)
        }
    }

    @CommandBody(optional = true, permission = "minesponsor.reload")
    val reload = subCommand {
        execute { sender: CommandSender, _, _ ->
            MineSponsor.load()
            sender.sendLang("reload")
        }
    }

    @CommandBody(aliases = ["w", "wechat", "wx"], optional = true)
    val wechat = subCommand {
        dynamic {
            execute { sender: Player, _, argument ->
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
            execute { sender: Player, _, argument ->
                if (argument.isNumber()) {
                    val num = argument.toDouble()
                    Sponsor.create(sender, num, SponsorType.ALIPAY)
                } else {
                    sender.sendLang("input-error")
                }
            }
        }
    }

}