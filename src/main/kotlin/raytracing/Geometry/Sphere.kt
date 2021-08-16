package raytracing.Geometry

import raytracing.Ray
import raytracing.Vector
import java.awt.Color
import kotlin.math.min
import kotlin.math.sqrt

class Sphere(val center: Vector, val radius: Double, val color: PhysicalColor): GeometryObject {
    override fun intersect(ray: Ray): Double {
        val f_c = ray.from - center
        val a = ray.dir.lengthSqr()
        val b = 2 * ray.dir.dot(f_c)
        val c = f_c.lengthSqr() - radius * radius

        val D = b * b - 4 * a * c
        if (D < 0) {
            return -1.0
        } else if (D > 0) {
            val sqrtD = sqrt(D)
            val t1 = (-b + sqrtD) / 2 / a
            val t2 = (-b - sqrtD) / 2 / a
            if (t1 > 0 && t2 > 0) {
                return min(t1, t2)
            } else if (t1 > 0) {
                return t1
            } else {
                return t2
            }
        } else {
            return -b / 2 / a
        }
    }

    override fun getColorAtPoint(point: Vector): PhysicalColor? {
        return color
    }

    override fun getNormalAtPoint(point: Vector): Vector? {
        return (point - center).normalized()
    }
}