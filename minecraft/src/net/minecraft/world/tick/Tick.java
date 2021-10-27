package net.minecraft.world.tick;

import it.unimi.dsi.fastutil.Hash.Strategy;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.TickPriority;

record Tick() {
	private final T type;
	private final BlockPos pos;
	private final int delay;
	private final TickPriority priority;
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

	Tick(T object, BlockPos blockPos, int i, TickPriority tickPriority) {
		this.type = object;
		this.pos = blockPos;
		this.delay = i;
		this.priority = tickPriority;
	}

	public static <T> void tick(NbtList tickList, Function<String, Optional<T>> nameToTypeFunction, ChunkPos pos, Consumer<Tick<T>> tickConsumer) {
		long l = pos.toLong();

		for (int i = 0; i < tickList.size(); i++) {
			NbtCompound nbtCompound = tickList.getCompound(i);
			((Optional)nameToTypeFunction.apply(nbtCompound.getString("i"))).ifPresent(type -> {
				BlockPos blockPos = new BlockPos(nbtCompound.getInt("x"), nbtCompound.getInt("y"), nbtCompound.getInt("z"));
				if (ChunkPos.toLong(blockPos) == l) {
					tickConsumer.accept(new Tick((T)type, blockPos, nbtCompound.getInt("t"), TickPriority.byIndex(nbtCompound.getInt("p"))));
				}
			});
		}
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

	public static <T> NbtCompound orderedTickToNbt(OrderedTick<T> orderedTick, Function<T, String> typeToNameFunction, long delay) {
		return toNbt((String)typeToNameFunction.apply(orderedTick.type()), orderedTick.pos(), (int)(orderedTick.triggerTick() - delay), orderedTick.priority());
	}

	public NbtCompound toNbt(Function<T, String> typeToNameFunction) {
		return toNbt((String)typeToNameFunction.apply(this.type), this.pos, this.delay, this.priority);
	}

	public OrderedTick<T> createOrderedTick(long time, long subTickOrder) {
		return new OrderedTick(this.type, this.pos, time + (long)this.delay, this.priority, subTickOrder);
	}

	public static <T> Tick<T> create(T type, BlockPos pos) {
		return new Tick(type, pos, 0, TickPriority.NORMAL);
	}
}
