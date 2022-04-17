package com.xbaimiao.minecraft.gPay.utils

import com.lly835.bestpay.config.AliPayConfig
import com.lly835.bestpay.config.WxPayConfig
import com.lly835.bestpay.service.impl.BestPayServiceImpl
import com.xbaimiao.minecraft.gPay.GPayX

object Service {

    private var wxPayConfig: WxPayConfig = object : WxPayConfig() {
        init {
            val section = GPayX.key.getConfigurationSection("pay_wx")!!
            appId = section.getString("appid")
            mchId = section.getString("mchid")
            mchKey = section.getString("mchKey")
            appSecret = section.getString("appSecret")
            notifyUrl = section.getString("notifyUrl")
        }
    }

    private var aliPayConfig: AliPayConfig = object : AliPayConfig() {
        init {
            val section = GPayX.key.getConfigurationSection("pay_ali")!!
            appId = section.getString("appid")
            privateKey = section.getString("privateKey")
            aliPayPublicKey = section.getString("aliPayPublicKey")
        }
    }

    fun reload() {
        wxPayConfig = object : WxPayConfig() {
            init {
                val section = GPayX.key.getConfigurationSection("pay_wx")!!
                appId = section.getString("appid")
                mchId = section.getString("mchid")
                mchKey = section.getString("mchKey")
                appSecret = section.getString("appSecret")
                notifyUrl = section.getString("notifyUrl")
            }
        }
        aliPayConfig = object : AliPayConfig() {
            init {
                val section = GPayX.key.getConfigurationSection("pay_ali")!!
                appId = section.getString("appid")
                privateKey = section.getString("privateKey")
                aliPayPublicKey = section.getString("aliPayPublicKey")
            }
        }
    }

    val bestPayService = BestPayServiceImpl()

    init {
        bestPayService.setWxPayConfig(wxPayConfig)
        bestPayService.setAliPayConfig(aliPayConfig)
    }

}