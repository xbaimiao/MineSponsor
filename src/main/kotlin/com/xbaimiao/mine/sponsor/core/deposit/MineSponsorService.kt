package com.xbaimiao.mine.sponsor.core.deposit

import com.xbaimiao.mine.sponsor.MineSponsor
import com.xbaimiao.mine.sponsor.core.Setting
import com.xbaimiao.mine.sponsor.core.service.Response
import com.xbaimiao.mine.sponsor.core.service.ali.AliService
import com.xbaimiao.mine.sponsor.core.service.ali.AliServiceImpl
import com.xbaimiao.mine.sponsor.core.service.weixin.WeiXinService
import com.xbaimiao.mine.sponsor.core.service.weixin.WeiXinServiceImpl

object MineSponsorService {

    private lateinit var aliService: AliService
    private lateinit var weiXinService: WeiXinService

    init {
        reload()
    }

    fun reload() {
        aliService = AliServiceImpl(Setting.url, Setting.apiKey)
        weiXinService = WeiXinServiceImpl(Setting.url, Setting.apiKey)
    }

    fun pay(sponsor: Sponsor): Response? {
        return when (sponsor.type) {
            SponsorType.WX -> weiXinService.wxPay(sponsor.desc, sponsor.player.name, sponsor.price)
            SponsorType.ALIPAY -> aliService.alipay(sponsor.desc, sponsor.player.name, sponsor.price)
        }
    }

    fun query(sponsor: Sponsor): Boolean {
        return when (sponsor.type) {
            SponsorType.WX -> weiXinService.wxQuery(sponsor.player.name)
            SponsorType.ALIPAY -> aliService.aliQuery(sponsor.player.name)
        }
    }

}