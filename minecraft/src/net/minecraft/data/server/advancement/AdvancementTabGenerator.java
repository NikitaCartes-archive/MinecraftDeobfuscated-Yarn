package net.minecraft.data.server.advancement;

import java.util.function.Consumer;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementEntry;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.Identifier;

public interface AdvancementTabGenerator {
	void accept(RegistryWrapper.WrapperLookup lookup, Consumer<AdvancementEntry> exporter);

	/**
	 * {@return an advancement to use as a reference in {@link
	 * net.minecraft.advancement.Advancement.Builder#parent(net.minecraft.advancement.AdvancementEntry)}}
	 * 
	 * <p>The returned advancement itself should not be exported.
	 */
	static AdvancementEntry reference(String id) {
		return Advancement.Builder.create().build(Identifier.of(id));
	}
}
