package com.xbaimiao.mine.sponsor.core.service.ali

import com.xbaimiao.mine.sponsor.core.service.Response

interface AliService {

    fun aliNative(orderName: String, amount: Double): Response

    fun aliQuery(orderId: String): Boolean

}