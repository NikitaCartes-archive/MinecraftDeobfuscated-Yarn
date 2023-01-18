/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.util.math;

import com.google.common.collect.Iterators;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.entity.Entity;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.Util;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.math.random.Random;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.joml.Vector4f;

/**
 * An enum representing 6 cardinal directions in Minecraft.
 * 
 * <p>In Minecraft, the X axis determines the east-west direction, the Y axis determines
 * the up-down direction, and the Z axis determines the south-north direction (note
 * that positive-Z direction is south, not north).
 */
public enum Direction implements StringIdentifiable
{
    DOWN(0, 1, -1, "down", AxisDirection.NEGATIVE, Axis.Y, new Vec3i(0, -1, 0)),
    UP(1, 0, -1, "up", AxisDirection.POSITIVE, Axis.Y, new Vec3i(0, 1, 0)),
    NORTH(2, 3, 2, "north", AxisDirection.NEGATIVE, Axis.Z, new Vec3i(0, 0, -1)),
    SOUTH(3, 2, 0, "south", AxisDirection.POSITIVE, Axis.Z, new Vec3i(0, 0, 1)),
    WEST(4, 5, 1, "west", AxisDirection.NEGATIVE, Axis.X, new Vec3i(-1, 0, 0)),
    EAST(5, 4, 3, "east", AxisDirection.POSITIVE, Axis.X, new Vec3i(1, 0, 0));

    public static final StringIdentifiable.Codec<Direction> CODEC;
    public static final Codec<Direction> VERTICAL_CODEC;
    private final int id;
    private final int idOpposite;
    private final int idHorizontal;
    private final String name;
    private final Axis axis;
    private final AxisDirection direction;
    private final Vec3i vector;
    private static final Direction[] ALL;
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

    public static Direction transform(Matrix4f matrix, Direction direction) {
        Vec3i vec3i = direction.getVector();
        Vector4f vector4f = matrix.transform(new Vector4f(vec3i.getX(), vec3i.getY(), vec3i.getZ(), 0.0f));
        return Direction.getFacing(vector4f.x(), vector4f.y(), vector4f.z());
    }

    /**
     * {@return a shuffled collection of all directions}
     */
    public static Collection<Direction> shuffle(Random random) {
        return Util.copyShuffled(Direction.values(), random);
    }

    public static Stream<Direction> stream() {
        return Stream.of(ALL);
    }

