package dev.kyleescobar.mandelbrot

import java.awt.Canvas
import java.math.BigDecimal

class CanvasRenderer(val canvas: Canvas, val mandelbrot: Mandelbrot, var width: Int, var height: Int) : Runnable {

    private var magnification = BigDecimal(1L)
    private var iterations = 100
    private var x: Int = width / 2
    private var y: Int =  height / 2
    private val lock = Object()
    private var resized = false

    private fun zoom(x: Int, y: Int, magnification: Long, iterations: Int) {
        this.x = x
        this.y = y
        this.magnification = BigDecimal(magnification)
        this.iterations = iterations
        cancel()
        triggerEvent()
    }

    fun zoomIn(x: Int, y: Int) {
        zoom(x, y, magnification.multiply(BigDecimal(2L)).toLong(), iterations + 75)
    }

    fun zoomOut(x: Int, y: Int) {
        zoom(x, y, magnification.divide(BigDecimal(2L)).toLong(), iterations - 75)
    }

    fun resize(width: Int, height: Int) {
        this.width = width
        this.height = height
        this.x = width / 2
        this.y = height / 2
        resized = true
        cancel()
        triggerEvent()
    }

    override fun run() {
        while(true) {
            if(resized) {
                mandelbrot.resize(width, height)
                resized = false
            }

            synchronized(lock) {
                if(mandelbrot.draw(x.toDouble(), y.toDouble(), magnification, iterations)) {
                    canvas.repaint()
                }
                waitForEvent()
            }
        }
    }

    fun cancel() = mandelbrot.cancel()

    private fun waitForEvent() {
        synchronized(lock) {
            lock.wait()
        }
    }

    private fun triggerEvent() {
        synchronized(lock) {
            lock.notify()
        }
    }
}