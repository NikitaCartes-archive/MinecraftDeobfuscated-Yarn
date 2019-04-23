/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.util.math;

import com.google.common.collect.Iterators;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;
import net.minecraft.util.SnakeCaseIdentifiable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3i;
import org.jetbrains.annotations.Nullable;

public enum Direction implements SnakeCaseIdentifiable
{
    DOWN(0, 1, -1, "down", AxisDirection.NEGATIVE, Axis.Y, new Vec3i(0, -1, 0)),
    UP(1, 0, -1, "up", AxisDirection.POSITIVE, Axis.Y, new Vec3i(0, 1, 0)),
    NORTH(2, 3, 2, "north", AxisDirection.NEGATIVE, Axis.Z, new Vec3i(0, 0, -1)),
    SOUTH(3, 2, 0, "south", AxisDirection.POSITIVE, Axis.Z, new Vec3i(0, 0, 1)),
    WEST(4, 5, 1, "west", AxisDirection.NEGATIVE, Axis.X, new Vec3i(-1, 0, 0)),
    EAST(5, 4, 3, "east", AxisDirection.POSITIVE, Axis.X, new Vec3i(1, 0, 0));

    private final int id;
    private final int idOpposite;
    private final int idHorizontal;
    private final String name;
    private final Axis axis;
    private final AxisDirection direction;
    private final Vec3i vector;
    private static final Direction[] ALL;
    private static final Map<String, Direction> NAME_MAP;
    private static final Direction[] ID_TO_DIRECTION;
    private static final Direction[] HORIZONTAL;
    private static final Long2ObjectMap<Direction> VECTOR_TO_DIRECTION;

    private Direction(int j, int k, int l, String string2, AxisDirection axisDirection, Axis axis, Vec3i vec3i) {
        this.id = j;
        this.idHorizontal = l;
        this.idOpposite = k;
        this.name = string2;
        this.axis = axis;
        this.direction = axisDirection;
        this.vector = vec3i;
    }

    public static Direction[] getEntityFacingOrder(Entity entity) {
        Direction direction3;
        float f = entity.getPitch(1.0f) * ((float)Math.PI / 180);
        float g = -entity.getYaw(1.0f) * ((float)Math.PI / 180);
        float h = MathHelper.sin(f);
        float i = MathHelper.cos(f);
        float j = MathHelper.sin(g);
        float k = MathHelper.cos(g);
        boolean bl = j > 0.0f;
        boolean bl2 = h < 0.0f;
        boolean bl3 = k > 0.0f;
        float l = bl ? j : -j;
        float m = bl2 ? -h : h;
        float n = bl3 ? k : -k;
        float o = l * i;
        float p = n * i;
        Direction direction = bl ? EAST : WEST;
        Direction direction2 = bl2 ? UP : DOWN;
        Direction direction4 = direction3 = bl3 ? SOUTH : NORTH;
        if (l > n) {
            if (m > o) {
                return Direction.method_10145(direction2, direction, direction3);
            }
            if (p > m) {
                return Direction.method_10145(direction, direction3, direction2);
            }
            return Direction.method_10145(direction, direction2, direction3);
        }
        if (m > p) {
            return Direction.method_10145(direction2, direction3, direction);
        }
        if (o > m) {
            return Direction.method_10145(direction3, direction, direction2);
        }
        return Direction.method_10145(direction3, direction2, direction);
    }

    private static Direction[] method_10145(Direction direction, Direction direction2, Direction direction3) {
        return new Direction[]{direction, direction2, direction3, direction3.getOpposite(), direction2.getOpposite(), direction.getOpposite()};
    }

    public int getId() {
        return this.id;
    }

    public int getHorizontal() {
        return this.idHorizontal;
    }

    public AxisDirection getDirection() {
        return this.direction;
    }

    public Direction getOpposite() {
        return Direction.byId(this.idOpposite);
    }

    @Environment(value=EnvType.CLIENT)
    public Direction rotateClockwise(Axis axis) {
        switch (axis) {
            case X: {
                if (this == WEST || this == EAST) {
                    return this;
                }
                return this.rotateXClockwise();
            }
            case Y: {
                if (this == UP || this == DOWN) {
                    return this;
                }
                return this.rotateYClockwise();
            }
            case Z: {
                if (this == NORTH || this == SOUTH) {
                    return this;
                }
                return this.rotateZClockwise();
            }
        }
        throw new IllegalStateException("Unable to get CW facing for axis " + axis);
    }

