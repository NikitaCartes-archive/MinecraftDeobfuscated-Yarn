/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.tick;

import it.unimi.dsi.fastutil.Hash;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.tick.OrderedTick;
import net.minecraft.world.tick.TickPriority;
import org.jetbrains.annotations.Nullable;

public record Tick<T>(T type, BlockPos pos, int delay, TickPriority priority) {
    private static final String TYPE_NBT_KEY = "i";
    private static final String X_NBT_KEY = "x";
    private static final String Y_NBT_KEY = "y";
    private static final String Z_NBT_KEY = "z";
    private static final String DELAY_NBT_KEY = "t";
    private static final String PRIORITY_NBT_KEY = "p";
    public static final Hash.Strategy<Tick<?>> HASH_STRATEGY = new Hash.Strategy<Tick<?>>(){

        @Override
        public int hashCode(Tick<?> tick) {
            return 31 * tick.pos().hashCode() + tick.type().hashCode();
        }

        @Override
        public boolean equals(@Nullable Tick<?> tick, @Nullable Tick<?> tick2) {
            if (tick == tick2) {
                return true;
            }
            if (tick == null || tick2 == null) {
                return false;
            }
            return tick.type() == tick2.type() && tick.pos().equals(tick2.pos());
        }

        @Override
        public /* synthetic */ boolean equals(@Nullable Object first, @Nullable Object second) {
            return this.equals((Tick)first, (Tick)second);
        }

        @Override
        public /* synthetic */ int hashCode(Object tick) {
            return this.hashCode((Tick)tick);
        }
    };

    public static <T> void tick(NbtList tickList, Function<String, Optional<T>> nameToTypeFunction, ChunkPos pos, Consumer<Tick<T>> tickConsumer) {
        long l = pos.toLong();
        for (int i = 0; i < tickList.size(); ++i) {
            NbtCompound nbtCompound = tickList.getCompound(i);
            Tick.fromNbt(nbtCompound, nameToTypeFunction).ifPresent(tick -> {
                if (ChunkPos.toLong(tick.pos()) == l) {
                    tickConsumer.accept((Tick)tick);
                }
            });
        }
    }

    public static <T> Optional<Tick<T>> fromNbt(NbtCompound nbt, Function<String, Optional<T>> nameToType) {
        return nameToType.apply(nbt.getString(TYPE_NBT_KEY)).map(type -> {
            BlockPos blockPos = new BlockPos(nbt.getInt(X_NBT_KEY), nbt.getInt(Y_NBT_KEY), nbt.getInt(Z_NBT_KEY));
            return new Tick<Object>(type, blockPos, nbt.getInt(DELAY_NBT_KEY), TickPriority.byIndex(nbt.getInt(PRIORITY_NBT_KEY)));
        });
    }

    private static NbtCompound toNbt(String type, BlockPos pos, int delay, TickPriority priority) {
        NbtCompound nbtCompound = new NbtCompound();
        nbtCompound.putString(TYPE_NBT_KEY, type);
        nbtCompound.putInt(X_NBT_KEY, pos.getX());
        nbtCompound.putInt(Y_NBT_KEY, pos.getY());
        nbtCompound.putInt(Z_NBT_KEY, pos.getZ());
        nbtCompound.putInt(DELAY_NBT_KEY, delay);
        nbtCompound.putInt(PRIORITY_NBT_KEY, priority.getIndex());
        return nbtCompound;
    }

    public static <T> NbtCompound orderedTickToNbt(OrderedTick<T> orderedTick, Function<T, String> typeToNameFunction, long delay) {
        return Tick.toNbt(typeToNameFunction.apply(orderedTick.type()), orderedTick.pos(), (int)(orderedTick.triggerTick() - delay), orderedTick.priority());
    }

    public NbtCompound toNbt(Function<T, String> typeToNameFunction) {
        return Tick.toNbt(typeToNameFunction.apply(this.type), this.pos, this.delay, this.priority);
    }

    public OrderedTick<T> createOrderedTick(long time, long subTickOrder) {
        return new OrderedTick<T>(this.type, this.pos, time + (long)this.delay, this.priority, subTickOrder);
    }

    public static <T> Tick<T> create(T type, BlockPos pos) {
        return new Tick<T>(type, pos, 0, TickPriority.NORMAL);
    }
}

