package raytracing.Geometry

import raytracing.Ray
import raytracing.Vector
import java.awt.Color
import kotlin.math.abs

class Plane(normal: Vector, val distance: Double, val color: PhysicalColor): GeometryObject {
    val normal = normal.normalized()

    override fun intersect(ray: Ray): Double {
        val dirProj = ray.dir.dot(normal)
        if (abs(dirProj) < GeometryObject.epsilon) {
            return -1.0
        }
        val t = (distance - ray.from.dot(normal)) / dirProj
        return t
    }

    override fun getColorAtPoint(point: Vector): PhysicalColor? {
        return color
    }

    override fun getNormalAtPoint(point: Vector): Vector? {
        return normal
    }
}