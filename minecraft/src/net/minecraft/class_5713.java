package net.minecraft;

import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.event.GameEvent;
import net.minecraft.world.event.listener.GameEventListener;

public interface class_5713 {
	class_5713 EMPTY = new class_5713() {
		@Override
		public boolean isEmpty() {
			return true;
		}

		@Override
		public void addListener(GameEventListener listener) {
		}

		@Override
		public void removeListener(GameEventListener listener) {
		}

		@Override
		public void listen(GameEvent event, @Nullable Entity entity, BlockPos pos) {
		}
	};

	boolean isEmpty();

	void addListener(GameEventListener listener);

	void removeListener(GameEventListener listener);

	void listen(GameEvent event, @Nullable Entity entity, BlockPos pos);
}
