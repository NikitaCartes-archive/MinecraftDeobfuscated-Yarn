package net.minecraft.util.registry;

import java.util.Random;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.util.Identifier;

public class DefaultedRegistry<T> extends SimpleRegistry<T> {
	private final Identifier field_11014;
	private T defaultValue;

	public DefaultedRegistry(String string) {
		this.field_11014 = new Identifier(string);
	}

	@Override
	public <V extends T> V method_10273(int i, Identifier identifier, V object) {
		if (this.field_11014.equals(identifier)) {
			this.defaultValue = (T)object;
		}

		return super.method_10273(i, identifier, object);
	}

	@Override
	public int getRawId(@Nullable T object) {
		int i = super.getRawId(object);
		return i == -1 ? super.getRawId(this.defaultValue) : i;
	}

	@Nonnull
	@Override
	public Identifier method_10221(T object) {
		Identifier identifier = super.method_10221(object);
		return identifier == null ? this.field_11014 : identifier;
	}

	@Nonnull
	@Override
	public T method_10223(@Nullable Identifier identifier) {
		T object = super.method_10223(identifier);
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

	public Identifier method_10137() {
		return this.field_11014;
	}
}
