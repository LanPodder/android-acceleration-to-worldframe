package com.example.rotation.util

class Quaternion {
    var ix: Float
    var iy: Float
    var iz: Float
    var w: Float

    constructor(ix: Float, iy: Float, iz: Float, w: Float){
        this.ix = ix
        this.iy = iy
        this.iz = iz
        this.w = w
    }

    fun conjugate(q: Quaternion): Quaternion{
        return Quaternion(-q.ix, -q.iy, -q.iz, q.w)
    }

    fun rotate(v: FloatArray): FloatArray {
        var u = floatArrayOf(ix, iy, iz)
        var s = w

        return 2.0f * u.dot(v) * u +
                (s * s - u.dot(u)) * v +
                2.0f * s * u.cross(v)
    }

    override fun toString(): String {
        return "(ix: $ix, iy: $iy, iz: $iz, w: $w)"
    }
}

operator fun FloatArray.plus(other: FloatArray): FloatArray {
    var out = FloatArray(size)
    for (i in indices) out[i] = this[i] + other[i]
    return out
}

operator fun Float.times(other: FloatArray): FloatArray {
    var out = FloatArray(other.size)
    for (i in other.indices) out[i] = this * other[i]
    return out
}

infix fun FloatArray.dot(other: FloatArray): Float {
    var out = 0.0f
    for (i in indices) out += this[i] * other[i]
    return out
}

infix fun FloatArray.cross(other: FloatArray): FloatArray {
    return floatArrayOf(
        this[1] * other[2] - this[2] * other[1],
        this[2] * other[0] - this[0] * other[2],
        this[0] * other[1] - this[1] * other[0]
    )
}

operator fun FloatArray.times(x: Float): FloatArray {
    var out = FloatArray(size)
    for (i in indices) out[i] = this[i] * x
    return out
}