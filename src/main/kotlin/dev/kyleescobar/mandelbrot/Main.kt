package dev.kyleescobar.mandelbrot

import com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatAtomOneDarkContrastIJTheme
import java.awt.*
import java.awt.event.ComponentAdapter
import java.awt.event.ComponentEvent
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.lang.Math.round
import javax.swing.JDialog
import javax.swing.JFrame
import javax.swing.JProgressBar
import kotlin.math.roundToInt

object Main {

    var THREADS: Int = 4

    lateinit var frame: JFrame

    lateinit var mandelbrot: Mandelbrot
    lateinit var canvas: MandelbrotCanvas
    private lateinit var canvasRenderer: CanvasRenderer

    lateinit var progressBar: JProgressBar

    @JvmStatic
    fun main(args: Array<String>) {
        val availableThreads = Runtime.getRuntime().availableProcessors() - 2
        THREADS = (availableThreads / 2).toDouble().roundToInt() * 2

        JFrame.setDefaultLookAndFeelDecorated(true)
        JDialog.setDefaultLookAndFeelDecorated(true)
        FlatAtomOneDarkContrastIJTheme.setup()

        frame = JFrame("Mandelbrot by Kyle Escobar")
        frame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
        frame.preferredSize = Dimension(1080, 860)
        frame.size = frame.preferredSize
        frame.setLocationRelativeTo(null)
        frame.layout = BorderLayout()

        mandelbrot = Mandelbrot(frame.width, frame.height)
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
                mandelbrot.resize(width, height)
            }
        })

        frame.isVisible = true

        val thread = Thread(canvasRenderer)
        thread.isDaemon = true
        thread.start()

        canvas.init()
        canvas.repaint()
    }

    class MandelbrotCanvas(private val mandelbrot: Mandelbrot) : Canvas() {

        fun init() {
            createBufferStrategy(3)
        }

        override fun paint(g: Graphics) {
            bufferStrategy.drawGraphics.drawImage(mandelbrot.image, 0, 0, Color.RED, null)
            bufferStrategy.show()
        }

        override fun update(g: Graphics) {
            paint(g)
        }
    }
}

