package com.xbaimiao.minecraft.gPay.command

import com.xbaimiao.minecraft.gPay.GPayX
import com.xbaimiao.minecraft.gPay.core.Config
import com.xbaimiao.minecraft.gPay.deposit.Deposit
import com.xbaimiao.minecraft.gPay.deposit.DepositType
import com.xbaimiao.minecraft.gPay.isNumber
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

object Auto {

    private val map = HashMap<Player, DepositType>()

    private fun Player.getPayType(): DepositType {
        return map[this] ?: DepositType.WX
    }

    fun open(player: Player) {
        player.openInventory(gui.invoke())
    }

    @SubscribeEvent
    fun click(event: InventoryClickEvent) {
        if (event.inventory.holder is Holder) {
            event.isCancelled = true
            val player = event.whoClicked as Player
            val slot = event.slot
            if (slot == 2) {
                map[player] = DepositType.ALIPAY
            }
            if (slot == 6) {
                map[player] = DepositType.WX
            }
            create(player)
        }
    }

    private fun create(player: Player) {
        player.inputSign(Config.signLines) {
            if (it.isEmpty()) {
                return@inputSign
            }
            val var1 = it[0].replace(Regex("§[0-9a-zA-Z]"), "")
            if (var1.isNumber()) {
                val amount = var1.toDouble()
                Deposit.create(player, amount, player.getPayType())
            } else {
                player.sendLang("input-error")
            }
        }
    }

    private val gui: () -> Inventory
        get() = {
            val inv = Bukkit.createInventory(Holder, 9, "&7&l自助赞助".colored())
            val aliHead = buildItem(XMaterial.PLAYER_HEAD) {
                skullTexture =
                    ItemBuilder.SkullTexture(
                        "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUv" +
                                "NDA5YWRkNDZlMWMxZjNkMzBkOThkYjQ1NjNkNTMyNjE3MGUyZjk0ZjRlYTY5YWY2ZDJmNzc1NDk5ZTM3MGVmNCJ9fX0=",
                        UUID.randomUUID()
                    )
                name = GPayX.config.getString("gui.ali.name")
                lore.addAll(GPayX.config.getStringList("gui.ali.lore"))
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
                name = GPayX.config.getString("gui.wx.name")
                lore.addAll(GPayX.config.getStringList("gui.wx.lore"))
                colored()
                build()
            }
            inv.setItem(2, aliHead)
            inv.setItem(6, wxHead)
            inv
        }

}

object Holder : InventoryHolder {
    override fun getInventory(): Inventory {
        TODO("Not yet implemented")
    }
}