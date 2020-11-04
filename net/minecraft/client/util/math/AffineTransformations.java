/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.util.math;

import com.google.common.collect.Maps;
import java.util.Map;
import java.util.function.Supplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.math.AffineTransformation;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.util.Util;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Quaternion;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(value=EnvType.CLIENT)
public class AffineTransformations {
    private static final Logger LOGGER = LogManager.getLogger();
    public static final Map<Direction, AffineTransformation> DIRECTION_ROTATIONS = Util.make(Maps.newEnumMap(Direction.class), enumMap -> {
        enumMap.put(Direction.SOUTH, AffineTransformation.identity());
        enumMap.put(Direction.EAST, new AffineTransformation(null, new Quaternion(new Vector3f(0.0f, 1.0f, 0.0f), 90.0f, true), null, null));
        enumMap.put(Direction.WEST, new AffineTransformation(null, new Quaternion(new Vector3f(0.0f, 1.0f, 0.0f), -90.0f, true), null, null));
        enumMap.put(Direction.NORTH, new AffineTransformation(null, new Quaternion(new Vector3f(0.0f, 1.0f, 0.0f), 180.0f, true), null, null));
        enumMap.put(Direction.UP, new AffineTransformation(null, new Quaternion(new Vector3f(1.0f, 0.0f, 0.0f), -90.0f, true), null, null));
        enumMap.put(Direction.DOWN, new AffineTransformation(null, new Quaternion(new Vector3f(1.0f, 0.0f, 0.0f), 90.0f, true), null, null));
    });
    public static final Map<Direction, AffineTransformation> INVERTED_DIRECTION_ROTATIONS = Util.make(Maps.newEnumMap(Direction.class), enumMap -> {
        for (Direction direction : Direction.values()) {
            enumMap.put(direction, DIRECTION_ROTATIONS.get(direction).invert());
        }
    });

    public static AffineTransformation setupUvLock(AffineTransformation affineTransformation) {
        Matrix4f matrix4f = Matrix4f.translate(0.5f, 0.5f, 0.5f);
        matrix4f.multiply(affineTransformation.getMatrix());
        matrix4f.multiply(Matrix4f.translate(-0.5f, -0.5f, -0.5f));
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

