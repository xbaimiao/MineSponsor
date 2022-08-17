package com.xbaimiao.mine.sponsor.datacenter

import com.xbaimiao.mine.sponsor.MineSponsor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import taboolib.module.chat.colored
import java.util.concurrent.CompletableFuture

/**
 * @author xbaimiao
 * 查询充值记录
 */
class FindTask(private val type: Type, private val year: Int, private val month: Int = 0, private val day: Int = 0) {

    enum class Type {
        ALL, PLAYER
    }

    fun start(sender: CommandSender, player: Player? = null) {
        if (player == null && type == Type.PLAYER) {
            error("在运行FindTask时,未传入参数 player")
        }
        val future: CompletableFuture<List<OldDeposit>> = when (type) {
            Type.ALL -> {
                MineSponsor.dataCenter.allDeposit()
            }

            Type.PLAYER -> {
                MineSponsor.dataCenter.playerAllDeposit(player!!)
            }
        }
        future.thenAcceptAsync {
            Thread {
                var total = 0.0
                var num = 0
                it.asSequence()
                    .filter { oldDeposit -> filter(oldDeposit) }
                    .forEach { oldDeposit ->
                        total += oldDeposit.amount
                        num++
                    }
                sender.sendMessage("&7在 &e${year}年${month}月${day}日 &7总计金额: &e$total &7总计笔数: &e$num".colored())
            }.start()
        }
    }

    /**
     * 筛选匹配日期的订单
     */
    private fun filter(oldDeposit: OldDeposit): Boolean {
        if (month == 0 && day == 0) {
            return oldDeposit.year == year
        }
        if (day == 0) {
            return oldDeposit.year == year && oldDeposit.month == month
        }
        return oldDeposit.year == year && oldDeposit.month == month && oldDeposit.day == day
    }

}