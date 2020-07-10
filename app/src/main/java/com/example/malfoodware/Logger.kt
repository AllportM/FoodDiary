package com.example.malfoodware

class Logger {
    companion object {
        val LOGGER_FILE = "logs.txt"
        var logs: MutableList<String> = mutableListOf()

        init {
            FileHelper.loadFile(LOGGER_FILE, "res/")
//            logs = FileHelper.getFileContentsArr(LOGGER_FILE)
        }

        fun add(message: String) {
            var output: String = ""
            var date = java.util.Calendar.getInstance()
            date.set(1, 1, 1, 1, 1, 1)
            output += "****[" + date.time + "]**** $message"
            logs.add(output)
        }

        fun saveContents() {
            var output: String = ""
            if (logs.size > 50) {
                for (i in logs.size - 51 until logs.size) {
                    output += logs[i] + "\n"
                }
                FileHelper.writeContents(LOGGER_FILE, output)
            } else {
                for (i in 0 until logs.size) {
                    output += logs.get(i) + "\n"
                }
                FileHelper.writeContents(LOGGER_FILE, output)
            }
            println(output)
        }

        fun last()
        {
            finalize()
        }

        protected fun finalize()
        {
            saveContents()
        }
    }
}