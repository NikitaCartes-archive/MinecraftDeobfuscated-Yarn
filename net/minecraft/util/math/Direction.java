/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.util.math;

import com.google.common.collect.Iterators;
import com.mojang.serialization.Codec;
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
import java.util.stream.Stream;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.client.util.math.Vector4f;
import net.minecraft.entity.Entity;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3i;
import org.jetbrains.annotations.Nullable;

public enum Direction implements StringIdentifiable
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
    private static final Direction[] VALUES;
    private static final Direction[] HORIZONTAL;
    private static final Long2ObjectMap<Direction> VECTOR_TO_DIRECTION;

    private Direction(int id, int idOpposite, int idHorizontal, String name, AxisDirection direction, Axis axis, Vec3i vector) {
        this.id = id;
        this.idHorizontal = idHorizontal;
        this.idOpposite = idOpposite;
        this.name = name;
        this.axis = axis;
        this.direction = direction;
        this.vector = vector;
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
                return Direction.listClosest(direction2, direction, direction3);
            }
            if (p > m) {
                return Direction.listClosest(direction, direction3, direction2);
            }
            return Direction.listClosest(direction, direction2, direction3);
        }
        if (m > p) {
            return Direction.listClosest(direction2, direction3, direction);
        }
        if (o > m) {
            return Direction.listClosest(direction3, direction, direction2);
        }
        return Direction.listClosest(direction3, direction2, direction);
    }

    /**
     * Helper function that returns the 3 directions given, followed by the 3 opposite given in opposite order.
     */
    private static Direction[] listClosest(Direction first, Direction second, Direction third) {
        return new Direction[]{first, second, third, third.getOpposite(), second.getOpposite(), first.getOpposite()};
    }

    @Environment(value=EnvType.CLIENT)
    public static Direction transform(Matrix4f matrix, Direction direction) {
        Vec3i vec3i = direction.getVector();
        Vector4f vector4f = new Vector4f(vec3i.getX(), vec3i.getY(), vec3i.getZ(), 0.0f);
        vector4f.transform(matrix);
        return Direction.getFacing(vector4f.getX(), vector4f.getY(), vector4f.getZ());
    }

    @Environment(value=EnvType.CLIENT)
    public Quaternion getRotationQuaternion() {
        Quaternion quaternion = Vector3f.POSITIVE_X.getDegreesQuaternion(90.0f);
        switch (this) {
            case DOWN: {
                return Vector3f.POSITIVE_X.getDegreesQuaternion(180.0f);
            }
            case UP: {
                return Quaternion.IDENTITY.copy();
            }
            case NORTH: {
                quaternion.hamiltonProduct(Vector3f.POSITIVE_Z.getDegreesQuaternion(180.0f));
                return quaternion;
            }
            case SOUTH: {
                return quaternion;
            }
            case WEST: {
                quaternion.hamiltonProduct(Vector3f.POSITIVE_Z.getDegreesQuaternion(90.0f));
                return quaternion;
            }
        }
        quaternion.hamiltonProduct(Vector3f.POSITIVE_Z.getDegreesQuaternion(-90.0f));
        return quaternion;
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
        return this.vector.getX();
    }

    public int getOffsetY() {
        return this.vector.getY();
    }

    public int getOffsetZ() {
        return this.vector.getZ();
    }

    @Environment(value=EnvType.CLIENT)
    public Vector3f getUnitVector() {
        return new Vector3f(this.getOffsetX(), this.getOffsetY(), this.getOffsetZ());
    }

    public String getName() {
        return this.name;
    }

    public Axis getAxis() {
        return this.axis;
    }

    @Nullable
    @Environment(value=EnvType.CLIENT)
    public static Direction byName(@Nullable String name) {
        if (name == null) {
            return null;
        }
        return NAME_MAP.get(name.toLowerCase(Locale.ROOT));
    }

    public static Direction byId(int id) {
        return VALUES[MathHelper.abs(id % VALUES.length)];
    }

    public static Direction fromHorizontal(int value) {
        return HORIZONTAL[MathHelper.abs(value % HORIZONTAL.length)];
    }

    @Nullable
    public static Direction fromVector(int x, int y, int z) {
        return (Direction)VECTOR_TO_DIRECTION.get(BlockPos.asLong(x, y, z));
    }

    public static Direction fromRotation(double rotation) {
        return Direction.fromHorizontal(MathHelper.floor(rotation / 90.0 + 0.5) & 3);
    }

    public static Direction from(Axis axis, AxisDirection direction) {
        switch (axis) {
            case X: {
                return direction == AxisDirection.POSITIVE ? EAST : WEST;
            }
            case Y: {
                return direction == AxisDirection.POSITIVE ? UP : DOWN;
            }
        }
        return direction == AxisDirection.POSITIVE ? SOUTH : NORTH;
    }

    public float asRotation() {
        return (this.idHorizontal & 3) * 90;
    }

    public static Direction random(Random random) {
        return Util.getRandom(ALL, random);
    }

    public static Direction getFacing(double x, double y, double z) {
        return Direction.getFacing((float)x, (float)y, (float)z);
    }

    public static Direction getFacing(float x, float y, float z) {
        Direction direction = NORTH;
        float f = Float.MIN_VALUE;
        for (Direction direction2 : ALL) {
            float g = x * (float)direction2.vector.getX() + y * (float)direction2.vector.getY() + z * (float)direction2.vector.getZ();
            if (!(g > f)) continue;
            f = g;
            direction = direction2;
        }
        return direction;
    }

    public String toString() {
        return this.name;
    }

    @Override
    public String asString() {
        return this.name;
    }

    public static Direction get(AxisDirection direction, Axis axis) {
        for (Direction direction2 : ALL) {
            if (direction2.getDirection() != direction || direction2.getAxis() != axis) continue;
            return direction2;
        }
        throw new IllegalArgumentException("No such direction: " + (Object)((Object)direction) + " " + axis);
    }

    public Vec3i getVector() {
        return this.vector;
    }

    public boolean method_30928(float f) {
        float g = f * ((float)Math.PI / 180);
        float h = -MathHelper.sin(g);
        float i = MathHelper.cos(g);
        return (float)this.vector.getX() * h + (float)this.vector.getZ() * i > 0.0f;
    }

    static {
        ALL = Direction.values();
        NAME_MAP = Arrays.stream(ALL).collect(Collectors.toMap(Direction::getName, direction -> direction));
        VALUES = (Direction[])Arrays.stream(ALL).sorted(Comparator.comparingInt(direction -> direction.id)).toArray(Direction[]::new);
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

        private Type(Direction[] facingArray, Axis[] axisArray) {
            this.facingArray = facingArray;
            this.axisArray = axisArray;
        }

        public Direction random(Random random) {
            return Util.getRandom(this.facingArray, random);
        }

        public Axis randomAxis(Random random) {
            return Util.getRandom(this.axisArray, random);
        }

        @Override
        public boolean test(@Nullable Direction direction) {
            return direction != null && direction.getAxis().getType() == this;
        }

        @Override
        public Iterator<Direction> iterator() {
            return Iterators.forArray(this.facingArray);
        }

        public Stream<Direction> stream() {
            return Arrays.stream(this.facingArray);
        }

        @Override
        public /* synthetic */ boolean test(@Nullable Object direction) {
            return this.test((Direction)direction);
        }
    }

    public static enum AxisDirection {
        POSITIVE(1, "Towards positive"),
        NEGATIVE(-1, "Towards negative");

        private final int offset;
        private final String description;

        private AxisDirection(int offset, String description) {
            this.offset = offset;
            this.description = description;
        }

        public int offset() {
            return this.offset;
        }

        public String toString() {
            return this.description;
        }

        public AxisDirection getOpposite() {
            return this == POSITIVE ? NEGATIVE : POSITIVE;
        }
    }

    public static enum Axis implements StringIdentifiable,
    Predicate<Direction>
    {
        X("x"){

            @Override
            public int choose(int x, int y, int z) {
                return x;
            }

            @Override
            public double choose(double x, double y, double z) {
                return x;
            }

            @Override
            public /* synthetic */ boolean test(@Nullable Object object) {
                return super.test((Direction)object);
            }
        }
        ,
        Y("y"){

            @Override
            public int choose(int x, int y, int z) {
                return y;
            }

            @Override
            public double choose(double x, double y, double z) {
                return y;
            }

            @Override
            public /* synthetic */ boolean test(@Nullable Object object) {
                return super.test((Direction)object);
            }
        }
        ,
        Z("z"){

            @Override
            public int choose(int x, int y, int z) {
                return z;
            }

            @Override
            public double choose(double x, double y, double z) {
                return z;
            }

            @Override
            public /* synthetic */ boolean test(@Nullable Object object) {
                return super.test((Direction)object);
            }
        };

        private static final Axis[] VALUES;
        public static final Codec<Axis> CODEC;
        private static final Map<String, Axis> BY_NAME;
        private final String name;

        private Axis(String name) {
            this.name = name;
        }

        @Nullable
        public static Axis fromName(String name) {
            return BY_NAME.get(name.toLowerCase(Locale.ROOT));
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

        public static Axis pickRandomAxis(Random random) {
            return Util.getRandom(VALUES, random);
        }

        @Override
        public boolean test(@Nullable Direction direction) {
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
        public String asString() {
            return this.name;
        }

        public abstract int choose(int var1, int var2, int var3);

        public abstract double choose(double var1, double var3, double var5);

        @Override
        public /* synthetic */ boolean test(@Nullable Object object) {
            return this.test((Direction)object);
        }

        static {
            VALUES = Axis.values();
            CODEC = StringIdentifiable.createCodec(Axis::values, Axis::fromName);
            BY_NAME = Arrays.stream(VALUES).collect(Collectors.toMap(Axis::getName, axis -> axis));
        }
    }
}

