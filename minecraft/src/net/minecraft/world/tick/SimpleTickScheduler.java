package net.minecraft.world.tick;

import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.objects.ObjectOpenCustomHashSet;
import java.util.List;
import java.util.Set;
import net.minecraft.util.math.BlockPos;

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
	public List<Tick<T>> collectTicks(long time) {
		return this.scheduledTicks;
	}

	public List<Tick<T>> getTicks() {
		return List.copyOf(this.scheduledTicks);
	}

	public static <T> SimpleTickScheduler<T> tick(List<Tick<T>> ticks) {
		SimpleTickScheduler<T> simpleTickScheduler = new SimpleTickScheduler<>();
		ticks.forEach(simpleTickScheduler::scheduleTick);
		return simpleTickScheduler;
	}
}
