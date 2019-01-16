package net.minecraft.state.property;

import java.util.Collection;
import java.util.Optional;

public interface Property<T extends Comparable<T>> {
	String getName();

	Collection<T> getValues();

	Class<T> getValueClass();

	Optional<T> getValue(String string);

	String getValueAsString(T comparable);
}
