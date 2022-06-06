package com.xbaimiao.minecraft.gPay.datacenter

import com.xbaimiao.minecraft.gPay.GPayX
import com.xbaimiao.minecraft.gPay.logger.FileLogger
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import taboolib.common.io.newFile
import taboolib.module.chat.colored
import taboolib.platform.BukkitPlugin
import java.io.File
import java.util.concurrent.CompletableFuture

/**
 * @author xbaimiao
 * 查询充值记录
 */
class FindTask(val type: Type, val year: Int, val month: Int = 0, val day: Int = 0) {

    enum class Type {
        ALL, PLAYER
    }

    fun start(sender: CommandSender, player: Player? = null) {
        if (player == null && type == Type.PLAYER) {
            throw NullPointerException("在运行FindTask时,未传入参数 player")
        }
        val future: CompletableFuture<List<OldDeposit>> = when (type) {
            Type.ALL -> {
                GPayX.dataCenter.allDeposit()
            }
            Type.PLAYER -> {
                GPayX.dataCenter.playerAllDeposit(player!!)
            }
        }
        future.thenAcceptAsync {
            Thread {
                val fileName = "logs${File.separator}$year-$month-$day.log"
                val file = newFile(BukkitPlugin.getInstance().dataFolder, fileName)
                val logger = FileLogger(file)
                var total = 0.0
                var num = 0
                it.asSequence()
                    .filter { oldDeposit -> filter(oldDeposit) }
                    .forEach { oldDeposit ->
                        total += oldDeposit.amount
                        num++
                        logger.println("${oldDeposit.year}-${oldDeposit.month}-${oldDeposit.day}: 玩家 -> ${oldDeposit.player} 金额 -> ${oldDeposit.amount}")
                    }
                logger.writer.close()
                sender.sendMessage("&7在 &e${year}年${month}月${day}日 &7总计金额: &e$total &7总计笔数: &e$num".colored())
                if (num != 0) {
                    sender.sendMessage("&7充值详情请查看 &e/plugins/GPayX/logs/$fileName".colored().replace("\\", "/"))
                } else {
                    file.delete()
                }
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