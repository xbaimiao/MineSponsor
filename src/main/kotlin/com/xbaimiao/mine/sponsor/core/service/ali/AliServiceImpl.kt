package com.xbaimiao.mine.sponsor.core.service.ali

import com.lly835.bestpay.config.AliPayConfig
import com.lly835.bestpay.enums.BestPayPlatformEnum
import com.lly835.bestpay.enums.BestPayTypeEnum
import com.lly835.bestpay.enums.OrderStatusEnum
import com.lly835.bestpay.model.OrderQueryRequest
import com.lly835.bestpay.model.PayRequest
import com.lly835.bestpay.service.impl.BestPayServiceImpl
import com.xbaimiao.mine.sponsor.core.service.CreateTradeOn
import com.xbaimiao.mine.sponsor.core.service.Response
import taboolib.library.configuration.ConfigurationSection

class AliServiceImpl(
    private val section: ConfigurationSection
) : AliService, CreateTradeOn {

    private val aliPayConfig: AliPayConfig
    private val beatPay: BestPayServiceImpl

    init {
        aliPayConfig = object : AliPayConfig() {
            init {
                this.appId = section.getString("appid")
                this.privateKey = section.getString("privateKey")
                this.aliPayPublicKey = section.getString("aliPayPublicKey")
            }
        }
        beatPay = BestPayServiceImpl()
        beatPay.setAliPayConfig(aliPayConfig)
    }

    override fun aliNative(orderName: String, amount: Double): Response {
        val request = PayRequest()
        val orderId = this.createOutTradeNo()
        request.payTypeEnum = BestPayTypeEnum.ALIPAY_QRCODE
        request.orderId = orderId
        request.orderName = orderName
        request.orderAmount = amount
        val response = beatPay.pay(request)
        response.orderId = orderId
        response.outTradeNo = orderId
        response.orderAmount = amount
        return Response(orderId, response.codeUrl, amount)
    }

    override fun aliQuery(orderId: String): Boolean {
        val request = OrderQueryRequest()
        request.platformEnum = BestPayPlatformEnum.ALIPAY
        request.orderId = orderId
        val response = beatPay.query(request) ?: return false
        return response.orderStatusEnum == OrderStatusEnum.SUCCESS
    }

}