    public Quaternionf getRotationQuaternion() {
        return switch (this) {
            default -> throw new IncompatibleClassChangeError();
            case DOWN -> new Quaternionf().rotationX((float)Math.PI);
            case UP -> new Quaternionf();
            case NORTH -> new Quaternionf().rotationXYZ(1.5707964f, 0.0f, (float)Math.PI);
            case SOUTH -> new Quaternionf().rotationX(1.5707964f);
            case WEST -> new Quaternionf().rotationXYZ(1.5707964f, 0.0f, 1.5707964f);
            case EAST -> new Quaternionf().rotationXYZ(1.5707964f, 0.0f, -1.5707964f);
        };
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

    public static Direction getLookDirectionForAxis(Entity entity, Axis axis) {
        return switch (axis) {
            default -> throw new IncompatibleClassChangeError();
            case Axis.X -> {
                if (EAST.pointsTo(entity.getYaw(1.0f))) {
                    yield EAST;
                }
                yield WEST;
            }
            case Axis.Z -> {
                if (SOUTH.pointsTo(entity.getYaw(1.0f))) {
                    yield SOUTH;
                }
                yield NORTH;
            }
            case Axis.Y -> entity.getPitch(1.0f) < 0.0f ? UP : DOWN;
        };
    }

    public Direction getOpposite() {
        return Direction.byId(this.idOpposite);
    }

    public Direction rotateClockwise(Axis axis) {
        return switch (axis) {
            default -> throw new IncompatibleClassChangeError();
            case Axis.X -> {
                if (this == WEST || this == EAST) {
                    yield this;
                }
                yield this.rotateXClockwise();
            }
            case Axis.Y -> {
                if (this == UP || this == DOWN) {
                    yield this;
                }
                yield this.rotateYClockwise();
            }
            case Axis.Z -> this == NORTH || this == SOUTH ? this : this.rotateZClockwise();
        };
    }

    public Direction rotateCounterclockwise(Axis axis) {
        return switch (axis) {
            default -> throw new IncompatibleClassChangeError();
            case Axis.X -> {
                if (this == WEST || this == EAST) {
                    yield this;
                }
                yield this.rotateXCounterclockwise();
            }
            case Axis.Y -> {
                if (this == UP || this == DOWN) {
                    yield this;
                }
                yield this.rotateYCounterclockwise();
            }
            case Axis.Z -> this == NORTH || this == SOUTH ? this : this.rotateZCounterclockwise();
        };
    }

    public Direction rotateYClockwise() {
        return switch (this) {
            case NORTH -> EAST;
            case EAST -> SOUTH;
            case SOUTH -> WEST;
            case WEST -> NORTH;
            default -> throw new IllegalStateException("Unable to get Y-rotated facing of " + this);
        };
    }

    private Direction rotateXClockwise() {
        return switch (this) {
            case UP -> NORTH;
            case NORTH -> DOWN;
            case DOWN -> SOUTH;
            case SOUTH -> UP;
            default -> throw new IllegalStateException("Unable to get X-rotated facing of " + this);
        };
    }

    private Direction rotateXCounterclockwise() {
        return switch (this) {
            case UP -> SOUTH;
            case SOUTH -> DOWN;
            case DOWN -> NORTH;
            case NORTH -> UP;
            default -> throw new IllegalStateException("Unable to get X-rotated facing of " + this);
        };
    }

    private Direction rotateZClockwise() {
        return switch (this) {
            case UP -> EAST;
            case EAST -> DOWN;
            case DOWN -> WEST;
            case WEST -> UP;
            default -> throw new IllegalStateException("Unable to get Z-rotated facing of " + this);
        };
    }

    private Direction rotateZCounterclockwise() {
        return switch (this) {
            case UP -> WEST;
            case WEST -> DOWN;
            case DOWN -> EAST;
            case EAST -> UP;
            default -> throw new IllegalStateException("Unable to get Z-rotated facing of " + this);
        };
    }

    public Direction rotateYCounterclockwise() {
        return switch (this) {
            case NORTH -> WEST;
            case EAST -> NORTH;
            case SOUTH -> EAST;
            case WEST -> SOUTH;
            default -> throw new IllegalStateException("Unable to get CCW facing of " + this);
        };
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

    public Vector3f getUnitVector() {
        return new Vector3f(this.getOffsetX(), this.getOffsetY(), this.getOffsetZ());
    }

    public String getName() {
        return this.name;
    }

    public Axis getAxis() {
        return this.axis;
    }

    /**
     * {@return a direction with the given {@code name}, or {@code null} if there is
     * no such direction}
     */
    @Nullable
    public static Direction byName(@Nullable String name) {
        return CODEC.byId(name);
    }

    public static Direction byId(int id) {
        return VALUES[MathHelper.abs(id % VALUES.length)];
    }

    public static Direction fromHorizontal(int value) {
        return HORIZONTAL[MathHelper.abs(value % HORIZONTAL.length)];
    }

    @Nullable
    public static Direction fromVector(BlockPos pos) {
        return (Direction)VECTOR_TO_DIRECTION.get(pos.asLong());
    }

    @Nullable
    public static Direction fromVector(int x, int y, int z) {
        return (Direction)VECTOR_TO_DIRECTION.get(BlockPos.asLong(x, y, z));
    }

    public static Direction fromRotation(double rotation) {
        return Direction.fromHorizontal(MathHelper.floor(rotation / 90.0 + 0.5) & 3);
    }

    public static Direction from(Axis axis, AxisDirection direction) {
        return switch (axis) {
            default -> throw new IncompatibleClassChangeError();
            case Axis.X -> {
                if (direction == AxisDirection.POSITIVE) {
                    yield EAST;
                }
                yield WEST;
            }
            case Axis.Y -> {
                if (direction == AxisDirection.POSITIVE) {
                    yield UP;
                }
                yield DOWN;
            }
            case Axis.Z -> direction == AxisDirection.POSITIVE ? SOUTH : NORTH;
        };
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

    private static DataResult<Direction> validateVertical(Direction direction) {
        return direction.getAxis().isVertical() ? DataResult.success(direction) : DataResult.error("Expected a vertical direction");
    }

    public static Direction get(AxisDirection direction, Axis axis) {
        for (Direction direction2 : ALL) {
            if (direction2.getDirection() != direction || direction2.getAxis() != axis) continue;
            return direction2;
        }
        throw new IllegalArgumentException("No such direction: " + direction + " " + axis);
    }

    public Vec3i getVector() {
        return this.vector;
    }

    /**
     * {@return whether the given yaw points to the direction}
     * 
     * @implNote This returns whether the yaw can make an acute angle with the direction.
     * 
     * <p>This always returns {@code false} for vertical directions.
     */
    public boolean pointsTo(float yaw) {
        float f = yaw * ((float)Math.PI / 180);
        float g = -MathHelper.sin(f);
        float h = MathHelper.cos(f);
        return (float)this.vector.getX() * g + (float)this.vector.getZ() * h > 0.0f;
    }

    static {
        CODEC = StringIdentifiable.createCodec(Direction::values);
        VERTICAL_CODEC = Codecs.validate(CODEC, Direction::validateVertical);
        ALL = Direction.values();
        VALUES = (Direction[])Arrays.stream(ALL).sorted(Comparator.comparingInt(direction -> direction.id)).toArray(Direction[]::new);
        HORIZONTAL = (Direction[])Arrays.stream(ALL).filter(direction -> direction.getAxis().isHorizontal()).sorted(Comparator.comparingInt(direction -> direction.idHorizontal)).toArray(Direction[]::new);
        VECTOR_TO_DIRECTION = Arrays.stream(ALL).collect(Collectors.toMap(direction -> new BlockPos(direction.getVector()).asLong(), direction -> direction, (direction1, direction2) -> {
            throw new IllegalArgumentException("Duplicate keys");
        }, Long2ObjectOpenHashMap::new));
    }

    /*
     * Uses 'sealed' constructs - enablewith --sealed true
     */
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

        public static final Axis[] VALUES;
        public static final StringIdentifiable.Codec<Axis> CODEC;
        private final String name;

        Axis(String name) {
            this.name = name;
        }

        @Nullable
        public static Axis fromName(String name) {
            return CODEC.byId(name);
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
            return switch (this) {
                default -> throw new IncompatibleClassChangeError();
                case X, Z -> Type.HORIZONTAL;
                case Y -> Type.VERTICAL;
            };
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
            CODEC = StringIdentifiable.createCodec(Axis::values);
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

        public String getDescription() {
            return this.description;
        }

        public String toString() {
            return this.description;
        }

        public AxisDirection getOpposite() {
            return this == POSITIVE ? NEGATIVE : POSITIVE;
        }
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

        public List<Direction> getShuffled(Random random) {
            return Util.copyShuffled(this.facingArray, random);
        }

        @Override
        public /* synthetic */ boolean test(@Nullable Object direction) {
            return this.test((Direction)direction);
        }
    }
}

