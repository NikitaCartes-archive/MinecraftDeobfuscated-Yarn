package net.minecraft.data.server.advancement;

import java.util.function.Consumer;
import net.minecraft.advancement.Advancement;
import net.minecraft.util.registry.RegistryWrapper;

public interface AdvancementTabGenerator {
	void accept(RegistryWrapper.WrapperLookup lookup, Consumer<Advancement> exporter);
}
