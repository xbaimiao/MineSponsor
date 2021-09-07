package com.xbaimiao.minecraft.gPay

import com.xbaimiao.minecraft.gPay.deposit.CallBackImpl
import com.xbaimiao.minecraft.gPay.deposit.Deposit
import com.xbaimiao.minecraft.gPay.deposit.DepositType
import org.bukkit.Bukkit
import org.bukkit.boss.BarColor
import org.bukkit.boss.BarStyle
import org.bukkit.entity.Player
import taboolib.common.platform.function.submit
import taboolib.module.nms.sendMap
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.URL
import java.util.regex.Pattern

fun sendGet(realUrl: URL): String {
    try {
        val result = StringBuilder()
        val conn = realUrl.openConnection()// 打开和URL之间的连接
        conn.doInput = true
        conn.doInput = true
        conn.setRequestProperty("accept", "*/*") // 设置通用的请求属性
        conn.setRequestProperty("connection", "Keep-Alive")
        conn.setRequestProperty("charset", "utf-8")
        conn.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1)")
        conn.connectTimeout = 4000
        conn.connect() // 建立实际的连接
        val input =
            BufferedReader(InputStreamReader(conn.getInputStream(), "UTF-8")) // 定义BufferedReader输入流来读取URL的响应
        var line: String?
        while (input.readLine().also { line = it } != null) {
            result.append(line).append("\n")
        }
        input.close()
        return result.toString()
    } catch (e: Exception) {
        e.printStackTrace()
        return ""
    }
}

/**
 * 创建一个订单
 */
fun Player.createDeposit(num: Double, type: DepositType) {
    val deposit = Deposit(type, num, this, CallBackImpl)
    deposit.ok {
        this@createDeposit.sendTitle(Main.title[0], Main.title[1])
        this@createDeposit.sendMap(deposit.qr())
    }
}

fun String.isNumber(): Boolean {
    if (this == "" || this.startsWith("0.00")) {
        return false
    }
    val pattern = Pattern.compile("[0-9.]*")
    var a = false
    for (c in this) {
        if (c == '.') {
            if (a) {
                return false
            }
            a = true
        }
    }
    val isNum = pattern.matcher(this)
    if (isNum.matches()) {
        val amount = this.toDouble()
        if (amount < Main.minPrice || amount > Main.maxPrice) {
            return false
        }
        return true
    }
    return false
}

class Bar(val string: String, val time: Int)

/**
 * 处理配置文件的commands
 */
fun List<String>.execute(player: Player, deposit: Deposit) {
    //应该给的点券
    val num = deposit.price * Main.exchange
    //主线程执行
    submit {
        for (s in this@execute) {
            val cmd = s.replace("%player_name%", player.name).replace("%pay_money%", num.toInt().toString())
            if (cmd.startsWith("[tell] ")) {
                val tell = cmd.substring(7)
                player.sendMessage(tell)
            }
            if (cmd.startsWith("[bc] ")) {
                val tell = cmd.substring(5)
                for (onlinePlayer in Bukkit.getOnlinePlayers()) {
                    onlinePlayer.sendMessage(tell)
                }
            }
            if (cmd.startsWith("[bossbar] ")) {
                val string = cmd.substring(10)
                val time = string.split("&time&:")[1].toInt()
                val bar = Bar(string.split("&time&:")[0], time)
                val bossBar = Bukkit.createBossBar("", BarColor.BLUE, BarStyle.SOLID)
                for (onlinePlayer in Bukkit.getOnlinePlayers()) {
                    bossBar.addPlayer(onlinePlayer)
                }
                submit(async = true) {
                    for (c in bar.string) {
                        bossBar.setTitle(bossBar.title + c)
                        if (c != '§') {
                            Thread.sleep(100)
                        }
                    }
                }
                submit(delay = bar.time * 20L) {
                    bossBar.removeAll()
                }
            }
            if (cmd.startsWith("[console] ")) {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd.substring(10))
            }
            if (cmd.startsWith("[player] ")) {
                Bukkit.dispatchCommand(player, cmd.substring(9))
            }
            if (cmd.startsWith("[condition] ")) {
                val condition = cmd.substring(12).replace(" ", "")
                if (condition.contains(">")) {
                    val args = condition.split(">")
                    if (args[0].toDouble() > args[1].toDouble()) {
                        continue
                    } else {
                        break
                    }
                }
                if (condition.contains("<")) {
                    val args = condition.split("<")
                    if (args[0].toDouble() < args[1].toDouble()) {
                        continue
                    } else {
                        break
                    }
                }
                if (condition.contains("=")) {
                    val args = condition.split("=")
                    if (args[0].toDouble() == args[1].toDouble()) {
                        continue
                    } else {
                        break
                    }
                }
            }
            if (cmd.startsWith("[points] ")) {
                val reward = cmd.substring(9).toInt()
                for (onlinePlayer in Bukkit.getOnlinePlayers()) {
                    val name = onlinePlayer.name
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "points give $name $reward")
                }
            }

        }
    }

}