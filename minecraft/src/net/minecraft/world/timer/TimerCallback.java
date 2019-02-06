package net.minecraft.world.timer;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Identifier;

@FunctionalInterface
public interface TimerCallback<T> {
	void call(T object, Timer<T> timer, long l);

	public abstract static class Serializer<T, C extends TimerCallback<T>> {
		private final Identifier id;
		private final Class<?> callbackClass;

		public Serializer(Identifier identifier, Class<?> class_) {
			this.id = identifier;
			this.callbackClass = class_;
		}

		public Identifier getId() {
			return this.id;
		}

		public Class<?> getCallbackClass() {
			return this.callbackClass;
		}

		public abstract void serialize(CompoundTag compoundTag, C timerCallback);

		public abstract C deserialize(CompoundTag compoundTag);
	}
}
