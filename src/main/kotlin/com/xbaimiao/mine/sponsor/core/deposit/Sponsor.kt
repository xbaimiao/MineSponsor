package com.xbaimiao.mine.sponsor.core.deposit

import com.xbaimiao.mine.sponsor.api.events.SponsorCreateEvent
import com.xbaimiao.mine.sponsor.api.events.SponsorSucceedEvent
import com.xbaimiao.mine.sponsor.core.Setting
import com.xbaimiao.mine.sponsor.core.service.Response
import com.xbaimiao.mine.sponsor.execute
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.scheduler.BukkitRunnable
import taboolib.common.platform.function.info
import taboolib.module.nms.NMSMap
import taboolib.module.nms.sendMap
import taboolib.platform.BukkitPlugin
import taboolib.platform.util.asLangText
import taboolib.platform.util.sendLang
import java.awt.image.BufferedImage
import java.util.*

class Sponsor(
    val type: SponsorType,
    val price: Double,
    var player: Player,
    val desc: String,
    val callback: Sponsor.() -> Unit,
) {

    companion object {

        val cache = HashMap<UUID, Sponsor>()

        // 等待执行的操作
        val wait = HashMap<UUID, Sponsor>()

        /**
         * @param player 玩家
         * @param num 金额
         * @param type 支付类型
         */
        fun create(player: Player, num: Double, type: SponsorType): Sponsor {
            val text = player.asLangText(
                "sponsor", player.name, num * Setting.exchange
            )
            val sponsor = Sponsor(type, num, player, text) {
                player.updateInventory()
                Setting.commands.execute(player, this@Sponsor)
            }
            info("${System.currentTimeMillis()}: ${player.name}创建了一笔订单 金额 -> ${num}元")
            sponsor.ok {
                player.sendLang("pay-start")
                val title = player.asLangText("pay-title").split("/")
                player.sendTitle(title[0], title[1], 20, 30, 20)
                player.sendMap(sponsor.getQrCode(), hand = NMSMap.Hand.MAIN)
            }
            cache[player.uniqueId] = sponsor
            return sponsor
        }

    }

    fun getQrCode(): BufferedImage {
        return response!!.image
    }

    /**
     * 充值网络访问是否完成
     */
    var load = false

    /**
     * 返回的订单数据
     */
    private var response: Response? = null

    /**
     * 充值参数加载完成后执行的方法
     */
    fun ok(func: Sponsor.() -> Unit) {
        object : BukkitRunnable() {
            override fun run() {
                if (load) {
                    func.invoke(this@Sponsor)
                    cancel()
                }
            }
        }.runTaskTimerAsynchronously(BukkitPlugin.getInstance(), 10, 10)
    }

    init {
        Bukkit.getScheduler().runTaskAsynchronously(BukkitPlugin.getInstance(), Runnable {
            Bukkit.getPluginManager().callEvent(SponsorCreateEvent(this@Sponsor))
            response = MineSponsorService.pay(this)
            if (response == null) {
                player.sendLang("fail")
                return@Runnable
            }
            object : BukkitRunnable() {
                var timer = 0
                val maxTime = 600
                var isPay = false
                override fun run() {
                    if (timer++ > maxTime) {
                        cancel()
                    }
                    if (query() && !isPay) {
                        isPay = true
                        Bukkit.getPluginManager().callEvent(SponsorSucceedEvent(this@Sponsor))
                        if (player.isOnline) {
                            callback.invoke(this@Sponsor)
                        } else {
                            wait[player.uniqueId] = this@Sponsor
                        }
                        cancel()
                    }
                }
            }.runTaskTimerAsynchronously(BukkitPlugin.getInstance(), 40, 40)
            load = true
        })
    }

    /**
     * 查询订单支付状态
     */
    fun query(): Boolean {
        return MineSponsorService.query(this)
    }

}