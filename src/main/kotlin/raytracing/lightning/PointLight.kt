package raytracing.lightning

import raytracing.Geometry.GeometryObject
import raytracing.Ray
import raytracing.Scene
import raytracing.Vector

class PointLight(val point: Vector, val color: Vector, val c0: Double = 0.01, val c1: Double = 0.5, val c2: Double = 0.1): Light {
    override fun evaluateLightingAt(position: Vector, scene: Scene): Vector {
        val dir = (this.point - position).normalized()
        val ray = Ray(
            position + dir * GeometryObject.epsilon,
            dir
        )

        var (minT, nearestGeomObject: GeometryObject?) = scene.intersect(ray)
        if (minT < 0) {
            val distance = (this.point - position).length()
            return color / (c0 + c1 * distance + c2 * distance * distance)
        }
        return Vector(0.0)
    }

    override fun directionAt(position: Vector): Vector {
        return point - position
    }
}
