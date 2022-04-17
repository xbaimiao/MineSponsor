package com.xbaimiao.minecraft.gPay.datacenter

import java.text.SimpleDateFormat

/**
 * @author xbaimiao
 */
class OldDeposit(
    // 玩家
    val player: String,
    // 金额
    val amount: Double,
    // 支付时间戳
    val time: Long
) {

    val year: Int get() = SimpleDateFormat("yyyy").format(time).toInt()

    val month: Int get() = SimpleDateFormat("MM").format(time).toInt()

    val day: Int get() = SimpleDateFormat("dd").format(time).toInt()

}