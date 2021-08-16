package raytracing

import kotlin.math.sqrt

class Vector(val x: Double, val y: Double, val z: Double) {
    constructor(v: Double): this(v, v, v) {}

    fun lengthSqr(): Double {
        return x*x + y*y + z*z
    }

    fun length(): Double {
        return sqrt(lengthSqr())
    }

    fun normalized(): Vector {
        return this / length()
    }

    operator fun unaryMinus(): Vector {
        return Vector(-x, -y, -z)
    }

    operator fun plus(v: Vector): Vector {
        return Vector(x + v.x, y + v.y, z + v.z)
    }

    operator fun minus(v: Vector): Vector {
        return Vector(x - v.x, y - v.y, z - v.z)
    }

    operator fun times(v: Vector): Vector {
        return Vector(x * v.x, y * v.y, z * v.z)
    }

    operator fun times(c: Double): Vector {
        return Vector(x * c, y * c, z * c)
    }

    operator fun div(c: Double): Vector {
        return Vector(x / c, y / c, z / c)
    }

    fun dot(v: Vector): Double {
        return x * v.x + y * v.y + z * v.z
    }

    fun cross(v: Vector): Vector {
        return Vector(y * v.z - z * v.y, z * v.x - x * v.z, x * v.y - y * v.x)
    }
}