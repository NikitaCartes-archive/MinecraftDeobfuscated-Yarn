package net.minecraft;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Identifier;

@FunctionalInterface
public interface class_234<T> {
	void method_974(T object, class_236<T> arg, long l);

	public abstract static class class_235<T, C extends class_234<T>> {
		private final Identifier field_1309;
		private final Class<?> field_1310;

		public class_235(Identifier identifier, Class<?> class_) {
			this.field_1309 = identifier;
			this.field_1310 = class_;
		}

		public Identifier method_977() {
			return this.field_1309;
		}

		public Class<?> method_978() {
			return this.field_1310;
		}

		public abstract void method_975(CompoundTag compoundTag, C arg);

		public abstract C method_976(CompoundTag compoundTag);
	}
}
