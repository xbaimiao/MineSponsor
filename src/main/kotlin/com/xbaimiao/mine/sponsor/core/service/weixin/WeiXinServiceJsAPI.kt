package com.xbaimiao.mine.sponsor.core.service.weixin

import com.google.gson.JsonParser
import com.xbaimiao.mine.sponsor.core.HttpClient
import com.xbaimiao.mine.sponsor.core.service.CreateTradeOn
import com.xbaimiao.mine.sponsor.core.service.Response
import taboolib.common.platform.function.info

class WeiXinServiceJsAPI(
    private val url: String
) : WeiXinService, CreateTradeOn {

    private val version = "v1"

    override fun wxQuery(orderId: String): Boolean {
        val result = HttpClient.get("$url/api/$version/query?order=$orderId") ?: return false
        val body = result.body()?.string() ?: return false
        val json = JsonParser().parse(body).asJsonObject
        if (json.get("code").asInt != 0) {
            return false
        }
        return json.get("message").asString.toBoolean()
    }

    override fun wxNative(orderName: String, player: String, amount: Double): Response? {
        val orderId = createOutTradeNo()
        val url = "$url/api/$version/create?desc=${orderName}&money=$amount&player=$player&orderId=$orderId"
        info("请求url: $url")
        val result = HttpClient.get(url) ?: return null
        val body = result.body()?.string() ?: return null
        val json = JsonParser().parse(body).asJsonObject
        if (json.get("code").asInt != 0) {
            return null
        }
        val codeUrl = json.get("message").asString
            .replace("\\u003d", "=")
            .replace("\\u0026", "&")
        return Response(orderId, codeUrl, amount)
    }

}