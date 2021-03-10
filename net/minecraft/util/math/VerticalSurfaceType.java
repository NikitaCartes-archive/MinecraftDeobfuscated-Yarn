/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.util.math;

import com.mojang.serialization.Codec;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.math.Direction;

public enum VerticalSurfaceType implements StringIdentifiable
{
    CEILING(Direction.UP, 1, "ceiling"),
    FLOOR(Direction.DOWN, -1, "floor");

    public static final Codec<VerticalSurfaceType> CODEC;
    private final Direction direction;
    private final int offset;
    private final String name;
    private static final VerticalSurfaceType[] VALUES;

    private VerticalSurfaceType(Direction direction, int offset, String name) {
        this.direction = direction;
        this.offset = offset;
        this.name = name;
    }

    public Direction getDirection() {
        return this.direction;
    }

    public int getOffset() {
        return this.offset;
    }

    public static VerticalSurfaceType byName(String name) {
        for (VerticalSurfaceType verticalSurfaceType : VALUES) {
            if (!verticalSurfaceType.asString().equals(name)) continue;
            return verticalSurfaceType;
        }
        throw new IllegalArgumentException("Unknown Surface type: " + name);
    }

    @Override
    public String asString() {
        return this.name;
    }

    static {
        CODEC = StringIdentifiable.createCodec(VerticalSurfaceType::values, VerticalSurfaceType::byName);
        VALUES = VerticalSurfaceType.values();
    }
}

