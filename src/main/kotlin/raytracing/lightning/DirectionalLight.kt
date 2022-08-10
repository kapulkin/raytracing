package raytracing.lightning

import raytracing.Scene
import raytracing.Vector

class DirectionalLight(val direction: Vector, val color: Vector): Light {
    override fun evaluateLightingAt(point: Vector, scene: Scene): Vector {
        return color
    }

    override fun directionAt(point: Vector): Vector {
        return -direction
    }
}