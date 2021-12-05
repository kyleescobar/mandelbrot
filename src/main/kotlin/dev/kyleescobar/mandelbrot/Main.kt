package dev.kyleescobar.mandelbrot

import com.formdev.flatlaf.FlatDarculaLaf
import com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatAtomOneDarkContrastIJTheme
import com.formdev.flatlaf.ui.FlatProgressBarUI
import java.awt.*
import java.awt.event.ComponentAdapter
import java.awt.event.ComponentEvent
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.swing.JDialog
import javax.swing.JFrame
import javax.swing.JProgressBar
import javax.swing.plaf.ProgressBarUI

object Main {

    const val THREADS = 4

    lateinit var frame: JFrame

    lateinit var mandelbrot: Mandelbrot
    lateinit var canvas: MandelbrotCanvas
    private lateinit var canvasRenderer: CanvasRenderer

    lateinit var progressBar: JProgressBar

    @JvmStatic
    fun main(args: Array<String>) {
        JFrame.setDefaultLookAndFeelDecorated(true)
        JDialog.setDefaultLookAndFeelDecorated(true)
        FlatAtomOneDarkContrastIJTheme.setup()

        frame = JFrame("Mandelbrot by Kyle Escobar")
        frame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
        frame.preferredSize = Dimension(1080, 860)
        frame.size = frame.preferredSize
        frame.setLocationRelativeTo(null)
        frame.layout = BorderLayout()

        mandelbrot = Mandelbrot(1080, 860)
        canvas = MandelbrotCanvas(mandelbrot)
        canvasRenderer = CanvasRenderer(canvas, mandelbrot, frame.width, frame.height)

        frame.add(canvas, BorderLayout.CENTER)
        frame.pack()

        progressBar = JProgressBar()
        progressBar.isVisible = true
        progressBar.preferredSize = Dimension(frame.width, 16)

        frame.add(progressBar, BorderLayout.SOUTH)

        canvas.addMouseListener(object : MouseAdapter() {
            override fun mousePressed(e: MouseEvent) {
                if(e.button == MouseEvent.BUTTON1) {
                    canvasRenderer.zoomIn(e.x, e.y)
                } else if(e.button == MouseEvent.BUTTON3) {
                    canvasRenderer.zoomOut(e.x, e.y)
                }
            }
        })

        canvas.addComponentListener(object : ComponentAdapter() {
            override fun componentResized(e: ComponentEvent) {
                val width = e.component.width
                val height = e.component.height
                canvasRenderer.resize(width, height)
            }
        })

        frame.isVisible = true

        val thread = Thread(canvasRenderer)
        thread.isDaemon = true
        thread.start()
        canvas.repaint()
    }

    class MandelbrotCanvas(private val mandelbrot: Mandelbrot) : Canvas() {

        override fun paint(g: Graphics) {
            g.drawImage(mandelbrot.image, 0, 0, Color.RED, null)
        }

        override fun update(g: Graphics) {
            paint(g)
        }
    }
}

