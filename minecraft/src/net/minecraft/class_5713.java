package net.minecraft;

import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.event.GameEvent;
import net.minecraft.world.event.listener.GameEventListener;

public interface class_5713 {
	class_5713 field_28181 = new class_5713() {
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
		public void method_32943(GameEvent gameEvent, @Nullable Entity entity, BlockPos blockPos) {
		}
	};

	boolean isEmpty();

	void addListener(GameEventListener listener);

	void removeListener(GameEventListener listener);

	void method_32943(GameEvent gameEvent, @Nullable Entity entity, BlockPos blockPos);
}
