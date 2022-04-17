package com.xbaimiao.minecraft.gPay.datacenter.impl

import com.xbaimiao.minecraft.gPay.datacenter.DataCenter
import com.xbaimiao.minecraft.gPay.datacenter.OldDeposit
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
        table.workspace(dataSource) { createTable() }
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