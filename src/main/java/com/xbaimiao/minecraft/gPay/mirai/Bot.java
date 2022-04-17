package com.xbaimiao.minecraft.gPay.mirai;

import com.xbaimiao.minecraft.gPay.GPayX;
import com.xbaimiao.minecraft.gPay.deposit.Deposit;
import me.albert.amazingbot.bot.BotAPI;

/**
 * @author xbaimiao
 * @date 2021/12/10 12:36
 */
public class Bot {

    public static void run(Deposit deposit) {
        String qq = GPayX.INSTANCE.getConfig().getString("qq");
        if (qq != null && !qq.equals("")) {
            BotAPI api = me.albert.amazingbot.bot.Bot.getApi();
            api.sendGroupMsg(Long.parseLong(qq), String.format(
                    "订单支付通知:\n" +
                            "订单号: %s\n" +
                            "商品名: %s\n" +
                            "总金额: %s\n"
                    , deposit.getOrderId(), deposit.getPlayer().getName() + "点券充值", deposit.getPrice()));
        }
    }

}
