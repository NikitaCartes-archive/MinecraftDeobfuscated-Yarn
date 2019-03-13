package net.minecraft.world.timer;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Identifier;

@FunctionalInterface
public interface TimerCallback<T> {
	void method_974(T object, Timer<T> timer, long l);

	public abstract static class Serializer<T, C extends TimerCallback<T>> {
		private final Identifier field_1309;
		private final Class<?> callbackClass;

		public Serializer(Identifier identifier, Class<?> class_) {
			this.field_1309 = identifier;
			this.callbackClass = class_;
		}

		public Identifier method_977() {
			return this.field_1309;
		}

		public Class<?> getCallbackClass() {
			return this.callbackClass;
		}

		public abstract void method_975(CompoundTag compoundTag, C timerCallback);

		public abstract C method_976(CompoundTag compoundTag);
	}
}
