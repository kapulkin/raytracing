package raytracing

import raytracing.Geometry.GeometryObject
import raytracing.Geometry.PhysicalColor
import raytracing.lightning.toColor
import raytracing.lightning.toVector
import java.awt.Graphics
import java.awt.Graphics2D
import javax.swing.JPanel
import kotlin.math.min
import kotlin.math.sin


class RayTracingPanel(val scene: Scene, val useLight: Boolean, val refractionTimes: UInt = 1.toUInt()): JPanel() {
    override fun paint(g: Graphics) {
        val g2d = g as Graphics2D

        for (x in 0..width) {
            for (y in 0..height) {
                var pixelColor = Vector(0.0)
                val subpixelsCount = 3
                for (i in 0 until subpixelsCount) {
                    for (j in 0 until subpixelsCount) {
                        val step = scene.camera.projectionPlaneHeight / height
                        val position = scene.camera.position +
                                scene.camera.dir * scene.camera.near +
                                scene.camera.up * (step * (height / 2.0 - y) + step / subpixelsCount * (i - (subpixelsCount - 1) / 2.0)) +
                                scene.camera.right * (step * (x - width / 2.0)  + step / subpixelsCount * (j - (subpixelsCount - 1) / 2.0))

                        val ray = Ray(
                            position,
                            position - scene.camera.position
                        )
                        var color = scene.rayTrace(ray, useLight, 1 + refractionTimes.toInt())
                        pixelColor += color
                    }
                }
                pixelColor /= (subpixelsCount * subpixelsCount).toDouble()
                pixelColor = Vector(min(pixelColor.x, 1.0), min(pixelColor.y, 1.0), min(pixelColor.z, 1.0))
                g.color = pixelColor.toColor()

//                g.color = Color((255 * x) / width, (255 * y) / height, 0)
                g.drawLine(x, y, x, y)
            }
        }

    }
}