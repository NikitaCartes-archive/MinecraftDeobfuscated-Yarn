package net.minecraft.data.server.advancement;

import java.util.function.Consumer;
import net.minecraft.advancement.Advancement;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.Identifier;

public interface AdvancementTabGenerator {
	static Advancement createEmptyAdvancement(String id) {
		return Advancement.Builder.create().build(new Identifier(id));
	}

	void accept(RegistryWrapper.WrapperLookup lookup, Consumer<Advancement> exporter);
}
