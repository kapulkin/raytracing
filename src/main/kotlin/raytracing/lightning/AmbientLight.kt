package raytracing.lightning

import raytracing.Scene
import raytracing.Vector

class AmbientLight(val color: Vector): Light {
    override fun evaluateLightingAt(point: Vector, scene: Scene): Vector {
        return color
    }

    override fun directionAt(point: Vector): Vector {
        throw NotImplementedError("Ambient light does not support direction")
    }
}