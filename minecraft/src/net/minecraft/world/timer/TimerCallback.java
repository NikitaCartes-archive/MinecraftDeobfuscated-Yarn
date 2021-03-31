package net.minecraft.world.timer;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;

@FunctionalInterface
public interface TimerCallback<T> {
	void call(T server, Timer<T> events, long time);

	public abstract static class Serializer<T, C extends TimerCallback<T>> {
		private final Identifier id;
		private final Class<?> callbackClass;

		public Serializer(Identifier id, Class<?> callbackClass) {
			this.id = id;
			this.callbackClass = callbackClass;
		}

		public Identifier getId() {
			return this.id;
		}

		public Class<?> getCallbackClass() {
			return this.callbackClass;
		}

		public abstract void serialize(NbtCompound nbt, C callback);

		public abstract C deserialize(NbtCompound nbt);
	}
}
