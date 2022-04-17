package com.xbaimiao.minecraft.gPay.hook

import com.xbaimiao.minecraft.gPay.GPayX
import me.clip.placeholderapi.expansion.PlaceholderExpansion
import org.bukkit.entity.Player

/**
 * @author xbaimiao
 */
class PlaceholderAPI : PlaceholderExpansion() {
    override fun getIdentifier(): String {
        return "gpayx"
    }

    override fun getAuthor(): String {
        return "xbaimiao"
    }

    override fun getVersion(): String {
        return "1.0"
    }

    override fun onPlaceholderRequest(p: Player, params: String): String {
        if (params == "total") {
            return GPayX.dataCenter.playerAmount(p).toString()
        }
        return super.onPlaceholderRequest(p, params)
    }
}