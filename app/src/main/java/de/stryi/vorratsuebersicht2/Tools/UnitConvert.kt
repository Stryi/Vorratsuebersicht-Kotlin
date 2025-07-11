package de.stryi.vorratsuebersicht2.Tools

object UnitConvert {

    fun getCaloriePerUnit(sizeText: String?, unit: String?, calorieText: String?): String {
        val size = sizeText?.toDoubleOrNull() ?: return "---"
        val calorie = calorieText?.toDoubleOrNull() ?: return "---"
        val u = unit?.lowercase() ?: return "---"

        if (size <= 0.0 || calorie <= 0.0) return "---"

        val factor = when (u) {
            "kg", "l" -> 10.0
            "g", "ml" -> 0.01
            else -> return "---"
        }

        val calPerUnit = (calorie / size / factor).toInt()
        return calPerUnit.toString()
    }

    fun getConvertUnit(unit: String?): String {
        return when (unit?.lowercase()) {
            "kg", "g" -> "g"
            "l", "ml" -> "ml"
            else -> ""
        }
    }
}
