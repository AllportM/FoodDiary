package com.example.malfoodware

import android.content.Context
import java.io.File
import java.io.FileNotFoundException

/**
 * FileHelpers purpose is to contain file input/output static functions and cached file map
 */
class FileHelper
{
    enum class Type
    {
        TEMP, PERM
    }
    companion object {
        var files: MutableMap<String, File> = mutableMapOf() // static map of file references
        var context:Context? = null // app context, used to decide whether to use app directory
                                // if running in android environment
        var type = Type.PERM
        var path = ""

        /**
         * Opens existing file/directory, or creates new file/directory if does not exist
         * @params
         *      filename: String, the name of file
         *      directory: String, the directory to open
         * @return
         *      Boolean: true if file opened, false if file created and opened
         */
        fun loadFile(filename: String, directory: String): Boolean {
            // creates/opens directory
            var created = false
            var dir: File
            if (context == null)
            {
                dir = File("test/" + directory)
            }
            // directory equals apps local data
            else if (type == Type.PERM) {
                path = context!!.filesDir!!.absolutePath
                dir = File("$path/$directory/")
            }
            else {
                path = context!!.externalCacheDir!!.absolutePath
                dir = File("$path/$directory/")
            }

            // checks if directory exists, if not attempts to create
            if (!(dir.exists())) {
                try {
                    dir.mkdir()
                } catch (e: Exception) {
                    throw FileInitError("Could not create directory " + directory)
                }
            }

            //  checks if file exists, if not attempts to create
            var file = File(dir, filename)
            if (!(file.exists())) {
                try {
                    file.createNewFile()
                    created = true
                } catch (e: Exception) {
                    throw FileInitError("Could not create file " + dir.absoluteFile + filename)
                }
            }
            files.put(filename, file)
            return !created
        }

        /**
         * Retrieves file lines into mutable list using cached file map
         */
        fun getFileContentsArr(filename: String): MutableList<String> {
            checkExists(filename)
            var file: File? = files[filename]
            var result = mutableListOf<String>()
            try {
                file?.useLines { lines -> result.addAll(lines) }
                println(file?.absoluteFile)
            } catch (e: Exception) {
                println("Could not read file contents of file: $filename")
            }
            return result
        }

        fun getFileContentsStr(filename: String): String
        {
            checkExists(filename)
            var file: File? = files[filename]
            var result = ""
            try {
                result = file!!.readText()
            } catch (e: Exception) {
                println("Could not read file contents of file: $filename")
            }
            return result
        }

        fun overrideContents(filename: String, toWrite: String)
        {
            checkExists(filename)
            var output = toWrite
            if (checkNewLine(toWrite))
                output += '\n'
            files.get(filename)?.writeText(output)
        }

        fun writeContents(filename: String, toWrite: String)
        {
            checkExists(filename)
            var output = toWrite
            if (checkNewLine(toWrite)) output += '\n'
            files.get(filename)?.appendText(output)
        }

        private fun checkNewLine(toCheck: String): Boolean
        {
            if (toCheck.length > 0)
                return toCheck.get(toCheck.length-1) != '\n' && toCheck.get(toCheck.length-2) != '\n'
            return true
        }

        private fun checkExists(filename: String)
        {
            if (!files.containsKey(filename)) throw FileNotFoundException("Failed to open file: $filename")
        }
    }
}