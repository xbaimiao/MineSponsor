package com.xbaimiao.minecraft.gPay.logger

import taboolib.common.io.newFile
import taboolib.platform.BukkitPlugin
import java.io.File
import java.text.SimpleDateFormat


class FileLogger(private val file: File) : Logger {

    init {
        cache.add(this)
    }

    val writer = file.printWriter()

    @Synchronized
    override fun println(string: String) {
        writer.println(string)
    }

    companion object {

        val cache = arrayListOf<FileLogger>()
        private var instance: FileLogger? = null

        fun getInstance(time: Long = System.currentTimeMillis()): FileLogger {
            if (instance != null) {
                return instance!!
            }
            val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd-HH-mm")
            return FileLogger(
                newFile(
                    BukkitPlugin.getInstance().dataFolder,
                    "logs${File.separator}${simpleDateFormat.format(time)}.log"
                )
            ).also {
                instance = it
            }
        }

    }

}