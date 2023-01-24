package com.xbaimiao.mine.sponsor.core.service.weixin

import com.google.gson.JsonParser
import com.xbaimiao.mine.sponsor.core.HttpClient
import com.xbaimiao.mine.sponsor.core.service.CreateTradeOn
import com.xbaimiao.mine.sponsor.core.service.Response
import java.util.*
import javax.imageio.ImageIO

class WeiXinServiceImpl(
    private val url: String,
    private val apiKey: String
) : WeiXinService, CreateTradeOn {

    private val version = "v1"

    override fun wxQuery(orderId: String): Boolean {
        val result = HttpClient.get("$url/api/$version/query?apiKey=${apiKey}&order=$orderId") ?: return false
        val body = result.body?.string() ?: return false
        val json = JsonParser().parse(body).asJsonObject
        if (json.get("code").asInt != 0) {
            return false
        }
        return json.get("message").asString.toBoolean()
    }

    override fun wxPay(orderName: String, player: String, amount: Double): Response? {
        val orderId = createOutTradeNo()
        val url =
            "$url/api/$version/create?apiKey=${apiKey}&desc=${orderName}&type=wechat&money=$amount&player=$player&orderId=$orderId"
        val result = HttpClient.get(url) ?: return null
        val body = result.body?.string() ?: return null
        val json = JsonParser().parse(body).asJsonObject
        if (json.get("code").asInt != 0) {
            return null
        }
        val codeUrl = json.get("message").asString
            .replace("\\u003d", "=")
            .replace("\\u0026", "&")
        val image =
            ImageIO.read(Base64.getDecoder().decode(json.get("params").asJsonObject.get("qr").asString).inputStream())
        return Response(orderId, codeUrl, amount, image)
    }

}