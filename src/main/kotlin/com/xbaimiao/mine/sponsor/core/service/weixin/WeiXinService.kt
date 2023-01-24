package com.xbaimiao.mine.sponsor.core.service.weixin

import com.xbaimiao.mine.sponsor.core.service.Response

interface WeiXinService {

    fun wxQuery(orderId: String): Boolean

    fun wxPay(orderName: String, player: String, amount: Double): Response?

}