package net.minecraft.registry;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.util.Identifier;

public interface DefaultedRegistry<T> extends Registry<T> {
	@Nonnull
	@Override
	Identifier getId(T value);

	@Nonnull
	@Override
	T get(@Nullable Identifier id);

	@Nonnull
	@Override
	T get(int index);

	Identifier getDefaultId();
}
