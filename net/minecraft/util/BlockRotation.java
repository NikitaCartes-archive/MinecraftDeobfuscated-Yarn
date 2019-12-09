/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.util;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import net.minecraft.util.math.Direction;

public enum BlockRotation {
    NONE,
    CLOCKWISE_90,
    CLOCKWISE_180,
    COUNTERCLOCKWISE_90;


    public BlockRotation rotate(BlockRotation rotation) {
        switch (rotation) {
            case CLOCKWISE_180: {
                switch (this) {
                    case NONE: {
                        return CLOCKWISE_180;
                    }
                    case CLOCKWISE_90: {
                        return COUNTERCLOCKWISE_90;
                    }
                    case CLOCKWISE_180: {
                        return NONE;
                    }
                    case COUNTERCLOCKWISE_90: {
                        return CLOCKWISE_90;
                    }
                }
            }
            case COUNTERCLOCKWISE_90: {
                switch (this) {
                    case NONE: {
                        return COUNTERCLOCKWISE_90;
                    }
                    case CLOCKWISE_90: {
                        return NONE;
                    }
                    case CLOCKWISE_180: {
                        return CLOCKWISE_90;
                    }
                    case COUNTERCLOCKWISE_90: {
                        return CLOCKWISE_180;
                    }
                }
            }
            case CLOCKWISE_90: {
                switch (this) {
                    case NONE: {
                        return CLOCKWISE_90;
                    }
                    case CLOCKWISE_90: {
                        return CLOCKWISE_180;
                    }
                    case CLOCKWISE_180: {
                        return COUNTERCLOCKWISE_90;
                    }
                    case COUNTERCLOCKWISE_90: {
                        return NONE;
                    }
                }
            }
        }
        return this;
    }

    public Direction rotate(Direction direction) {
        if (direction.getAxis() == Direction.Axis.Y) {
            return direction;
        }
        switch (this) {
            case CLOCKWISE_180: {
                return direction.getOpposite();
            }
            case COUNTERCLOCKWISE_90: {
                return direction.rotateYCounterclockwise();
            }
            case CLOCKWISE_90: {
                return direction.rotateYClockwise();
            }
        }
        return direction;
    }

    public int rotate(int rotation, int fullTurn) {
        switch (this) {
            case CLOCKWISE_180: {
                return (rotation + fullTurn / 2) % fullTurn;
            }
            case COUNTERCLOCKWISE_90: {
                return (rotation + fullTurn * 3 / 4) % fullTurn;
            }
            case CLOCKWISE_90: {
                return (rotation + fullTurn / 4) % fullTurn;
            }
        }
        return rotation;
    }

    public static BlockRotation random(Random random) {
        BlockRotation[] blockRotations = BlockRotation.values();
        return blockRotations[random.nextInt(blockRotations.length)];
    }

    public static List<BlockRotation> randomRotationOrder(Random random) {
        ArrayList<BlockRotation> list = Lists.newArrayList(BlockRotation.values());
        Collections.shuffle(list, random);
        return list;
    }
}

