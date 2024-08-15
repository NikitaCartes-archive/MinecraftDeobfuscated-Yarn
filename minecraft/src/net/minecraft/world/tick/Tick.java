package net.minecraft.world.tick;

import it.unimi.dsi.fastutil.Hash.Strategy;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;

public record Tick<T>(T type, BlockPos pos, int delay, TickPriority priority) {
	private static final String TYPE_NBT_KEY = "i";
	private static final String X_NBT_KEY = "x";
	private static final String Y_NBT_KEY = "y";
	private static final String Z_NBT_KEY = "z";
	private static final String DELAY_NBT_KEY = "t";
	private static final String PRIORITY_NBT_KEY = "p";
	public static final Strategy<Tick<?>> HASH_STRATEGY = new Strategy<Tick<?>>() {
		public int hashCode(Tick<?> tick) {
			return 31 * tick.pos().hashCode() + tick.type().hashCode();
		}

		public boolean equals(@Nullable Tick<?> tick, @Nullable Tick<?> tick2) {
			if (tick == tick2) {
				return true;
			} else {
				return tick != null && tick2 != null ? tick.type() == tick2.type() && tick.pos().equals(tick2.pos()) : false;
			}
		}
	};

	public static <T> List<Tick<T>> tick(NbtList tickList, Function<String, Optional<T>> nameToTypeFunction, ChunkPos pos) {
		List<Tick<T>> list = new ArrayList(tickList.size());
		long l = pos.toLong();

		for (int i = 0; i < tickList.size(); i++) {
			NbtCompound nbtCompound = tickList.getCompound(i);
			fromNbt(nbtCompound, nameToTypeFunction).ifPresent(tick -> {
				if (ChunkPos.toLong(tick.pos()) == l) {
					list.add(tick);
				}
			});
		}

		return list;
	}

	public static <T> Optional<Tick<T>> fromNbt(NbtCompound nbt, Function<String, Optional<T>> nameToType) {
		return ((Optional)nameToType.apply(nbt.getString("i"))).map(type -> {
			BlockPos blockPos = new BlockPos(nbt.getInt("x"), nbt.getInt("y"), nbt.getInt("z"));
			return new Tick<>(type, blockPos, nbt.getInt("t"), TickPriority.byIndex(nbt.getInt("p")));
		});
	}

	private static NbtCompound toNbt(String type, BlockPos pos, int delay, TickPriority priority) {
		NbtCompound nbtCompound = new NbtCompound();
		nbtCompound.putString("i", type);
		nbtCompound.putInt("x", pos.getX());
		nbtCompound.putInt("y", pos.getY());
		nbtCompound.putInt("z", pos.getZ());
		nbtCompound.putInt("t", delay);
		nbtCompound.putInt("p", priority.getIndex());
		return nbtCompound;
	}

	public NbtCompound toNbt(Function<T, String> typeToNameFunction) {
		return toNbt((String)typeToNameFunction.apply(this.type), this.pos, this.delay, this.priority);
	}

	public OrderedTick<T> createOrderedTick(long time, long subTickOrder) {
		return new OrderedTick<>(this.type, this.pos, time + (long)this.delay, this.priority, subTickOrder);
	}

	public static <T> Tick<T> create(T type, BlockPos pos) {
		return new Tick<>(type, pos, 0, TickPriority.NORMAL);
	}
}
