/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.util.math;

import com.google.common.collect.Maps;
import com.mojang.logging.LogUtils;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.util.Util;
import net.minecraft.util.math.AffineTransformation;
import net.minecraft.util.math.Direction;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.slf4j.Logger;

public class AffineTransformations {
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final Map<Direction, AffineTransformation> DIRECTION_ROTATIONS = Util.make(Maps.newEnumMap(Direction.class), map -> {
        map.put(Direction.SOUTH, AffineTransformation.identity());
        map.put(Direction.EAST, new AffineTransformation(null, new Quaternionf().rotateY(1.5707964f), null, null));
        map.put(Direction.WEST, new AffineTransformation(null, new Quaternionf().rotateY(-1.5707964f), null, null));
        map.put(Direction.NORTH, new AffineTransformation(null, new Quaternionf().rotateY((float)Math.PI), null, null));
        map.put(Direction.UP, new AffineTransformation(null, new Quaternionf().rotateX(-1.5707964f), null, null));
        map.put(Direction.DOWN, new AffineTransformation(null, new Quaternionf().rotateX(1.5707964f), null, null));
    });
    public static final Map<Direction, AffineTransformation> INVERTED_DIRECTION_ROTATIONS = Util.make(Maps.newEnumMap(Direction.class), map -> {
        for (Direction direction : Direction.values()) {
            map.put(direction, DIRECTION_ROTATIONS.get(direction).invert());
        }
    });

    public static AffineTransformation setupUvLock(AffineTransformation affineTransformation) {
        Matrix4f matrix4f = new Matrix4f().translation(0.5f, 0.5f, 0.5f);
        matrix4f.mul(affineTransformation.getMatrix());
        matrix4f.translate(-0.5f, -0.5f, -0.5f);
        return new AffineTransformation(matrix4f);
    }

    public static AffineTransformation method_35829(AffineTransformation affineTransformation) {
        Matrix4f matrix4f = new Matrix4f().translation(-0.5f, -0.5f, -0.5f);
        matrix4f.mul(affineTransformation.getMatrix());
        matrix4f.translate(0.5f, 0.5f, 0.5f);
        return new AffineTransformation(matrix4f);
    }

    public static AffineTransformation uvLock(AffineTransformation affineTransformation, Direction direction, Supplier<String> supplier) {
        Direction direction2 = Direction.transform(affineTransformation.getMatrix(), direction);
        AffineTransformation affineTransformation2 = affineTransformation.invert();
        if (affineTransformation2 == null) {
            LOGGER.warn(supplier.get());
            return new AffineTransformation(null, null, new Vector3f(0.0f, 0.0f, 0.0f), null);
        }
        AffineTransformation affineTransformation3 = INVERTED_DIRECTION_ROTATIONS.get(direction).multiply(affineTransformation2).multiply(DIRECTION_ROTATIONS.get(direction2));
        return AffineTransformations.setupUvLock(affineTransformation3);
    }
}

