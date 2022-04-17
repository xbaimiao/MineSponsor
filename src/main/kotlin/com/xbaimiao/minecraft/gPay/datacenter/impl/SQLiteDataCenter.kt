package com.xbaimiao.minecraft.gPay.datacenter.impl

import com.xbaimiao.minecraft.gPay.datacenter.DataCenter
import com.xbaimiao.minecraft.gPay.datacenter.OldDeposit
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import taboolib.common.io.newFile
import taboolib.common.platform.function.submit
import taboolib.module.database.ColumnTypeSQLite
import taboolib.module.database.Table
import taboolib.module.database.getHost
import taboolib.platform.BukkitPlugin
import java.util.concurrent.CompletableFuture

/**
 * @author xbaimiao
 */
class SQLiteDataCenter : DataCenter {

    private val file = newFile(BukkitPlugin.getInstance().dataFolder, "datacenter.db")
    private val host = file.getHost()
    private val dataSource = host.createDataSource()

    /**
     * 玩家充值总金额数据库缓存
     */
    private val cache = HashMap<String, Double>()
    private val table = Table("gpayx", host) {
        add("player") {
            type(ColumnTypeSQLite.TEXT)
        }
        add("amount") {
            type(ColumnTypeSQLite.REAL)
        }
        add("time") {
            type(ColumnTypeSQLite.INTEGER)
        }
    }

    init {
        table.workspace(dataSource) { createTable() }.run()
        submit(async = true, period = 40, delay = 40) {
            Bukkit.getOnlinePlayers().asSequence().filter { it.name in cache.keys }.forEach {
                playerAllDeposit(it).thenAcceptAsync { list ->
                    var amount = 0.0
                    list.forEach { deposit ->
                        amount += deposit.amount
                    }
                    cache[it.name] = amount
                }
            }
        }
    }

    override fun allDeposit(): CompletableFuture<List<OldDeposit>> {
        val future = CompletableFuture<List<OldDeposit>>()
        submit(async = true) {
            table.workspace(dataSource) {
                select { }
            }.map {
                OldDeposit(getString("player"), getDouble("amount"), getLong("time"))
            }.also {
                future.complete(it)
            }
        }
        return future
    }

    override fun addDeposit(oldDeposit: OldDeposit) {
        table.workspace(dataSource) {
            this.insert {
                value(oldDeposit.player, oldDeposit.amount, oldDeposit.time)
            }
        }.run()
    }

    override fun playerAmount(player: Player): Double {
        if (!cache.containsKey(player.name)) {
            cache[player.name] = 0.0
        }
        return cache[player.name] ?: 0.0
    }

    override fun playerAllDeposit(player: Player): CompletableFuture<List<OldDeposit>> {
        val future = CompletableFuture<List<OldDeposit>>()
        submit(async = true) {
            table.workspace(dataSource) {
                select {
                    where {
                        "player" eq player.name
                    }
                }
            }.map {
                OldDeposit(getString("player"), getDouble("amount"), getLong("time"))
            }.also {
                future.complete(it)
            }
        }
        return future
    }

}