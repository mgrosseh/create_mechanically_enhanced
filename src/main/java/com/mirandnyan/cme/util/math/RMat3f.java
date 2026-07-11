package com.mirandnyan.cme.util.math;

import org.joml.Quaternionfc;

import java.util.Optional;

public record RMat3f(RVec3f xAxis, RVec3f yAxis, RVec3f zAxis) {
    public static final RMat3f identity = new RMat3f(RVec3f.xAxis, RVec3f.yAxis, RVec3f.zAxis);
    public static final RMat3f zero = new RMat3f(RVec3f.zero, RVec3f.zero, RVec3f.zero);

    public static RMat3f from(Quaternionfc rotation) {
        // format e.g: dxy = 2 * x * y

        var xx = rotation.x() * rotation.x();
        var yy = rotation.y() * rotation.y();
        var zz = rotation.z() * rotation.z();
        var ww = rotation.w() * rotation.w();

        var dx = rotation.x() + rotation.x();
        var dy = rotation.y() + rotation.y();
        var dz = rotation.z() + rotation.z();

        var dxx = xx + xx;
        var dxy = rotation.x() * dy;
        var dxz = rotation.x() * dz;
        var dyy = yy + yy;
        var dyz = rotation.y() * dz;
        var dzz = zz + zz;
        var dwx = rotation.w() * dx;
        var dwy = rotation.w() * dy;
        var dwz = rotation.w() * dz;

        // norm
        var nn = xx + yy + zz + ww;

        var xAxis = new RVec3f(nn - (dyy + dzz), dxy + dwz, dxz - dwy);
        var yAxis = new RVec3f(dxy - dwz, nn - (dxx + dzz), dyz + dwx);
        var zAxis = new RVec3f(dxz + dwy, dyz - dwx, nn - (dxx + dyy));

        return new RMat3f(xAxis, yAxis, zAxis);
    }


    public Optional<RMat3f> tryInverse() {
        var yz = yAxis.cross(zAxis);
        var zx = zAxis.cross(xAxis);
        var xy = xAxis.cross(yAxis);
        var det = zAxis.dot(xy);
        if (det == 0)
            return Optional.empty();
        var invDet = 1 / det;
        return Optional.of(new RMat3f(
                yz.scale(invDet),
                zx.scale(invDet),
                xy.scale(invDet)
        ));
    }

    public RMat3f inverse() {
        return tryInverse().orElse(zero);
    }

    public RVec3f transform(RVec3f vec) {
        return xAxis.fma(vec.x(), yAxis.fma(vec.y(), zAxis.scale(vec.z())));
    }

    public RMat3f rotate(Quaternionfc quaternion) {
        return mul(from(quaternion));
    }

    public RMat3f mul(RMat3f rhs) {
        return new RMat3f(
                transform(rhs.xAxis),
                transform(rhs.yAxis),
                transform(rhs.zAxis)
        );
    }

    public RMat3f scale(float factorX, float factorY, float factorZ) {
        return new RMat3f(xAxis.scale(factorX), yAxis.scale(factorY), zAxis.scale(factorZ));
    }
}
