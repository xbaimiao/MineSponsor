package com.xbaimiao.mine.sponsor.core

import com.xbaimiao.mine.sponsor.MineSponsor
import taboolib.module.chat.colored

/**
 * @author xbaimiao
 */
object Setting {

    val prefix: String get() = MineSponsor.config.getString("prefix")!!.colored()

    val maxPrice: Double get() = MineSponsor.config.getDouble("maxPrice")

    val minPrice: Double get() = MineSponsor.config.getDouble("minPrice")

    val exchange get() = MineSponsor.config.getInt("setting.exchange")

    val cmds: List<String> get() = MineSponsor.config.getStringList("setting.commands").colored()

    val title get() = MineSponsor.config.getString("title")!!.colored().split("/")

    val signLines get() = MineSponsor.config.getString("gui.sign")!!.colored().split("\n").toTypedArray()

}