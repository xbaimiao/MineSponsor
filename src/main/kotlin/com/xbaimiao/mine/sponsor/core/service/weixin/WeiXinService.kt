package com.xbaimiao.mine.sponsor.core.service.weixin

import com.xbaimiao.mine.sponsor.core.service.Response

interface WeiXinService {

    fun wxQuery(orderId: String): Boolean

    fun wxNative(orderName: String, amount: Double): Response

}