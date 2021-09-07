package com.xbaimiao.minecraft.gPay.kit

import com.xbaimiao.minecraft.gPay.deposit.Deposit
import com.xbaimiao.minecraft.gPay.execute
import org.bukkit.entity.Player

class Kit(
    val name: String,
    val enable: Boolean,
    val maxAmount: Int,
    val premise: Int,
    val commands: List<String>
) {

    fun execute(player: Player, deposit: Deposit) {
        commands.execute(player, deposit)
    }

}