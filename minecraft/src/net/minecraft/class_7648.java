package net.minecraft;

import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.network.Packet;

public interface class_7648 {
	static class_7648 method_45084(Runnable runnable) {
		return new class_7648() {
			@Override
			public void method_45083() {
				runnable.run();
			}

			@Nullable
			@Override
			public Packet<?> method_45086() {
				runnable.run();
				return null;
			}
		};
	}

	static class_7648 method_45085(Supplier<Packet<?>> supplier) {
		return new class_7648() {
			@Nullable
			@Override
			public Packet<?> method_45086() {
				return (Packet<?>)supplier.get();
			}
		};
	}

	default void method_45083() {
	}

	@Nullable
	default Packet<?> method_45086() {
		return null;
	}
}
