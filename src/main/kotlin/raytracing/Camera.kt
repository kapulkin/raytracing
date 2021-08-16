package raytracing

class Camera(val position: Vector, dir: Vector, up: Vector, val near: Double, val projectionPlaneHeight: Double) {
    val dir = dir.normalized()
    val up = up.normalized()
    val right = dir.cross(up)
}