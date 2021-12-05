package dev.kyleescobar.mandelbrot

import java.awt.Color
import java.awt.image.BufferedImage
import java.awt.image.BufferedImage.TYPE_INT_RGB
import java.math.BigDecimal
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class Mandelbrot(var width: Int, var height: Int) {

    var initialWidth = 3.0
    var initialHeight = initialWidth / width * height

    var curXStart = -0.5 - initialWidth / 2
    var curYStart = 0 - initialHeight / 2

    val gradient = Gradient(
        MAX_GRADIENT_ITERATIONS,
        Color(0, 0, 0),
        Color(255, 0, 0),
        Color(255, 255, 0),
        Color(255, 255, 255),
    )

    var curMagnification = BigDecimal(1L)

    lateinit var executor: ExecutorService private set

    var cancelled: Boolean = false

    var image = BufferedImage(width, height, TYPE_INT_RGB)
    var raster = image.raster

    fun draw(xPos: Double, yPos: Double, magnification: BigDecimal, iterations: Int): Boolean {
        val xSize = initialWidth / magnification.toDouble()
        val ySize = initialHeight / magnification.toDouble()
        val xStart = curXStart + (initialWidth / curMagnification.toDouble() * xPos / width) - xSize / 2
        val yStart = curYStart + (initialHeight / curMagnification.toDouble() * yPos / height) - ySize / 2

        cancelled = false
        executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors())

        for(i in 0 until Main.THREADS) {
            val x = 0
            val y = i * (height / Main.THREADS)
            val xLimit = (x + width)
            val yLimit = y + (height / Main.THREADS)
            executor.execute(MandelbrotRenderer(xStart, xSize, yStart, ySize, width, height, x, y, xLimit, yLimit, iterations))
        }

        executor.shutdown()
        executor.awaitTermination(10, TimeUnit.SECONDS)

        curXStart = xStart
        curYStart = yStart
        curMagnification = magnification

        return !cancelled
    }

    fun cancel() {
        cancelled = true
    }

    fun resize(width: Int, height: Int) {
        this.width = width
        this.height = height
        initialWidth = 3.0
        initialHeight = initialWidth / width * height
        image = BufferedImage(width, height, TYPE_INT_RGB)
        raster = image.raster
    }

    companion object {
        private const val MAX_GRADIENT_ITERATIONS = 500
    }

    inner class MandelbrotRenderer(
        val xStart: Double,
        val xSize: Double,
        val yStart: Double,
        val ySize: Double,
        val width: Int,
        val height: Int,
        val xPos: Int,
        val yPos: Int,
        val xLimit: Int,
        val yLimit: Int,
        val iterations: Int
    ) : Runnable {

        override fun run() {
            val totalSteps = xLimit - xPos * yLimit - yPos
            var step = 0

            Main.progressBar.maximum = totalSteps

            for (y in yPos until yLimit) {
                if (cancelled) break
                for (x in xPos until xLimit) {
                    var i = 0
                    var xc = 0.0
                    var yc = 0.0
                    //Get the coordinates of the x/y point on the complex plane
                    //based on our current top right coordinate (xStart,yStart), and the
                    //width/height of the screen at the current magnification (xSize,ySize)
                    val x0 = xStart + xSize * (x.toDouble() / width)
                    val y0 = yStart + ySize * (y.toDouble() / height)
                    while (xc * xc + yc * yc < 4 && i < iterations) {
                        val xtemp = xc * xc - yc * yc
                        yc = 2 * xc * yc + y0
                        xc = xtemp + x0
                        i++
                    }
                    Main.progressBar.value = step++
                    raster.setPixel(x, y, getColor(i, iterations))
                }
            }
        }

        private fun getColor(i: Int, maxIters: Int): IntArray? {
            //Calculate the gradient factor
            //This should be a value between 0..1
            val gradientFactor: Double =
                if (i == maxIters) 0.0
                else if (maxIters < MAX_GRADIENT_ITERATIONS) i / maxIters.toDouble()
                else i % MAX_GRADIENT_ITERATIONS / MAX_GRADIENT_ITERATIONS.toDouble()
            return gradient.getColor(gradientFactor)
        }
    }
}