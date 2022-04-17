package com.xbaimiao.minecraft.gPay.datacenter

import org.bukkit.entity.Player
import java.util.concurrent.CompletableFuture

/**
 * @author xbaimiao
 * 支付信息 数据库
 */
interface DataCenter {

    /**
     * 所有订单记录
     */
    fun allDeposit(): CompletableFuture<List<OldDeposit>>

    /**
     * 玩家的所有订单记录
     */
    fun playerAllDeposit(player: Player): CompletableFuture<List<OldDeposit>>

    /**
     * 添加一个订单记录
     */
    fun addDeposit(oldDeposit: OldDeposit)

    /**
     * 玩家充值总金额
     */
    fun playerAmount(player: Player): Double

}