package com.xbaimiao.mine.sponsor.core.deposit

import com.lly835.bestpay.config.AliPayConfig
import com.lly835.bestpay.config.WxPayConfig
import com.lly835.bestpay.enums.BestPayPlatformEnum
import com.lly835.bestpay.enums.BestPayTypeEnum
import com.lly835.bestpay.enums.OrderStatusEnum
import com.lly835.bestpay.model.OrderQueryRequest
import com.lly835.bestpay.model.PayRequest
import com.lly835.bestpay.model.PayResponse
import com.lly835.bestpay.service.impl.BestPayServiceImpl
import com.xbaimiao.mine.sponsor.MineSponsor
import java.util.*

object MineSponsorService : BestPayServiceImpl() {

    init {
        reload()
    }

    fun reload() {
        val wxPayConfig = object : WxPayConfig() {
            init {
                val section = MineSponsor.key.getConfigurationSection("pay_wx")!!
                appId = section.getString("appid")
                mchId = section.getString("mchid")
                mchKey = section.getString("mchKey")
                appSecret = section.getString("appSecret")
                notifyUrl = "http://www.baidu.com"
            }
        }
        val aliPayConfig = object : AliPayConfig() {
            init {
                val section = MineSponsor.key.getConfigurationSection("pay_ali")!!
                appId = section.getString("appid")
                privateKey = section.getString("privateKey")
                aliPayPublicKey = section.getString("aliPayPublicKey")
            }
        }
        this.setWxPayConfig(wxPayConfig)
        this.setAliPayConfig(aliPayConfig)
    }

    @JvmStatic
    fun createOutTradeNo(): String {
        val calendar = Calendar.getInstance()
        return "${calendar.get(Calendar.YEAR)}${calendar.get(Calendar.MONTH) + 1}${calendar.get(Calendar.DATE)}" + (System.currentTimeMillis() + (Math.random() * 10000000L).toLong()).toString()
    }

    /**
     * 调起微信支付
     */
    fun wxNative(orderName: String, amount: Double): PayResponse {
        val request = PayRequest()
        val orderId = createOutTradeNo()
        request.payTypeEnum = BestPayTypeEnum.WXPAY_NATIVE
        request.orderId = orderId
        request.orderName = orderName
        request.orderAmount = amount
        val response = this.pay(request)
        response.orderId = orderId
        response.outTradeNo = orderId
        response.orderAmount = amount
        return response
    }

    /**
     * 调起支付宝支付
     */
    fun aliNative(orderName: String, amount: Double): PayResponse {
        val request = PayRequest()
        val orderId = createOutTradeNo()
        request.payTypeEnum = BestPayTypeEnum.ALIPAY_QRCODE
        request.orderId = orderId
        request.orderName = orderName
        request.orderAmount = amount
        val response = this.pay(request)
        response.orderId = orderId
        response.outTradeNo = orderId
        response.orderAmount = amount
        return response
    }

    /**
     * 查询支付宝订单
     */
    fun aliQuery(orderId: String): Boolean {
        val request = OrderQueryRequest()
        request.platformEnum = BestPayPlatformEnum.ALIPAY
        request.orderId = orderId
        val response = this.query(request) ?: return false
        return response.orderStatusEnum == OrderStatusEnum.SUCCESS
    }

    /**
     * 查询微信订单
     */
    fun wxQuery(orderId: String): Boolean {
        val request = OrderQueryRequest()
        request.platformEnum = BestPayPlatformEnum.WX
        request.orderId = orderId
        val response = this.query(request) ?: return false
        return response.orderStatusEnum == OrderStatusEnum.SUCCESS
    }


}