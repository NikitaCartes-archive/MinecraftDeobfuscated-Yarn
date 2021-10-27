package net.minecraft.world.tick;

import net.minecraft.util.math.BlockPos;

public class EmptyTickSchedulers {
	private static final BasicTickScheduler<Object> EMPTY_BASIC_TICK_SCHEDULER = new BasicTickScheduler<Object>() {
		@Override
		public void scheduleTick(OrderedTick<Object> orderedTick) {
		}

		@Override
		public boolean isQueued(BlockPos pos, Object type) {
			return false;
		}

		@Override
		public int getTickCount() {
			return 0;
		}
	};
	private static final QueryableTickScheduler<Object> EMPTY_QUERYABLE_TICK_SCHEDULER = new QueryableTickScheduler<Object>() {
		@Override
		public void scheduleTick(OrderedTick<Object> orderedTick) {
		}

		@Override
		public boolean isQueued(BlockPos pos, Object type) {
			return false;
		}

		@Override
		public boolean isTicking(BlockPos pos, Object type) {
			return false;
		}

		@Override
		public int getTickCount() {
			return 0;
		}
	};

	public static <T> BasicTickScheduler<T> getReadOnlyTickScheduler() {
		return EMPTY_BASIC_TICK_SCHEDULER;
	}

	public static <T> QueryableTickScheduler<T> getClientTickScheduler() {
		return EMPTY_QUERYABLE_TICK_SCHEDULER;
	}
}
