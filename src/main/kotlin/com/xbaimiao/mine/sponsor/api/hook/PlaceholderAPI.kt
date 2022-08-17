package com.xbaimiao.mine.sponsor.api.hook

import com.xbaimiao.mine.sponsor.MineSponsor
import me.clip.placeholderapi.expansion.PlaceholderExpansion
import org.bukkit.entity.Player

/**
 * @author xbaimiao
 */
class PlaceholderAPI : PlaceholderExpansion() {
    override fun getIdentifier(): String {
        return "minesponsor"
    }

    override fun getAuthor(): String {
        return "xbaimiao"
    }

    override fun getVersion(): String {
        return "1.0"
    }

    override fun onPlaceholderRequest(p: Player, params: String): String {
        if (params == "total") {
            return MineSponsor.dataCenter.playerAmount(p).toString()
        }
        return super.onPlaceholderRequest(p, params)
    }
}