    public Direction rotateYClockwise() {
        switch (this) {
            case NORTH: {
                return EAST;
            }
            case EAST: {
                return SOUTH;
            }
            case SOUTH: {
                return WEST;
            }
            case WEST: {
                return NORTH;
            }
        }
        throw new IllegalStateException("Unable to get Y-rotated facing of " + this);
    }

    @Environment(value=EnvType.CLIENT)
    private Direction rotateXClockwise() {
        switch (this) {
            case UP: {
                return NORTH;
            }
            case NORTH: {
                return DOWN;
            }
            case DOWN: {
                return SOUTH;
            }
            case SOUTH: {
                return UP;
            }
        }
        throw new IllegalStateException("Unable to get X-rotated facing of " + this);
    }

    @Environment(value=EnvType.CLIENT)
    private Direction rotateZClockwise() {
        switch (this) {
            case UP: {
                return EAST;
            }
            case EAST: {
                return DOWN;
            }
            case DOWN: {
                return WEST;
            }
            case WEST: {
                return UP;
            }
        }
        throw new IllegalStateException("Unable to get Z-rotated facing of " + this);
    }

    public Direction rotateYCounterclockwise() {
        switch (this) {
            case NORTH: {
                return WEST;
            }
            case EAST: {
                return NORTH;
            }
            case SOUTH: {
                return EAST;
            }
            case WEST: {
                return SOUTH;
            }
        }
        throw new IllegalStateException("Unable to get CCW facing of " + this);
    }

    public int getOffsetX() {
        return this.axis == Axis.X ? this.direction.offset() : 0;
    }

    public int getOffsetY() {
        return this.axis == Axis.Y ? this.direction.offset() : 0;
    }

    public int getOffsetZ() {
        return this.axis == Axis.Z ? this.direction.offset() : 0;
    }

    public String getName() {
        return this.name;
    }

    public Axis getAxis() {
        return this.axis;
    }

    @Nullable
    @Environment(value=EnvType.CLIENT)
    public static Direction byName(@Nullable String string) {
        if (string == null) {
            return null;
        }
        return NAME_MAP.get(string.toLowerCase(Locale.ROOT));
    }

    public static Direction byId(int i) {
        return ID_TO_DIRECTION[MathHelper.abs(i % ID_TO_DIRECTION.length)];
    }

    public static Direction fromHorizontal(int i) {
        return HORIZONTAL[MathHelper.abs(i % HORIZONTAL.length)];
    }

    @Nullable
    public static Direction fromVector(int i, int j, int k) {
        return (Direction)VECTOR_TO_DIRECTION.get(BlockPos.asLong(i, j, k));
    }

    public static Direction fromRotation(double d) {
        return Direction.fromHorizontal(MathHelper.floor(d / 90.0 + 0.5) & 3);
    }

    public static Direction from(Axis axis, AxisDirection axisDirection) {
        switch (axis) {
            case X: {
                return axisDirection == AxisDirection.POSITIVE ? EAST : WEST;
            }
            case Y: {
                return axisDirection == AxisDirection.POSITIVE ? UP : DOWN;
            }
        }
        return axisDirection == AxisDirection.POSITIVE ? SOUTH : NORTH;
    }

    public float asRotation() {
        return (this.idHorizontal & 3) * 90;
    }

    public static Direction random(Random random) {
        return Direction.values()[random.nextInt(Direction.values().length)];
    }

    public static Direction getFacing(double d, double e, double f) {
        return Direction.getFacing((float)d, (float)e, (float)f);
    }

    public static Direction getFacing(float f, float g, float h) {
        Direction direction = NORTH;
        float i = Float.MIN_VALUE;
        for (Direction direction2 : ALL) {
            float j = f * (float)direction2.vector.getX() + g * (float)direction2.vector.getY() + h * (float)direction2.vector.getZ();
            if (!(j > i)) continue;
            i = j;
            direction = direction2;
        }
        return direction;
    }

    public String toString() {
        return this.name;
    }

    @Override
    public String toSnakeCase() {
        return this.name;
    }

    public static Direction get(AxisDirection axisDirection, Axis axis) {
        for (Direction direction : Direction.values()) {
            if (direction.getDirection() != axisDirection || direction.getAxis() != axis) continue;
            return direction;
        }
        throw new IllegalArgumentException("No such direction: " + (Object)((Object)axisDirection) + " " + axis);
    }

    public Vec3i getVector() {
        return this.vector;
    }

