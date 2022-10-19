/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.util.math;

import java.util.Optional;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;

public class RotationPropertyHelper {
    private static final int MAX = 15;
    private static final int NORTH = 0;
    private static final int EAST = 4;
    private static final int SOUTH = 8;
    private static final int WEST = 12;

    public static int getMax() {
        return 15;
    }

    public static int fromDirection(Direction direction) {
        return direction.getAxis().isVertical() ? 0 : direction.getOpposite().getHorizontal() * 4;
    }

    public static int fromYaw(float yaw) {
        return MathHelper.floor((double)((180.0f + yaw) * 16.0f / 360.0f) + 0.5) & 0xF;
    }

    public static Optional<Direction> toDirection(int rotation) {
        Direction direction = switch (rotation) {
            case 0 -> Direction.NORTH;
            case 4 -> Direction.EAST;
            case 8 -> Direction.SOUTH;
            case 12 -> Direction.WEST;
            default -> null;
        };
        return Optional.ofNullable(direction);
    }

    public static float toDegrees(int rotation) {
        return (float)rotation * 22.5f;
    }
}

