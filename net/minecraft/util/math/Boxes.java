/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.util.math;

import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;

public class Boxes {
    public static Box stretch(Box box, Direction direction, double d) {
        double e = d * (double)direction.getDirection().offset();
        double f = Math.min(e, 0.0);
        double g = Math.max(e, 0.0);
        switch (direction) {
            case WEST: {
                return new Box(box.x1 + f, box.y1, box.z1, box.x1 + g, box.y2, box.z2);
            }
            case EAST: {
                return new Box(box.x2 + f, box.y1, box.z1, box.x2 + g, box.y2, box.z2);
            }
            case DOWN: {
                return new Box(box.x1, box.y1 + f, box.z1, box.x2, box.y1 + g, box.z2);
            }
            default: {
                return new Box(box.x1, box.y2 + f, box.z1, box.x2, box.y2 + g, box.z2);
            }
            case NORTH: {
                return new Box(box.x1, box.y1, box.z1 + f, box.x2, box.y2, box.z1 + g);
            }
            case SOUTH: 
        }
        return new Box(box.x1, box.y1, box.z2 + f, box.x2, box.y2, box.z2 + g);
    }
}