    static {
        ALL = Direction.values();
        NAME_MAP = Arrays.stream(ALL).collect(Collectors.toMap(Direction::getName, direction -> direction));
        ID_TO_DIRECTION = (Direction[])Arrays.stream(ALL).sorted(Comparator.comparingInt(direction -> direction.id)).toArray(Direction[]::new);
        HORIZONTAL = (Direction[])Arrays.stream(ALL).filter(direction -> direction.getAxis().isHorizontal()).sorted(Comparator.comparingInt(direction -> direction.idHorizontal)).toArray(Direction[]::new);
        VECTOR_TO_DIRECTION = Arrays.stream(ALL).collect(Collectors.toMap(direction -> new BlockPos(direction.getVector()).asLong(), direction -> direction, (direction, direction2) -> {
            throw new IllegalArgumentException("Duplicate keys");
        }, Long2ObjectOpenHashMap::new));
    }

    public static enum Type implements Iterable<Direction>,
    Predicate<Direction>
    {
        HORIZONTAL(new Direction[]{NORTH, EAST, SOUTH, WEST}, new Axis[]{Axis.X, Axis.Z}),
        VERTICAL(new Direction[]{UP, DOWN}, new Axis[]{Axis.Y});

        private final Direction[] facingArray;
        private final Axis[] axisArray;

        private Type(Direction[] directions, Axis[] axiss) {
            this.facingArray = directions;
            this.axisArray = axiss;
        }

        public Direction random(Random random) {
            return this.facingArray[random.nextInt(this.facingArray.length)];
        }

        public boolean method_10182(@Nullable Direction direction) {
            return direction != null && direction.getAxis().getType() == this;
        }

        @Override
        public Iterator<Direction> iterator() {
            return Iterators.forArray(this.facingArray);
        }

        @Override
        public /* synthetic */ boolean test(@Nullable Object object) {
            return this.method_10182((Direction)object);
        }
    }

    public static enum AxisDirection {
        POSITIVE(1, "Towards positive"),
        NEGATIVE(-1, "Towards negative");

        private final int offset;
        private final String desc;

        private AxisDirection(int j, String string2) {
            this.offset = j;
            this.desc = string2;
        }

        public int offset() {
            return this.offset;
        }

        public String toString() {
            return this.desc;
        }
    }

    public static enum Axis implements Predicate<Direction>,
    SnakeCaseIdentifiable
    {
        X("x"){

            @Override
            public int choose(int i, int j, int k) {
                return i;
            }

            @Override
            public double choose(double d, double e, double f) {
                return d;
            }

            @Override
            public /* synthetic */ boolean test(@Nullable Object object) {
                return super.method_10176((Direction)object);
            }
        }
        ,
        Y("y"){

            @Override
            public int choose(int i, int j, int k) {
                return j;
            }

            @Override
            public double choose(double d, double e, double f) {
                return e;
            }

            @Override
            public /* synthetic */ boolean test(@Nullable Object object) {
                return super.method_10176((Direction)object);
            }
        }
        ,
        Z("z"){

            @Override
            public int choose(int i, int j, int k) {
                return k;
            }

            @Override
            public double choose(double d, double e, double f) {
                return f;
            }

            @Override
            public /* synthetic */ boolean test(@Nullable Object object) {
                return super.method_10176((Direction)object);
            }
        };

        private static final Map<String, Axis> BY_NAME;
        private final String name;

        private Axis(String string2) {
            this.name = string2;
        }

        @Nullable
        @Environment(value=EnvType.CLIENT)
        public static Axis fromName(String string) {
            return BY_NAME.get(string.toLowerCase(Locale.ROOT));
        }

        public String getName() {
            return this.name;
        }

        public boolean isVertical() {
            return this == Y;
        }

        public boolean isHorizontal() {
            return this == X || this == Z;
        }

        public String toString() {
            return this.name;
        }

        public static Axis method_16699(Random random) {
            return Axis.values()[random.nextInt(Axis.values().length)];
        }

        public boolean method_10176(@Nullable Direction direction) {
            return direction != null && direction.getAxis() == this;
        }

        public Type getType() {
            switch (this) {
                case X: 
                case Z: {
                    return Type.HORIZONTAL;
                }
                case Y: {
                    return Type.VERTICAL;
                }
            }
            throw new Error("Someone's been tampering with the universe!");
        }

        @Override
        public String toSnakeCase() {
            return this.name;
        }

        public abstract int choose(int var1, int var2, int var3);

        public abstract double choose(double var1, double var3, double var5);

        @Override
        public /* synthetic */ boolean test(@Nullable Object object) {
            return this.method_10176((Direction)object);
        }

        static {
            BY_NAME = Arrays.stream(Axis.values()).collect(Collectors.toMap(Axis::getName, axis -> axis));
        }
    }
}

