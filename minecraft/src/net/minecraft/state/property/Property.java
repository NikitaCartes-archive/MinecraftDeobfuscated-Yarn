package net.minecraft.state.property;

import java.util.Collection;
import java.util.Optional;

public interface Property<T extends Comparable<T>> {
	String getName();

	Collection<T> getValues();

	Class<T> getValueType();

	Optional<T> getValue(String string);

	String getName(T comparable);
}
