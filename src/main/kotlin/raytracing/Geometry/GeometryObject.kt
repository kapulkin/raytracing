package raytracing.Geometry

import raytracing.Ray
import raytracing.Vector
import java.awt.Color

interface GeometryObject {
    companion object {
        val epsilon = 0.000001
    }

    fun intersect(ray: Ray): Double

    fun getColorAtPoint(point: Vector): PhysicalColor?

    fun getNormalAtPoint(point: Vector): Vector?
}