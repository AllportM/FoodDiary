package com.example.malfoodware

class JSONParser
{
    companion object
    {
        fun parseJSON(arr: MutableMap<String, Recipe>, filename: String): Boolean
        {
            var contents = FileHelper.getFileContentsStr(filename)
            var hasRecipes = false
            arr["hey"] = Recipe("recID0", "Invalid Recipe!")
            println(contents.substring(7))
            return true
        }
    }
}