package net.minecraft.data.server.advancement.winterdrop;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import net.minecraft.data.DataOutput;
import net.minecraft.data.server.advancement.AdvancementProvider;
import net.minecraft.registry.RegistryWrapper;

public class WinterDropAdvancementProviders {
	public static AdvancementProvider createWinterDropProvider(DataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
		return new AdvancementProvider(output, registriesFuture, List.of(new WinterDropAdventureAdvancementTabGenerator()));
	}
}
