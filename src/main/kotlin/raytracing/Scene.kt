package raytracing

import raytracing.Geometry.GeometryObject
import raytracing.Geometry.PhysicalColor
import raytracing.lightning.Light
import raytracing.lightning.toColor
import raytracing.lightning.toVector
import java.awt.Color
import kotlin.math.min
import kotlin.math.pow

class Scene(val camera: Camera, val lights: Array<Light>, val geometryObjects: Array<GeometryObject>, val backgroundColor: Color = Color.black) {
    fun intersect(ray: Ray): Pair<Double, GeometryObject?> {
        var nearestGeomObject: GeometryObject? = null
        var minT = -1.0
        for (geomObject in geometryObjects) {
            val t = geomObject.intersect(ray)
            if (t > 0) {
                if (minT < 0 || t < minT) {
                    minT = t
                    nearestGeomObject = geomObject
                }
            }
        }
        return Pair(minT, nearestGeomObject)
    }

    fun colorFromLights(
        objPoint: Vector,
        objColor: PhysicalColor,
        nearestGeomObject: GeometryObject
    ): Vector {
        var lightedColor = Vector(0.0)
        for (light in lights) {
            val lightColor = light.evaluateLightingAt(objPoint, this)
            val diffuse = lightColor * objColor.color
            lightedColor += diffuse
        }
        lightedColor = Vector(min(lightedColor.x, 1.0), min(lightedColor.y, 1.0), min(lightedColor.z, 1.0))
        return lightedColor
    }

    fun glowFromLights(
        objPoint: Vector,
        objColor: PhysicalColor,
        nearestGeomObject: GeometryObject
    ): Vector {
        var lightedColor = Vector(0.0)
        for (light in lights) {
            val lightColor = light.evaluateLightingAt(objPoint, this)

            val normal = nearestGeomObject.getNormalAtPoint(objPoint)!!.normalized()
            val lightDir = light.directionAt(objPoint)
            val refLightDir = reflectDirection(lightDir, normal).normalized()
            val dirToCam = (objPoint - camera.position).normalized()
            val cos = dirToCam.dot(refLightDir)
            val specular = lightColor * Vector(
                cos.pow(2 / (1 - objColor.reflection.x)), cos.pow(2 / (1 - objColor.reflection.y)), cos.pow(2 / (1 - objColor.reflection.z))
            )
            lightedColor += specular
        }
        return lightedColor
    }

    fun rayTrace(ray: Ray, useLight: Boolean, times: Int): Vector {
        if (times == 0) {
            return backgroundColor.toVector()
        }
        var (minT, nearestGeomObject: GeometryObject?) = intersect(ray)
        if (minT > 0) {
            val objPoint = ray.from + ray.dir * minT
            val objColor = nearestGeomObject!!.getColorAtPoint(objPoint)
            if (objColor != null) {
                var color: Vector
                if (useLight) {
                    var lightedColor = colorFromLights(objPoint, objColor, nearestGeomObject)
                    color = lightedColor
                } else {
                    color = objColor.color
                }
                if (!(objColor.reflection.x == 0.0 && objColor.reflection.y == 0.0 && objColor.reflection.z == 0.0)) {
                    val normal = nearestGeomObject.getNormalAtPoint(objPoint)!!.normalized()
                    val refDir = reflectDirection(ray.dir,  normal)
                    val refRay = Ray(objPoint + refDir * GeometryObject.epsilon, refDir)
                    val refColor = rayTrace(refRay, useLight, times - 1)
                    var lightedGlowColor = glowFromLights(objPoint, objColor, nearestGeomObject)
                    return color * (Vector(1.0) - objColor.reflection) + (refColor + lightedGlowColor) * objColor.reflection
                } else {
                    return color
                }
            } else {
                return backgroundColor.toVector()
            }
        } else {
            return backgroundColor.toVector()
        }
    }

    companion object {
        fun reflectDirection(dir: Vector, normal: Vector): Vector {
            return dir - normal * (2 * dir.dot(normal))
        }
    }
}