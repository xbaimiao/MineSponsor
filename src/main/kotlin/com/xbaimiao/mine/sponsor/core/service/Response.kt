package com.xbaimiao.mine.sponsor.core.service

import java.awt.image.BufferedImage

data class Response(
    val orderId: String,
    val codeUrl: String,
    val money: Double,
    val image: BufferedImage
)
