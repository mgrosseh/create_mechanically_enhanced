package com.mirandnyan.cme.util;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.engine_room.flywheel.lib.transform.Affine;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Quaternionfc;
import org.joml.Vector3f;

public class AffineTransform implements Affine<AffineTransform> {
    Matrix3f matrix;
    Vector3f translation;

    public AffineTransform() {
        matrix = new Matrix3f();
        translation = new Vector3f(0, 0, 0);
    }
    public AffineTransform(Matrix3f matrix, Vector3f translation) {
        this.matrix = matrix;
        this.translation = translation;
    }


    public AffineTransform inverse() {
        var mat = matrix.invert(new Matrix3f());
        var trans = mat.transform(translation, new Vector3f()).negate();
        return new AffineTransform(mat, trans);
    }

    @Override
    public AffineTransform rotate(Quaternionfc quaternion) {
        matrix.rotate(quaternion);
        translation.rotate(quaternion);
        return this;
    }

    @Override
    public AffineTransform scale(float factorX, float factorY, float factorZ) {
        matrix.scale(factorX, factorY, factorZ);
        translation.mul(factorX, factorY, factorZ);
        return this;
    }

    @Override
    public AffineTransform translate(float x, float y, float z) {
        translation.add(x, y, z);
        return this;
    }

    public Matrix4f asPose() {
        return new Matrix4f().set(matrix).setTranslation(translation);
    }

    public void apply(PoseStack ms) {
        ms.mulPose(asPose());
    }

    public AffineTransform copy() {
        return new AffineTransform(new Matrix3f(matrix), new Vector3f(translation));
    }

    public AffineTransform mul(AffineTransform transform, AffineTransform dest) {
        dest.translation = transform.translation.mul(matrix, new Vector3f()).add(translation);
        matrix.mul(transform.matrix, dest.matrix);
        return dest;
    }
    public AffineTransform mul(AffineTransform transform) {
        return mul(transform, this);
    }
}
