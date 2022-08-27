package com.xbaimiao.mine.sponsor.api

import org.bukkit.entity.Player
import taboolib.module.nms.sendMap
import java.awt.image.BufferedImage
import java.net.URL

object MineSponsorAPI {

    @JvmStatic
    fun sendMap(player: Player, url: String) {
        player.sendMap(url)
    }

    @JvmStatic
    fun sendMap(player: Player, url: URL) {
        player.sendMap(url)
    }

    @JvmStatic
    fun sendMap(player: Player, image: BufferedImage) {
        player.sendMap(image)
    }

}