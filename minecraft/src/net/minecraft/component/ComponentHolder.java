package net.minecraft.component;

import javax.annotation.Nullable;

public interface ComponentHolder {
	ComponentMap getComponents();

	@Nullable
	default <T> T get(DataComponentType<? extends T> type) {
		return this.getComponents().get(type);
	}

	default <T> T getOrDefault(DataComponentType<? extends T> type, T fallback) {
		return this.getComponents().getOrDefault(type, fallback);
	}

	default boolean contains(DataComponentType<?> type) {
		return this.getComponents().contains(type);
	}
}
