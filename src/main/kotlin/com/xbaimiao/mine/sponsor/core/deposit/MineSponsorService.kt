package com.xbaimiao.mine.sponsor.core.deposit

import com.xbaimiao.mine.sponsor.MineSponsor
import com.xbaimiao.mine.sponsor.core.service.Response
import com.xbaimiao.mine.sponsor.core.service.ali.AliService
import com.xbaimiao.mine.sponsor.core.service.ali.AliServiceImpl
import com.xbaimiao.mine.sponsor.core.service.weixin.WeiXinService
import com.xbaimiao.mine.sponsor.core.service.weixin.WeiXinServiceImpl
import com.xbaimiao.mine.sponsor.core.service.weixin.WeiXinServiceJsAPI

object MineSponsorService {

    private lateinit var aliService: AliService
    private lateinit var weiXinService: WeiXinService

    init {
        reload()
    }

    fun reload() {
        MineSponsor.key.reload()
        aliService = AliServiceImpl(MineSponsor.key.getConfigurationSection("pay_ali")!!)
        weiXinService = if (MineSponsor.key.getBoolean("js_api.enable")) {
            WeiXinServiceJsAPI(MineSponsor.key.getString("js_api.url")!!)
        } else {
            WeiXinServiceImpl(MineSponsor.key.getConfigurationSection("pay_wx")!!)
        }
    }


    /**
     * 调起微信支付
     */
    fun wxNative(orderName: String, amount: Double): Response? {
        return weiXinService.wxNative(orderName, "test", amount)
    }

    /**
     * 调起支付宝支付
     */
    fun aliNative(orderName: String, amount: Double): Response {
        return aliService.aliNative(orderName, amount)
    }

    /**
     * 查询支付宝订单
     */
    fun aliQuery(orderId: String): Boolean {
        return aliService.aliQuery(orderId)
    }

    /**
     * 查询微信订单
     */
    fun wxQuery(orderId: String): Boolean {
        return weiXinService.wxQuery(orderId)
    }


}