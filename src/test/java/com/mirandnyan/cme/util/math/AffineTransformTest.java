package com.mirandnyan.cme.util.math;

import com.mojang.math.Axis;
import org.joml.Matrix4f;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class AffineTransformTest {
    @Test
    public void testRotate1() {
        var actual = AffineTransform.identity.translate(1f, 2f, 3f).rotate(Axis.YP.rotationDegrees(90f)).asPose();
        var expected = new Matrix4f().translate(1f, 2f, 3f).rotate(Axis.YP.rotationDegrees(90f));

        assertEquals(expected, actual);
    }

    @Test
    public void testRotate2() {
        var actual = AffineTransform.identity
                .translate(1f, 2f, 3f)
                .rotate(Axis.YP.rotationDegrees(90f))
                .translate(3f, 5f, 11f)
                .asPose();
        var expected = new Matrix4f()
                .translate(1f, 2f, 3f)
                .rotate(Axis.YP.rotationDegrees(90f))
                .translate(3f, 5f, 11f)
                ;

        assertEquals(expected, actual);
    }

    @Test
    public void testTransitiveInvariant1() {
        var a1 = AffineTransform.identity.rotate(Axis.YP.rotationDegrees(90f));
        var a2 = AffineTransform.identity.translate(3f, 5f, 11f);

        var actual = AffineTransform.identity
                .rotate(Axis.YP.rotationDegrees(90f))
                .translate(3f, 5f, 11f);

        assertEquals(a1.mul(a2), actual);
    }

    @Test
    public void testTransitiveInvariant2() {
        var a1 = AffineTransform.identity.translate(1f, 2f, 3f);
        var a2 = AffineTransform.identity.rotate(Axis.YP.rotationDegrees(90f));
        var a3 = AffineTransform.identity.translate(3f, 5f, 11f);

        var actual = AffineTransform.identity
                .translate(1f, 2f, 3f)
                .rotate(Axis.YP.rotationDegrees(90f))
                .translate(3f, 5f, 11f);

        assertEquals(a1.mul(a2).mul(a3), actual);

        assertEquals(a1.mul(a2.mul(a3)), actual);
    }

    @Test
    public void testRotateAround() {
        var actual = AffineTransform.identity.translate(1f, 2f, 3f).rotateAround(Axis.YP.rotationDegrees(90f), 7f, 11f, 13f).asPose();
        var expected = new Matrix4f().translate(1f, 2f, 3f).rotateAround(Axis.YP.rotationDegrees(90f), 7f, 11f, 13f);

        assertEquals(expected, actual);
    }

    @Test
    public void testScale() {
        var actual = AffineTransform.identity.translate(1, 2, 3).scale(2).translate(5, 7, 11).asPose();
        var expected = new Matrix4f().translate(1, 2, 3).scale(2).translate(5, 7, 11);

        assertEquals(expected, actual);
    }

}
