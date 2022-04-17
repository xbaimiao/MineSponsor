package com.xbaimiao.minecraft.gPay.logger

import taboolib.common.io.newFile
import taboolib.platform.BukkitPlugin
import java.io.File
import java.text.SimpleDateFormat


class FileLogger(private val file: File) : Logger {

    @Synchronized
    override fun println(string: String) {
        file.printWriter().use {
            it.println(string)
        }
    }

    companion object {

        private var instance: FileLogger? = null

        fun getInstance(time: Long = System.currentTimeMillis()): FileLogger {
            if (instance != null) {
                return instance!!
            }
            val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd-HH-mm")
            return FileLogger(
                newFile(
                    BukkitPlugin.getInstance().dataFolder,
                    "${simpleDateFormat.format(time)}.log"
                )
            ).also {
                instance = it
            }
        }

    }

}