package com.xbaimiao.minecraft.gPay.kit

import com.xbaimiao.minecraft.gPay.Main
import com.xbaimiao.minecraft.gPay.deposit.Deposit
import com.xbaimiao.minecraft.gPay.utils.SQLite
import org.bukkit.entity.Player
import taboolib.platform.BukkitPlugin
import java.io.File

object KitListener {

    val tabName = "KITDATA"

    val sqlite = SQLite(File(BukkitPlugin.getInstance().dataFolder, "kit.db")).also {
        val sql = "CREATE TABLE IF NOT EXISTS $tabName " +
                "(" +
                "KIT VARCHAR(50) PRIMARY KEY NOT NULL," +
                "AMOUNT BIGINT(20) NOT NULL" +
                ");"
        it.statement.executeUpdate(sql)
    }

    /**
     * 获取玩家领取了这个礼包多少次
     */
    private fun Player.getAmount(kit: Kit): Int {
        val result =
            sqlite.statement.executeQuery("SELECT * FROM $tabName WHERE KIT='${kit.name}&${this.name}';")
        if (result.next()) {
            return result.getInt("AMOUNT")
        }
        return 0
    }

    /**
     * 设置玩家领取了多少次
     */
    private fun Player.setAmount(kit: Kit, int: Int) {
        val result =
            sqlite.statement.executeQuery("SELECT * FROM $tabName WHERE KIT='${kit.name}&${this.name}';")
        if (result.next()) {
            sqlite.statement.executeUpdate("UPDATE $tabName SET AMOUNT=${int} WHERE KIT='${kit.name}&${this.name}'")
        }else{
            sqlite.statement.executeUpdate("INSERT INTO $tabName VALUES ('${kit.name}&${this.name}',$int)")
        }
    }

    fun run(deposit: Deposit) {
        for (kit in Main.kitList) {
            if (!kit.enable) {
                continue
            }
            val num = deposit.price * Main.exchange
            if (num >= kit.premise) {
                val player = deposit.player
                if (kit.maxAmount > player.getAmount(kit)) {
                    kit.execute(player, deposit)
                    player.setAmount(kit, player.getAmount(kit) + 1)
                }
            }
        }
    }

}