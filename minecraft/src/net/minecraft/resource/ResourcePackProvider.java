package net.minecraft.resource;

import java.util.function.Consumer;

/**
 * A resource pack provider provides {@link ResourcePackProfile}s, usually to
 * {@link ResourcePackManager}s.
 */
@FunctionalInterface
public interface ResourcePackProvider {
	/**
	 * Register resource pack profiles created with the {@code factory} to the
	 * {@code profileAdder}.
	 * 
	 * @see ResourcePackProfile
	 * 
	 * @param profileAdder the profile adder that accepts created resource pack profiles
	 */
	void register(Consumer<ResourcePackProfile> profileAdder);
}
