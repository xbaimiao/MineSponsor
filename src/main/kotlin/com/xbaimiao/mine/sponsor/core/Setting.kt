package com.xbaimiao.mine.sponsor.core

import com.xbaimiao.mine.sponsor.MineSponsor
import taboolib.module.chat.colored

/**
 * @author xbaimiao
 */
object Setting {

    val apiKey: String get() = MineSponsor.config.getString("apiKey")!!
    val url: String get() = MineSponsor.config.getString("url")!!

    val maxPrice: Double get() = MineSponsor.config.getDouble("maxPrice")

    val minPrice: Double get() = MineSponsor.config.getDouble("minPrice")

    val exchange get() = MineSponsor.config.getInt("setting.exchange")

    val commands: List<String> get() = MineSponsor.config.getStringList("setting.commands").colored()

    val signLines get() = MineSponsor.config.getString("gui.sign")!!.colored().split("\n").toTypedArray()

}