package com.xbaimiao.minecraft.gPay.deposit

import com.google.zxing.BarcodeFormat
import com.google.zxing.common.BitMatrix
import com.google.zxing.qrcode.QRCodeWriter
import com.lly835.bestpay.model.PayResponse
import com.xbaimiao.minecraft.gPay.GPayX
import com.xbaimiao.minecraft.gPay.datacenter.OldDeposit
import com.xbaimiao.minecraft.gPay.events.OrderCreateEvent
import com.xbaimiao.minecraft.gPay.events.OrderPaymentEvent
import com.xbaimiao.minecraft.gPay.execute
import com.xbaimiao.minecraft.gPay.kit.KitListener
import com.xbaimiao.minecraft.gPay.mirai.Bot
import com.xbaimiao.minecraft.gPay.utils.Trade
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.scheduler.BukkitRunnable
import taboolib.common.platform.function.submit
import taboolib.module.nms.NMSMap
import taboolib.module.nms.sendMap
import taboolib.platform.BukkitPlugin
import taboolib.platform.util.sendLang
import java.awt.image.BufferedImage

class Deposit(
    val type: DepositType,
    val price: Double,
    val player: Player,
    private val callback: Deposit.() -> Unit,
) {

    companion object {

        /**
         * @param player 玩家
         * @param num 金额
         * @param type 支付类型
         */
        fun create(player: Player, num: Double, type: DepositType): Deposit {
            val deposit = Deposit(type, num, player) {
                player.updateInventory()
                GPayX.dataCenter.addDeposit(this.toOld())
                GPayX.cmds.execute(player, this@Deposit)
                KitListener.run(this@Deposit)
                submit(async = true) {
                    GPayX.logger.println("${System.currentTimeMillis()}: ${this@Deposit.player.name}充值点券 -> ${this@Deposit.price} 元，支付方式 -> ${this@Deposit.type}")
                    if (GPayX.hasBot) {
                        Bot.run(this@Deposit)
                    }
                }
            }
            GPayX.logger.println("${System.currentTimeMillis()}: ${player.name}创建了一笔订单 金额 -> ${num}元")
            deposit.ok {
                player.sendTitle(GPayX.title[0], GPayX.title[1], 20, 30, 20)
                player.sendMap(deposit.getQR(), hand = NMSMap.Hand.OFF)
            }
            return deposit
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
    private lateinit var response: PayResponse

    /**
     * 充值参数加载完成后执行的方法
     */
    fun ok(func: Deposit.() -> Unit) {
        object : BukkitRunnable() {
            override fun run() {
                if (load) {
                    func.invoke(this@Deposit)
                    cancel()
                }
            }
        }.runTaskTimerAsynchronously(BukkitPlugin.getInstance(), 10, 10)
    }

    init {
        Bukkit.getScheduler().runTaskAsynchronously(BukkitPlugin.getInstance(), Runnable {
            Bukkit.getPluginManager().callEvent(OrderCreateEvent(this@Deposit))
            response = when (type) {
                DepositType.ALIPAY -> Trade.aliNative("余额充值", price)
                DepositType.WX -> Trade.wxNative("余额充值", price)
            }
            if (response.codeUrl == null) {
                player.sendLang("fail")
                return@Runnable
            }
            orderId = response.orderId
            object : BukkitRunnable() {
                var timer = 0
                val maxTime = 300
                var isPay = false
                override fun run() {
                    if (timer++ > maxTime) {
                        cancel()
                    }
                    if (query() && !isPay) {
                        isPay = true
                        Bukkit.getPluginManager().callEvent(OrderPaymentEvent(this@Deposit))
                        callback.invoke(this@Deposit)
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
            DepositType.ALIPAY -> Trade.aliQuery(orderId)
            DepositType.WX -> Trade.wxQuery(orderId)
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
        val bitMatrix1: BitMatrix = qrCodeWriter1.encode(this.response.codeUrl, BarcodeFormat.QR_CODE, 128, 128)
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