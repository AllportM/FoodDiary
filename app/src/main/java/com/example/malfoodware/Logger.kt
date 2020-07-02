package com.example.malfoodware

import java.io.File
import java.time.LocalDateTime

class Logger(val filename: String) {
    var logs: MutableList<String>

    init {
        FileHelper.loadFile(filename, "res/")
        logs = FileHelper.getFileContentsArr(filename)
    }

    fun add(message: String)
    {
        var output: String = ""
        var date = java.util.Calendar.getInstance()
        date.set(1, 1, 1, 1, 1, 1)
        output += "[" + date.time + "] $message"
        logs.add(output)
    }

    fun saveContents()
    {
        var output: String = ""
        if (logs.size > 50)
        {
            for (i in logs.size-51 until logs.size-1)
            {
                output += logs[i] + "\n"
            }
            FileHelper.writeContents(filename, output)
        }
        else
        {
            for (i in 0 until logs.size-1)
            {
                output += logs[i] + "\n"
            }
            FileHelper.writeContents(filename, output)
        }
    }
}