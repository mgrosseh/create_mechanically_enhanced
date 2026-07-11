package com.mirandnyan.cme.util.math;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.engine_room.flywheel.lib.transform.Affine;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.joml.*;

public record AffineTransform(RMat3f matrix, RVec3f translation) implements Affine<AffineTransform> {

    public static final AffineTransform identity = new AffineTransform(RMat3f.identity, RVec3f.zero);

//    public static AffineTransform fromMatrix4d(Matrix4fc matrix4d) {
//        var translation = matrix4d.getTranslation(new Vector3f());
//        var matrix = matrix4d.get3x3(new Matrix3f());
//        return new AffineTransform(matrix, RVec3f.from(translation));
//    }

    public static final AffineTransform modelToBlock = identity.scale(1 / 16f).translate(-0.5f);
    public static final AffineTransform blockToModel = modelToBlock.inverse();


    public AffineTransform convertToBlockSpace() {
        return modelToBlock.mul(this).mul(blockToModel);
    }

    public AffineTransform inverse() {
        var mat = matrix.inverse();
        return new AffineTransform(mat, mat.transform(translation).negate());
    }

    @Override
    public AffineTransform rotate(Quaternionfc quaternion) {
        return new AffineTransform(matrix.rotate(quaternion), translation);
    }

    @Override
    public AffineTransform scale(float factorX, float factorY, float factorZ) {
        return new AffineTransform(
                matrix.scale(factorX, factorY, factorZ),
                translation
        );
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
        var vec = new RVec3f(x, y ,z);
        var translation = this.translation.add(matrix.transform(vec));
        return new AffineTransform(matrix, translation);
    }

    public Matrix4f asPose() {
        return new Matrix4f(
                matrix.xAxis().x(), matrix.xAxis().y(), matrix.xAxis().z(), 0,
                matrix.yAxis().x(), matrix.yAxis().y(), matrix.yAxis().z(), 0,
                matrix.zAxis().x(), matrix.zAxis().y(), matrix.zAxis().z(), 0,
                translation.x(),    translation.y(),       translation.z(), 1
        );
    }

    @OnlyIn(Dist.CLIENT)
    public void apply(PoseStack ms) {
        ms.mulPose(asPose());
    }

    public AffineTransform mul(AffineTransform rhs) {
        return new AffineTransform(
                matrix.mul(rhs.matrix),
                matrix.transform(rhs.translation).add(translation)
        );
    }
}
