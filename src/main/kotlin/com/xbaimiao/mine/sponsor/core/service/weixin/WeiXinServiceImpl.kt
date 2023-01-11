package com.xbaimiao.mine.sponsor.core.service.weixin

import com.wechat.pay.java.core.RSAConfig
import com.wechat.pay.java.service.payments.nativepay.NativePayService
import com.wechat.pay.java.service.payments.nativepay.model.Amount
import com.wechat.pay.java.service.payments.nativepay.model.PrepayRequest
import com.wechat.pay.java.service.payments.nativepay.model.QueryOrderByOutTradeNoRequest
import com.xbaimiao.mine.sponsor.core.service.CreateTradeOn
import com.xbaimiao.mine.sponsor.core.service.Response
import taboolib.library.configuration.ConfigurationSection
import taboolib.platform.BukkitPlugin
import java.io.File

class WeiXinServiceImpl(
    private val mchid: String,
    private val appId: String,
    privateKeyPath: String,
    wechatPayCertificatePath: String,
    merchantSerialNumber: String
) : WeiXinService, CreateTradeOn {

    constructor(config: ConfigurationSection) : this(
        config.getString("mchid")!!,
        config.getString("appid")!!,
        BukkitPlugin.getInstance().dataFolder.path + File.separator + config.getString("privateKeyPath")!!,
        BukkitPlugin.getInstance().dataFolder.path + File.separator + config.getString("wechatPayCertificatePath")!!,
        config.getString("merchantSerialNumber")!!
    )

    private val service: NativePayService

    init {
        // 初始化商户配置
        val config = RSAConfig.Builder().merchantId(mchid).privateKeyFromPath(privateKeyPath)
            .merchantSerialNumber(merchantSerialNumber).wechatPayCertificatesFromPath(wechatPayCertificatePath).build()

        // 初始化服务
        service = NativePayService.Builder().config(config).build()
    }


    override fun wxQuery(orderId: String): Boolean {
        val request = QueryOrderByOutTradeNoRequest()
        request.outTradeNo = orderId
        request.mchid = mchid
        val response = service.queryOrderByOutTradeNo(request)
        return response.tradeState == com.wechat.pay.java.service.payments.model.Transaction.TradeStateEnum.SUCCESS
    }

    override fun wxNative(orderName: String, amount: Double): Response {
        val request = PrepayRequest()
        request.appid = appId
        request.mchid = mchid
        request.description = orderName
        val money = Amount()
        money.currency = "CNY"
        money.total = (amount * 100.0).toInt()
        request.amount = money
        request.outTradeNo = createOutTradeNo()
        request.notifyUrl = "https://www.baidu.com"
        val result = service.prepay(request)

        return Response(
            orderId = request.outTradeNo, codeUrl = result.codeUrl, money = amount
        )
    }

}