package com.xbaimiao.minecraft.gPay.core

import com.xbaimiao.minecraft.gPay.GPayX
import taboolib.module.chat.colored

/**
 * @author xbaimiao
 */
object Config {

    val prefix: String get() = GPayX.config.getString("prefix")!!.colored()

    val maxPrice: Double get() = GPayX.config.getDouble("maxPrice")

    val minPrice: Double get() = GPayX.config.getDouble("minPrice")

    val exchange get() = GPayX.config.getInt("setting.exchange")

    val cmds: List<String> get() = GPayX.config.getStringList("setting.commands").colored()

    val title get() = GPayX.config.getString("title")!!.colored().split("/")

    val signLines get() = GPayX.config.getString("gui.sign")!!.colored().split("\n").toTypedArray()

}