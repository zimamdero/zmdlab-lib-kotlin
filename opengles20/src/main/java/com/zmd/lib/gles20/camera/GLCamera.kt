package com.zmd.lib.gles20.camera

import android.opengl.Matrix
import kotlin.math.sqrt

class GLCamera(var type: Int) {
    companion object {
        val CAMERA_TYPE_ORBIT = 0
        val CAMERA_TYPE_TRACKING = 1
    }

    private val matrix = FloatArray(16)
    private val up = FloatArray(4)
    private val right = FloatArray(4)
    private val normal = FloatArray(4)
    private var position = FloatArray(4)
    private var home = FloatArray(4)
    private var azimuth = 0f
    private var elevation = 0f
    private var steps = 0f

    fun goHome(home: FloatArray) {
        if(home != null) {
            this.home = home
        }
        setPosition(this.home)
        setAzimuth(0f)
        setElevation(0f)
        steps = 0f
    }

    fun dolly(s: Float) {
        val p = position
        val n = FloatArray(3)
        val step = s - steps
        normalize(n, normal)

        val newPos = FloatArray(3)

        if(type == CAMERA_TYPE_TRACKING) {
            newPos[0] = p[0] - step * n[0]
            newPos[1] = p[1] - step * n[1]
            newPos[2] = p[2] - step * n[2]
        } else {
            newPos[0] = p[0]
            newPos[1] = p[1]
            newPos[2] = p[2] - step
        }

        setPosition(newPos)
        steps = s
    }

    fun setPosition(p: FloatArray) {
        position = p
        update()
    }

    fun setAzimuth(azimuth: Float) {
        //float az = azimuth - this.azimuth;
        //this.azimuth += az;
        this.azimuth = azimuth

        if(this.azimuth > 360f || this.azimuth < - 360f) {
            this.azimuth = this.azimuth % 360f
        }

        update()
    }

    fun setElevation(elevation: Float) {
        //float el = elevation - this.elevation;
        //this.elevation += el;
        this.elevation = elevation

        if(this.elevation > 360f || this.elevation < -360f) {
            this.elevation = this.elevation % 360f
        }

        update()
    }

    fun update() {
        if(type == CAMERA_TYPE_TRACKING) {
            Matrix.setIdentityM(matrix, 0)
            Matrix.translateM(matrix, 0, position[0], position[1], position[2])
            Matrix.rotateM(matrix, 0, azimuth, 0f, 1.0f, 0f)
            Matrix.rotateM(matrix, 0, elevation, 1.0f, 0f, 0f)
        } else {
            Matrix.setIdentityM(matrix, 0)
            Matrix.rotateM(matrix, 0, azimuth, 0f, 1.0f, 0f)
            Matrix.rotateM(matrix, 0, elevation, 1.0f, 0f, 0f)
            Matrix.translateM(matrix, 0, position[0], position[1], position[2])
        }

        Matrix.multiplyMV(right, 0, matrix, 0, floatArrayOf(1f, 0f, 0f, 0f), 0)
        Matrix.multiplyMV(up, 0, matrix, 0, floatArrayOf(0f, 1f, 0f, 0f), 0)
        Matrix.multiplyMV(normal, 0, matrix, 0, floatArrayOf(0f, 0f, 1f, 0f), 0)

        if(type == CAMERA_TYPE_TRACKING) {
            Matrix.multiplyMV(position, 0, matrix, 0, floatArrayOf(0f, 0f, 0f, 1f), 0)
        }
    }

    fun normalize(result: FloatArray, origin: FloatArray) {
        val c = origin[0]
        val d = origin[1]
        val e = origin[2]
        var g = sqrt((c*c + d*d + e*e).toDouble()).toFloat()

        if(g > 0) {
            g = 1 / g
            result[0] = c * g
            result[1] = d * g
            result[2] = e * g
        } else {
            result[0] = 0f
            result[1] = 0f
            result[2] = 0f
        }
    }

    fun getViewM(): FloatArray {
        val result = FloatArray(16)
        Matrix.invertM(result, 0, matrix, 0)
        return result
    }
}