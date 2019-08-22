package net.minecraft.util.registry;

import java.util.Random;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.util.Identifier;

public class DefaultedRegistry<T> extends SimpleRegistry<T> {
	private final Identifier defaultId;
	private T defaultValue;

	public DefaultedRegistry(String string) {
		this.defaultId = new Identifier(string);
	}

	@Override
	public <V extends T> V set(int i, Identifier identifier, V object) {
		if (this.defaultId.equals(identifier)) {
			this.defaultValue = (T)object;
		}

		return super.set(i, identifier, object);
	}

	@Override
	public int getRawId(@Nullable T object) {
		int i = super.getRawId(object);
		return i == -1 ? super.getRawId(this.defaultValue) : i;
	}

	@Nonnull
	@Override
	public Identifier getId(T object) {
		Identifier identifier = super.getId(object);
		return identifier == null ? this.defaultId : identifier;
	}

	@Nonnull
	@Override
	public T get(@Nullable Identifier identifier) {
		T object = super.get(identifier);
		return object == null ? this.defaultValue : object;
	}

	@Nonnull
	@Override
	public T get(int i) {
		T object = super.get(i);
		return object == null ? this.defaultValue : object;
	}

	@Nonnull
	@Override
	public T getRandom(Random random) {
		T object = super.getRandom(random);
		return object == null ? this.defaultValue : object;
	}

	public Identifier getDefaultId() {
		return this.defaultId;
	}
}
