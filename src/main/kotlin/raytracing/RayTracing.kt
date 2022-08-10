package raytracing

import raytracing.Geometry.PhysicalColor
import raytracing.Geometry.Plane
import raytracing.Geometry.Sphere
import raytracing.lightning.AmbientLight
import raytracing.lightning.DirectionalLight
import raytracing.lightning.PointLight
import raytracing.lightning.toVector
import java.awt.BorderLayout
import java.awt.Color
import javax.swing.JFrame


class RayTracing {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            JFrame.setDefaultLookAndFeelDecorated(true)
            val frame = JFrame("RayTracing")
            frame.setSize(1200, 800)
            frame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
            val scene = Scene(
                Camera(Vector(0.0), Vector(0.0, 0.0, 1.0), Vector(0.0, 1.0, 0.0), 1.0, 1.0),
                arrayOf(
                    PointLight(Vector(0.0, 2.0, 3.0), Color.white.toVector() / 2.0),
                    AmbientLight(Vector(0.075)),
                    DirectionalLight(Vector(0.5, -1.0, 0.0), Color.white.toVector()),
                    PointLight(Vector(0.0, 0.0, 8.0), Color.white.toVector() / 1.0)
                ),
                arrayOf(
                    Sphere(Vector(0.7, 0.7, 4.0), 0.4, PhysicalColor(Color.green.toVector(), Vector(0.2), Vector(0.0))),
                    Sphere(Vector(1.5, -1.0, 5.0), 1.5, PhysicalColor(Color.red.toVector(), Vector(0.2), Vector(0.0))),
                    Plane(Vector(0.0, 1.0, 0.0), -1.75, PhysicalColor(Color.blue.toVector(), Vector(0.3), Vector(0.0)))
                )
            )
            val panel = RayTracingPanel(scene, useLight = true, refractionTimes = 1.toUInt())
            frame.contentPane.add(panel, BorderLayout.CENTER)
//            frame.pack()
            frame.isVisible = true
        }
    }
}
