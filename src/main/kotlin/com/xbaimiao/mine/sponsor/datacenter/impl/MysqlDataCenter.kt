package com.xbaimiao.mine.sponsor.datacenter.impl

import com.xbaimiao.mine.sponsor.MineSponsor
import com.xbaimiao.mine.sponsor.datacenter.DataCenter
import com.xbaimiao.mine.sponsor.datacenter.OldDeposit
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import taboolib.common.platform.function.submit
import taboolib.module.database.ColumnTypeSQL
import taboolib.module.database.Table
import taboolib.module.database.getHost
import java.util.concurrent.CompletableFuture

class MysqlDataCenter : DataCenter {

    private val host = MineSponsor.config.getHost("mysql")
    private val dataSource = host.createDataSource()

    /**
     * 玩家充值总金额数据库缓存
     */
    private val cache = HashMap<String, Double>()
    private val table = Table("gpayx", host) {
        add("player") {
            type(ColumnTypeSQL.VARCHAR, 32)
        }
        add("amount") {
            type(ColumnTypeSQL.INT, 32)
        }
        add("time") {
            type(ColumnTypeSQL.BIGINT, 100)
        }
    }

    init {
        table.workspace(dataSource) { createTable() }.run()
        submit(async = true, period = 40, delay = 40) {
            Bukkit.getOnlinePlayers().forEach {
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