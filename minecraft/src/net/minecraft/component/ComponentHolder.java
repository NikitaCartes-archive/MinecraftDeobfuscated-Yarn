package net.minecraft.component;

import javax.annotation.Nullable;

/**
 * An object that holds components. Note that this interface does not expose
 * methods to modify the held components.
 * 
 * <p>Component holders usually have "base" components and the overrides to the base
 * (usually referred to as "changes"). The overrides may set additional components,
 * modify the values from the base-provided default, or "unset"/remove base values.
 * Methods in this interface expose the final value, after applying the changes.
 * 
 * @see ComponentMap
 * @see ComponentChanges
 */
public interface ComponentHolder {
	ComponentMap getComponents();

	/**
	 * {@return the value for the component {@code type}, or {@code null} if the
	 * component is missing}
	 * 
	 * <p>The returned value should never be mutated.
	 */
	@Nullable
	default <T> T get(ComponentType<? extends T> type) {
		return this.getComponents().get(type);
	}

	/**
	 * {@return the value for the component {@code type}, or {@code fallback} if the
	 * component is missing}
	 * 
	 * <p>This method does not initialize the components with {@code fallback}.
	 * The returned value should never be mutated.
	 */
	default <T> T getOrDefault(ComponentType<? extends T> type, T fallback) {
		return this.getComponents().getOrDefault(type, fallback);
	}

	/**
	 * {@return whether the held components include {@code type}}
	 * 
	 * @implNote This is implemented as {@code get(type) != null}.
	 */
	default boolean contains(ComponentType<?> type) {
		return this.getComponents().contains(type);
	}
}
