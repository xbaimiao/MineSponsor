package com.xbaimiao.minecraft.gPay.deposit

import com.xbaimiao.minecraft.gPay.Main
import com.xbaimiao.minecraft.gPay.execute
import com.xbaimiao.minecraft.gPay.kit.KitListener
import com.xbaimiao.minecraft.gPay.utils.Log

object CallBackImpl : Callback {

    override fun run(deposit: Deposit) {
        val player = deposit.player
        player.updateInventory()
        Main.cmds.execute(player, deposit)
        KitListener.run(deposit)
        Log.add(deposit)
    }

}