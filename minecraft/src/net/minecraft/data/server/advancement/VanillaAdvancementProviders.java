package net.minecraft.data.server.advancement;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import net.minecraft.data.DataOutput;
import net.minecraft.util.registry.RegistryWrapper;

public class VanillaAdvancementProviders {
	public static AdvancementProvider createVanillaProvider(DataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookupFuture) {
		return new AdvancementProvider(
			output,
			registryLookupFuture,
			List.of(
				new EndTabAdvancementGenerator(),
				new HusbandryTabAdvancementGenerator(),
				new AdventureTabAdvancementGenerator(),
				new NetherTabAdvancementGenerator(),
				new StoryTabAdvancementGenerator()
			)
		);
	}
}
