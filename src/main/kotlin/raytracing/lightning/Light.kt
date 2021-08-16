package raytracing.lightning

import raytracing.Scene
import raytracing.Vector
import java.awt.Color

interface Light {
    fun evaluateLightingAt(point: Vector, scene: Scene): Vector

    fun directionAt(point: Vector): Vector
}

fun Vector.toColor(): Color {
    return Color(x.toFloat(), y.toFloat(), z.toFloat())
}

fun Color.toVector(): Vector {
    return Vector(red / 255.0, green / 255.0, blue / 255.0)
}
