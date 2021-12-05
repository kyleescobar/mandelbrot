package dev.kyleescobar.mandelbrot

import java.awt.Color

class Gradient(private val numcolors: Int, vararg val colors: Color) {

    private lateinit var colorTable: Array<IntArray?>
    
    private fun generatecolorTable() {
        colorTable = arrayOfNulls(numcolors)
        var curcolor = 0
        val gradesPercolor = numcolors / colors.size
        for (grad in 0 until colors.size) {
            val fromcolor = colors[grad]
            val tocolor = colors[(grad + 1) % colors.size]
            val startIndex = curcolor * gradesPercolor
            fillcolor(startIndex, gradesPercolor, fromcolor, tocolor)
            curcolor++
        }
    }

    private fun fillcolor(startIndex: Int, numGrades: Int, fromcolor: Color, tocolor: Color) {
        for (i in startIndex until startIndex + numGrades) {
            //Get the average red, blue and green between the from and to colors at this index
            val r = fromcolor.red + ((tocolor.red - fromcolor.red) * (i - startIndex) / numGrades)
            val g = fromcolor.green + ((tocolor.green - fromcolor.green) * (i - startIndex) / numGrades)
            val b = fromcolor.blue + ((tocolor.blue - fromcolor.blue) * (i - startIndex) / numGrades)
            colorTable[i] = intArrayOf(r, g, b)
        }
    }

    fun getColor(factor: Double): IntArray? {
        assert(factor >= 0 && factor < 1)
        return colorTable[(numcolors * factor).toInt()]
    }

    /**
     * Creates a gradient object with the given number of unique
     * colors cycling between the given list of fixed color points
     * @param colors
     */
    init {
        generatecolorTable()
    }
}