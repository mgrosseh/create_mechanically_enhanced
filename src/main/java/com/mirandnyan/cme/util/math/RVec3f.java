package com.mirandnyan.cme.util.math;

import org.joml.Matrix3f;
import org.joml.Vector3f;
import org.joml.Vector3fc;

public record RVec3f(float x, float y, float z) {

    public static final RVec3f zero = new RVec3f(0, 0 , 0);
    public static final RVec3f xAxis = new RVec3f(1, 0 ,0);
    public static final RVec3f yAxis = new RVec3f(0, 1 ,0);
    public static final RVec3f zAxis = new RVec3f(0, 0 ,1);

    public static RVec3f from(Vector3fc vec) {
        return new RVec3f(vec.x(), vec.y(), vec.z());
    }
    public Vector3f toVector3f() {
        return new Vector3f(x, y, z);
    }

    public RVec3f scale(float factor) {
        return new RVec3f(x * factor, y * factor, z * factor);
    }
    public RVec3f scale(float factorX, float factorY, float factorZ) {
        return new RVec3f(x * factorX, y * factorY, z * factorZ);
    }

    public RVec3f add(RVec3f vec) {
        return new RVec3f(x + vec.x, y + vec.y, z + vec.z);
    }
    public RVec3f add(Vector3f vec) {
        return add(from(vec));
    }

    public RVec3f mul(Matrix3f matrix) {
        return RVec3f.from(toVector3f().mul(matrix));
    }

    public RVec3f cross(RVec3f rhs) {
        return new RVec3f(y * rhs.z - rhs.y * z, z * rhs.x - rhs.z * x, x * rhs.y - rhs.x * y);
    }
    public float dot(RVec3f rhs) {
        return x * rhs.x + y * rhs.y + z * rhs.z;
    }

    public RVec3f fma(float num, RVec3f vec) {
        return new RVec3f(Math.fma(x, num, vec.x), Math.fma(y, num, vec.y), Math.fma(z, num, vec.z));
    }

    public RVec3f negate() {
        return new RVec3f(-x, -y, -z);
    }
}
