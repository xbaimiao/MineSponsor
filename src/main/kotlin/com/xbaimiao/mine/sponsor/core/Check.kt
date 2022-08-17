package com.xbaimiao.mine.sponsor.core

import java.util.regex.Pattern

object Check {

    fun String.isNumber(): Boolean {
        if (this == "" || this.startsWith("0.00")) {
            return false
        }
        val pattern = Pattern.compile("[0-9.]*")
        var a = false
        for (c in this) {
            if (c == '.') {
                if (a) {
                    return false
                }
                a = true
            }
        }
        val isNum = pattern.matcher(this)
        if (isNum.matches()) {
            val amount = this.toDouble()
            if (amount < Setting.minPrice || amount > Setting.maxPrice) {
                return false
            }
            return true
        }
        return false
    }


}