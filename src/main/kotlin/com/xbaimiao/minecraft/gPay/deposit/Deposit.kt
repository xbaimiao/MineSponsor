package com.xbaimiao.minecraft.gPay.deposit

import com.google.zxing.BarcodeFormat
import com.google.zxing.common.BitMatrix
import com.google.zxing.qrcode.QRCodeWriter
import com.lly835.bestpay.model.PayResponse
import com.xbaimiao.minecraft.gPay.utils.Trade
import com.xbaimiao.minecraft.gPay.api.events.CreateOrderEvent
import com.xbaimiao.minecraft.gPay.api.events.PaySuccessEvent
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.scheduler.BukkitRunnable
import taboolib.common.platform.function.submit
import taboolib.platform.BukkitPlugin
import taboolib.platform.util.sendLang
import java.awt.image.BufferedImage

class Deposit(
    val type: DepositType,
    val price: Double,
    val player: Player,
    private val callback: Callback,
) {

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
    lateinit var response: PayResponse

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
        submit(async = true) {
            Bukkit.getPluginManager().callEvent(CreateOrderEvent(this@Deposit))
            response = when (type) {
                DepositType.ALIPAY -> Trade.aliNative("余额充值", price)
                DepositType.WX -> Trade.wxNative("余额充值", price)
            }
            if (response.codeUrl == null) {
                player.sendLang("fail")
                return@submit
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
                        Bukkit.getPluginManager().callEvent(PaySuccessEvent(this@Deposit))
                        callback.run(this@Deposit)
                        cancel()
                    }
                }
            }.runTaskTimerAsynchronously(BukkitPlugin.getInstance(), 40, 40)
            load = true
        }
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

    /**
     * 生成的二维码图片
     */
    fun qr(): BufferedImage {
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