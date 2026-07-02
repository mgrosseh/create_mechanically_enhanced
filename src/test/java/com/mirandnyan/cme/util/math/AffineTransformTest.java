package com.mirandnyan.cme.util.math;

import com.mojang.math.Axis;
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
    public void testRotateAround() {
        var actual = new AffineTransform().translate(1f, 2f, 3f).rotateAround(Axis.YP.rotationDegrees(90f), 7f, 11f, 13f).asPose();
        var expected = new Matrix4f().translate(1f, 2f, 3f).rotateAround(Axis.YP.rotationDegrees(90f), 7f, 11f, 13f);

        assertEquals(expected, actual);
    }

}
