package raytracing

import raytracing.Geometry.GeometryObject
import raytracing.Geometry.PhysicalColor
import raytracing.lightning.AmbientLight
import raytracing.lightning.Light
import raytracing.lightning.toColor
import raytracing.lightning.toVector
import java.awt.Color
import kotlin.math.max
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

    private fun colorFromLight(
        light: Light,
        objPoint: Vector,
        ): Vector {
        if (light is AmbientLight) {
            return light.evaluateLightingAt(objPoint, this)
        }
        val directionToLight = light.directionAt(objPoint)
        val ray = Ray(objPoint + directionToLight * 0.00001, directionToLight)
        var (minT, beforeLightGeomObject: GeometryObject?) = intersect(ray)
        if (minT > 0) {
            val beforeLightObjPoint = ray.from + ray.dir * minT
            val beforeLightObjColor = beforeLightGeomObject!!.getColorAtPoint(beforeLightObjPoint)
            if (beforeLightObjColor != null && !beforeLightObjColor.transparent.isZero()) {
                return (Vector(1.0) - beforeLightObjColor.transparent) * beforeLightObjColor.color + beforeLightObjColor.transparent * colorFromLight(light, beforeLightObjPoint)
            } else {
                return Vector(0.0)
            }
        }
        return light.evaluateLightingAt(objPoint, this)
    }

    private fun colorFromLights(
        objPoint: Vector,
        objColor: PhysicalColor,
        nearestGeomObject: GeometryObject
    ): Vector {
        var lightedColor = Vector(0.0)
        for (light in lights) {
            val lightColor = colorFromLight(light, objPoint)
            val diffuse =
                if (light is AmbientLight) {
                    lightColor * objColor.color
                } else {
                    val normal = nearestGeomObject.getNormalAtPoint(objPoint)!!.normalized()
                    val lightDir = light.directionAt(objPoint).normalized()
                    val cos = lightDir.dot(normal)
                    lightColor * objColor.color * max(cos, 0.0)
                }
            lightedColor += diffuse
        }
//        lightedColor = Vector(min(lightedColor.x, 1.0), min(lightedColor.y, 1.0), min(lightedColor.z, 1.0))
        return lightedColor
    }

    fun glowFromLights(
        objPoint: Vector,
        objColor: PhysicalColor,
        nearestGeomObject: GeometryObject
    ): Vector {
        var lightedColor = Vector(0.0)
        for (light in lights) {
            if (light is AmbientLight) {
                continue
            }
            val lightColor = colorFromLight(light, objPoint)

            val normal = nearestGeomObject.getNormalAtPoint(objPoint)!!.normalized()
            val lightDir = -light.directionAt(objPoint)
            val refLightDir = reflectDirection(lightDir, normal).normalized()
            val dirToCam = (camera.position - objPoint).normalized()
            val cos = max(0.0, dirToCam.dot(refLightDir))
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
                val diffuseColor = if (useLight) {
                    var lightedColor = colorFromLights(objPoint, objColor, nearestGeomObject)
                    lightedColor
                } else {
                    objColor.color
                }
                if (useLight && !(objColor.reflection.isZero())) {
                    val normal = nearestGeomObject.getNormalAtPoint(objPoint)!!.normalized()
                    val refDir = reflectDirection(ray.dir,  normal)
                    val refRay = Ray(objPoint + refDir * GeometryObject.epsilon, refDir)
                    val refColor = rayTrace(refRay, useLight, times - 1)
                    var lightedGlowColor = glowFromLights(objPoint, objColor, nearestGeomObject)
                    return diffuseColor * (Vector(1.0) - objColor.reflection) + (refColor + lightedGlowColor) * objColor.reflection
                } else {
                    return diffuseColor
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