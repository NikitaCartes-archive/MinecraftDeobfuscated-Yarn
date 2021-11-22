package net.minecraft.world.tick;

import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.objects.ObjectOpenCustomHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;

public class SimpleTickScheduler<T> implements SerializableTickScheduler<T>, BasicTickScheduler<T> {
	private final List<Tick<T>> scheduledTicks = Lists.<Tick<T>>newArrayList();
	private final Set<Tick<?>> scheduledTicksSet = new ObjectOpenCustomHashSet<>(Tick.HASH_STRATEGY);

	@Override
	public void scheduleTick(OrderedTick<T> orderedTick) {
		Tick<T> tick = new Tick<>(orderedTick.type(), orderedTick.pos(), 0, orderedTick.priority());
		this.scheduleTick(tick);
	}

	private void scheduleTick(Tick<T> tick) {
		if (this.scheduledTicksSet.add(tick)) {
			this.scheduledTicks.add(tick);
		}
	}

	@Override
	public boolean isQueued(BlockPos pos, T type) {
		return this.scheduledTicksSet.contains(Tick.create(type, pos));
	}

	@Override
	public int getTickCount() {
		return this.scheduledTicks.size();
	}

	@Override
	public NbtElement toNbt(long time, Function<T, String> typeToNameFunction) {
		NbtList nbtList = new NbtList();

		for (Tick<T> tick : this.scheduledTicks) {
			nbtList.add(tick.toNbt(typeToNameFunction));
		}

		return nbtList;
	}

	public List<Tick<T>> getTicks() {
		return List.copyOf(this.scheduledTicks);
	}

	public static <T> SimpleTickScheduler<T> tick(NbtList tickList, Function<String, Optional<T>> typeToNameFunction, ChunkPos pos) {
		SimpleTickScheduler<T> simpleTickScheduler = new SimpleTickScheduler<>();
		Tick.tick(tickList, typeToNameFunction, pos, simpleTickScheduler::scheduleTick);
		return simpleTickScheduler;
	}
}
