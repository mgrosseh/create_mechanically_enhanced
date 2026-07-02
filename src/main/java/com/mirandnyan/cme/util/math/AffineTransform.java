package com.mirandnyan.cme.util.math;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.engine_room.flywheel.lib.transform.Affine;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.joml.*;

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
    public AffineTransform(Matrix4f matrix) {
        with(matrix);
    }

    public static AffineTransform withMatrix(Matrix3f matrix) {
        return new AffineTransform(matrix, new Vector3f(0, 0, 0));
    }
    public static AffineTransform withTranslation(Vector3f translation) {
        return new AffineTransform(new Matrix3f(), translation);
    }

    public static AffineTransform modelToBlock() {
        return new AffineTransform().scale(1 / 16f).translate(-0.5f);
    }
    public static AffineTransform blockToModel() {
        return modelToBlock().inverse();
    }


    public AffineTransform convertToBlockSpace() {
        return modelToBlock().mul(this).mul(blockToModel());
    }

    public AffineTransform inverse() {
        var mat = matrix.invert(new Matrix3f());
        var trans = mat.transform(translation, new Vector3f()).negate();
        return new AffineTransform(mat, trans);
    }

    public AffineTransform with(Matrix4fc matrix) {
        this.translation = matrix.getTranslation(new Vector3f(0, 0, 0));
        this.matrix = matrix.get3x3(new Matrix3f());
        return this;
    }

    @Override
    public AffineTransform rotate(Quaternionfc quaternion) {
        matrix.rotate(quaternion);
        //translation.rotate(quaternion);
        return this;
    }

    @Override
    public AffineTransform rotateAround(Quaternionfc quaternion, float x, float y, float z) {
        var mat = asPose();
        return this.with(mat.rotateAround(quaternion, x, y, z));
    }

    @Override
    public AffineTransform scale(float factorX, float factorY, float factorZ) {
        matrix.scale(factorX, factorY, factorZ);
        translation.mul(factorX, factorY, factorZ);
        return this;
    }

    public AffineTransform scaleAround(float factorX, float factorY, float factorZ, float x, float y, float z) {
        return translate(x, y, z).scale(factorX, factorY, factorZ).translateBack(x, y, z);
    }
    public AffineTransform scaleAround(float factor, float x, float y, float z) {
        return scaleAround(factor, factor, factor, x, y, z);
    }
    public AffineTransform scaleAround(float factor, Vector3f vec) {
        return scaleAround(factor, vec.x, vec.y, vec.z);
    }

    @Override
    public AffineTransform translate(float x, float y, float z) {
//        var vec = new Vector3f(x, y ,z);
//        translation.add(vec.mul(matrix));
        translation.add(x, y, z);
        return this;
    }

    public Matrix4f asPose() {
        return new Matrix4f().set(matrix).setTranslation(translation);
    }

    @OnlyIn(Dist.CLIENT)
    public void apply(PoseStack ms) {
        ms.mulPose(asPose());
    }

    public AffineTransform copy() {
        return new AffineTransform(new Matrix3f(matrix), new Vector3f(translation));
    }

    public AffineTransform mul(AffineTransform transform, AffineTransform dest) {
        //return new AffineTransform(this.asPose().mul(transform.asPose()));

        dest.translation = transform.translation.mul(matrix, new Vector3f()).add(translation);
        matrix.mul(transform.matrix, dest.matrix);
        return dest;
    }
    public AffineTransform mul(AffineTransform transform) {
        return mul(transform, this);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!(obj instanceof AffineTransform affine))
            return false;
        return matrix.equals(affine.matrix) && translation.equals(affine.translation);
    }
}
