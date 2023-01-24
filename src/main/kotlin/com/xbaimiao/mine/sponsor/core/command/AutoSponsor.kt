package com.xbaimiao.mine.sponsor.core.command

import com.xbaimiao.mine.sponsor.MineSponsor
import com.xbaimiao.mine.sponsor.core.Setting
import com.xbaimiao.mine.sponsor.core.deposit.Sponsor
import com.xbaimiao.mine.sponsor.core.deposit.SponsorType
import com.xbaimiao.mine.sponsor.isNumber
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.InventoryHolder
import taboolib.common.platform.event.SubscribeEvent
import taboolib.library.xseries.XMaterial
import taboolib.module.chat.colored
import taboolib.module.nms.inputSign
import taboolib.platform.util.ItemBuilder
import taboolib.platform.util.buildItem
import taboolib.platform.util.sendLang
import java.util.*

object AutoSponsor {

    class Holder : InventoryHolder {
        override fun getInventory(): Inventory {
            return Bukkit.createInventory(this, 9, "§8§l自动赞助".colored())
        }
    }

    enum class AutoType {
        POINTS, KIT
    }

    private val map = HashMap<Player, SponsorType>()
    private val autoTypes = HashMap<UUID, AutoType>()

    private fun Player.getPayType(): SponsorType {
        return map[this] ?: SponsorType.WX
    }

    private fun Player.getAutoType(): AutoType {
        return autoTypes[uniqueId] ?: AutoType.POINTS
    }

    fun open(player: Player, type: AutoType) {
        autoTypes[player.uniqueId] = type
        player.openInventory(gui)
    }

    @SubscribeEvent
    fun click(event: InventoryClickEvent) {
        if (event.inventory.holder is Holder) {
            event.isCancelled = true
            val player = event.whoClicked as Player
            val slot = event.slot
            if (slot == 2) {
                map[player] = SponsorType.ALIPAY
            }
            if (slot == 6) {
                map[player] = SponsorType.WX
            }
            when (player.getAutoType()) {
                AutoType.POINTS -> create(player)
                AutoType.KIT -> {
                    player.closeInventory()
                }
            }
        }
    }

    private fun create(player: Player) {
        player.inputSign(Setting.signLines) {
            if (it.isEmpty()) {
                return@inputSign
            }
            val var1 = it[0].replace(Regex("§[0-9a-zA-Z]"), "")
            if (var1.isNumber()) {
                val amount = var1.toDouble()
                Sponsor.create(player, amount, player.getPayType())
            } else {
                player.sendLang("input-error")
            }
        }
    }

    private val gui by lazy {
        val inv = Bukkit.createInventory(Holder(), 9, "&7&l自助赞助".colored())
        val aliHead = buildItem(XMaterial.PLAYER_HEAD) {
            skullTexture =
                ItemBuilder.SkullTexture(
                    "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUv" +
                            "NDA5YWRkNDZlMWMxZjNkMzBkOThkYjQ1NjNkNTMyNjE3MGUyZjk0ZjRlYTY5YWY2ZDJmNzc1NDk5ZTM3MGVmNCJ9fX0=",
                    UUID.randomUUID()
                )
            name = MineSponsor.config.getString("gui.ali.name")
            lore.addAll(MineSponsor.config.getStringList("gui.ali.lore"))
            colored()
            build()
        }
        val wxHead = buildItem(XMaterial.PLAYER_HEAD) {
            skullTexture =
                ItemBuilder.SkullTexture(
                    "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmU" +
                            "vMWY5YTQwMWMzNzRhZDcwODNmMjVhZGNkZDUyYjQ2YTc0NzQyYzQ4YmEyZTM5ZGQ2YmMzZTAwMTAzZjJmOThkOSJ9fX0=",
                    UUID.randomUUID()
                )
            name = MineSponsor.config.getString("gui.wx.name")
            lore.addAll(MineSponsor.config.getStringList("gui.wx.lore"))
            colored()
            build()
        }
        inv.setItem(2, aliHead)
        inv.setItem(6, wxHead)
        inv
    }

}
