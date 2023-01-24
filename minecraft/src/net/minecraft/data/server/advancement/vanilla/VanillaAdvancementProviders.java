package net.minecraft.data.server.advancement.vanilla;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import net.minecraft.data.DataOutput;
import net.minecraft.data.server.advancement.AdvancementProvider;
import net.minecraft.registry.RegistryWrapper;

public class VanillaAdvancementProviders {
	public static AdvancementProvider createVanillaProvider(DataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookupFuture) {
		return new AdvancementProvider(
			output,
			registryLookupFuture,
			List.of(
				new VanillaEndTabAdvancementGenerator(),
				new VanillaHusbandryTabAdvancementGenerator(),
				new VanillaAdventureTabAdvancementGenerator(),
				new VanillaNetherTabAdvancementGenerator(),
				new VanillaStoryTabAdvancementGenerator()
			)
		);
	}
}
