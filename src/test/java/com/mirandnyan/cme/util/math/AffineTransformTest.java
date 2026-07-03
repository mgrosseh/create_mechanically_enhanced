package com.mirandnyan.cme.util.math;

import com.mojang.math.Axis;
import org.checkerframework.checker.units.qual.A;
import org.joml.Matrix4f;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class AffineTransformTest {
    @Test
    public void testRotate1() {
        var actual = new AffineTransform().translate(1f, 2f, 3f).rotate(Axis.YP.rotationDegrees(90f)).asPose();
        var expected = new Matrix4f().translate(1f, 2f, 3f).rotate(Axis.YP.rotationDegrees(90f));

        assertEquals(expected, actual);
    }

    @Test
    public void testRotate2() {
        var actual = new AffineTransform()
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
        var a1 = new AffineTransform().rotate(Axis.YP.rotationDegrees(90f));
        var a2 = new AffineTransform().translate(3f, 5f, 11f);

        var actual = new AffineTransform()
                .rotate(Axis.YP.rotationDegrees(90f))
                .translate(3f, 5f, 11f);

        assertEquals(a1.mul(a2), actual);
    }

    @Test
    public void testTransitiveInvariant2() {
        var a1 = new AffineTransform().translate(1f, 2f, 3f);
        var a2 = new AffineTransform().rotate(Axis.YP.rotationDegrees(90f));
        var a3 = new AffineTransform().translate(3f, 5f, 11f);

        var actual = new AffineTransform()
                .translate(1f, 2f, 3f)
                .rotate(Axis.YP.rotationDegrees(90f))
                .translate(3f, 5f, 11f);

        assertEquals(a3.mul(a2).mul(a1), actual);

        assertEquals(a3.mul(a2.mul(a1)), actual);
    }

    @Test
    public void testRotateAround() {
        var actual = new AffineTransform().translate(1f, 2f, 3f).rotateAround(Axis.YP.rotationDegrees(90f), 7f, 11f, 13f).asPose();
        var expected = new Matrix4f().translate(1f, 2f, 3f).rotateAround(Axis.YP.rotationDegrees(90f), 7f, 11f, 13f);

        assertEquals(expected, actual);
    }

}
