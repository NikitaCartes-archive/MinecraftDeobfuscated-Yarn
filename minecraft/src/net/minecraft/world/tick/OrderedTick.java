package net.minecraft.world.tick;

import it.unimi.dsi.fastutil.Hash.Strategy;
import java.util.Comparator;
import javax.annotation.Nullable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.TickPriority;

public record OrderedTick() {
	private final T type;
	private final BlockPos pos;
	private final long triggerTick;
	private final TickPriority priority;
	private final long subTickOrder;
	public static final Comparator<OrderedTick<?>> TRIGGER_TICK_COMPARATOR = (first, second) -> {
		int i = Long.compare(first.triggerTick, second.triggerTick);
		if (i != 0) {
			return i;
		} else {
			i = first.priority.compareTo(second.priority);
			return i != 0 ? i : Long.compare(first.subTickOrder, second.subTickOrder);
		}
	};
	public static final Comparator<OrderedTick<?>> BASIC_COMPARATOR = (first, second) -> {
		int i = first.priority.compareTo(second.priority);
		return i != 0 ? i : Long.compare(first.subTickOrder, second.subTickOrder);
	};
	public static final Strategy<OrderedTick<?>> HASH_STRATEGY = new Strategy<OrderedTick<?>>() {
		public int hashCode(OrderedTick<?> orderedTick) {
			return 31 * orderedTick.pos().hashCode() + orderedTick.type().hashCode();
		}

		public boolean equals(@Nullable OrderedTick<?> orderedTick, @Nullable OrderedTick<?> orderedTick2) {
			if (orderedTick == orderedTick2) {
				return true;
			} else {
				return orderedTick != null && orderedTick2 != null ? orderedTick.type() == orderedTick2.type() && orderedTick.pos().equals(orderedTick2.pos()) : false;
			}
		}
	};

	public OrderedTick(T type, BlockPos pos, long triggerTick, long subTickOrder) {
		this(type, pos, triggerTick, TickPriority.NORMAL, subTickOrder);
	}

	public OrderedTick(T object, BlockPos blockPos, long l, TickPriority tickPriority, long m) {
		blockPos = blockPos.toImmutable();
		this.type = object;
		this.pos = blockPos;
		this.triggerTick = l;
		this.priority = tickPriority;
		this.subTickOrder = m;
	}

	public static <T> OrderedTick<T> create(T type, BlockPos pos) {
		return new OrderedTick(type, pos, 0L, TickPriority.NORMAL, 0L);
	}

	public static <T> OrderedTick<T> create(T type, BlockPos pos, long subTickOrder) {
		return new OrderedTick(type, pos, 0L, TickPriority.NORMAL, subTickOrder);
	}
}
