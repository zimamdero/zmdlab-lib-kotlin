package com.zmd.lib.gles20.obj

import android.content.Context
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import kotlin.jvm.Throws
import kotlin.math.sqrt

/**
 * to load and parsing json file in assets
 *
 * json format
 *
 * {
 *      "vertices" : [-0.5, 0.5, -0.5, ...],
 *      "indices" : [0, 1, 2, ...],
 *      "scalars" : [1.0, 0.0, 0.0, 1.0, ...],
 *      "texture_coords" : [0.0, 0.0, ...],
 *      "diffuse : [1.0, 1.0, 1.0, 1.0]"
 * }
 */

class ObjData(val name: String = "") {
    var vertices: FloatArray = FloatArray(1)
        private set
    var indices: ShortArray = ShortArray(1)
        private set
    var diffuse: FloatArray = FloatArray(1)
        private set
    var normals: FloatArray = FloatArray(1)
        private set
    var textureCodes: FloatArray = FloatArray(1)

    fun load(context: Context, fileName: String) {
        try {
            val inputStream = context.assets.open(fileName)
            val size = inputStream.available()
            val buffer = ByteArray(size)
            inputStream.read(buffer)
            inputStream.close()

            val result = String(buffer)
            parse(result)
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun parse(data: String) {
        try {
            val json = JSONObject(data)
            parseVertices(json)
            parseIndices(json)
            parseDiffuse(json)
            parseTextureCodes(json)
            calculateNormal()
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    @Throws(JSONException::class)
    private fun parseVertices(json: JSONObject) {
        if (!json.has("vertices")) return

        val list = json.getJSONArray("vertices")
        vertices = FloatArray(list.length())

        for (i in 0 until list.length()) {
            vertices[i] = list.getDouble(i).toFloat()
        }
    }

    @Throws(JSONException::class)
    private fun parseIndices(json: JSONObject) {
        if (!json.has("indices")) return

        val list = json.getJSONArray("indices")
        indices = ShortArray(list.length())

        for (i in 0 until list.length()) {
            indices[i] = list.getInt(i).toShort()
        }
    }

    @Throws(JSONException::class)
    private fun parseDiffuse(json: JSONObject) {
        if (!json.has("diffuse")) return

        val list = json.getJSONArray("diffuse")
        diffuse = FloatArray(list.length())

        for (i in 0 until list.length()) {
            diffuse[i] = list.getDouble(i).toFloat()
        }
    }

    @Throws(JSONException::class)
    private fun parseTextureCodes(json: JSONObject) {
        if (!json.has("texture_coords")) return

        val list = json.getJSONArray("texture_coords")
        textureCodes = FloatArray(list.length())

        for (i in 0 until list.length()) {
            textureCodes[i] = list.getDouble(i).toFloat()
        }
    }

    private fun calculateNormal() {
        var x = 0
        var y = 1
        var z = 2

        FloatArray(vertices.size).also { normals = it }

        for (i in vertices.indices) {
            normals[i] = 0.0f
        }

        for (i in indices.indices step 3) {
            val v1 = FloatArray(3)
            val v2 = FloatArray(3)
            val normal = FloatArray(3)

            // p1 - p0
            v1[x] = vertices[3*indices[i+1]+x] - vertices[3*indices[i]+x]
            v1[y] = vertices[3*indices[i+1]+y] - vertices[3*indices[i]+y]
            v1[z] = vertices[3*indices[i+1]+z] - vertices[3*indices[i]+z]
            // p0 - p1
            v2[x] = vertices[3*indices[i+2]+x] - vertices[3*indices[i+1]+x]
            v2[y] = vertices[3*indices[i+2]+y] - vertices[3*indices[i+1]+y]
            v2[z] = vertices[3*indices[i+2]+z] - vertices[3*indices[i+1]+z]

            normal[x] = v1[y]*v2[z] - v1[z]*v2[y]
            normal[y] = v1[z]*v2[x] - v1[x]*v2[z]
            normal[z] = v1[x]*v2[y] - v1[y]*v2[x]

            for (j in 0..2) {
                normals[3*indices[i+j]+x] =  normals[3*indices[i+j]+x] + normal[x]
                normals[3*indices[i+j]+y] =  normals[3*indices[i+j]+y] + normal[y]
                normals[3*indices[i+j]+z] =  normals[3*indices[i+j]+z] + normal[z]
            }
        }

        for(i in vertices.indices step 3) {
            val nn = FloatArray(3)
            nn[x] = normals[i+x]
            nn[y] = normals[i+y]
            nn[z] = normals[i+z]

            var len = sqrt(((nn[x]*nn[x])+(nn[y]*nn[y])+(nn[z]*nn[z])).toDouble()).toFloat()
            if (len == 0f) len = 0.00001f

            nn[x] = nn[x]/len
            nn[y] = nn[y]/len
            nn[z] = nn[z]/len

            normals[i+x] = nn[x]
            normals[i+y] = nn[y]
            normals[i+z] = nn[z]
        }
    }
}