package com.xbaimiao.mine.sponsor.core.deposit

import com.google.zxing.BarcodeFormat
import com.google.zxing.common.BitMatrix
import com.google.zxing.qrcode.QRCodeWriter
import com.xbaimiao.mine.sponsor.MineSponsor
import com.xbaimiao.mine.sponsor.api.events.SponsorCreateEvent
import com.xbaimiao.mine.sponsor.api.events.SponsorSucceedEvent
import com.xbaimiao.mine.sponsor.core.Setting
import com.xbaimiao.mine.sponsor.core.service.Response
import com.xbaimiao.mine.sponsor.datacenter.OldDeposit
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
    val text: String,
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
                MineSponsor.dataCenter.addDeposit(this.toOld())
                Setting.cmds.execute(player, this@Sponsor)
            }
            info("${System.currentTimeMillis()}: ${player.name}创建了一笔订单 金额 -> ${num}元")
            sponsor.ok {
                player.sendLang("pay-start")
                player.sendTitle(Setting.title[0], Setting.title[1], 20, 30, 20)
                player.sendMap(sponsor.getQR(), hand = NMSMap.Hand.MAIN)
            }
            cache[player.uniqueId] = sponsor
            return sponsor
        }

    }

    /**
     * 充值网络访问是否完成
     */
    var load = false

    /**
     * 订单号
     */
    lateinit var orderId: String

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
            response = when (type) {
                SponsorType.ALIPAY -> MineSponsorService.aliNative(text, price)
                SponsorType.WX -> MineSponsorService.wxNative(text, player, price)
            }
            if (response == null) {
                player.sendLang("fail")
                return@Runnable
            }
            orderId = response!!.orderId
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
        return when (type) {
            SponsorType.ALIPAY -> MineSponsorService.aliQuery(orderId)
            SponsorType.WX -> MineSponsorService.wxQuery(orderId)
        }
    }

    fun toOld(): OldDeposit {
        return OldDeposit(player.name, price, System.currentTimeMillis())
    }

    /**
     * 生成的二维码图片
     */
    fun getQR(): BufferedImage {
        val qrCodeWriter1 = QRCodeWriter()
        val bitMatrix1: BitMatrix = qrCodeWriter1.encode(this.response!!.codeUrl, BarcodeFormat.QR_CODE, 128, 128)
        val width = bitMatrix1.width
        val height = bitMatrix1.height
        val image = BufferedImage(width, height, BufferedImage.TYPE_INT_RGB)
        for (i in 0 until width) {
            for (j in 0 until height) {
                image.setRGB(i, j, if (bitMatrix1[i, j]) -0x1000000 else 0XFFFFFF)
            }
        }
        return image
    }

}