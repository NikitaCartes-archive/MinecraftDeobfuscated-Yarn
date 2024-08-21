package net.minecraft.data.server.advancement;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementEntry;
import net.minecraft.data.DataOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.DataWriter;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.Identifier;

public class AdvancementProvider implements DataProvider {
	private final DataOutput.PathResolver pathResolver;
	private final List<AdvancementTabGenerator> tabGenerators;
	private final CompletableFuture<RegistryWrapper.WrapperLookup> registryLookupFuture;

	public AdvancementProvider(
		DataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookupFuture, List<AdvancementTabGenerator> tabGenerators
	) {
		this.pathResolver = output.getResolver(RegistryKeys.ADVANCEMENT);
		this.tabGenerators = tabGenerators;
		this.registryLookupFuture = registryLookupFuture;
	}

	@Override
	public CompletableFuture<?> run(DataWriter writer) {
		return this.registryLookupFuture.thenCompose(registries -> {
			Set<Identifier> set = new HashSet();
			List<CompletableFuture<?>> list = new ArrayList();
			Consumer<AdvancementEntry> consumer = advancement -> {
				if (!set.add(advancement.id())) {
					throw new IllegalStateException("Duplicate advancement " + advancement.id());
				} else {
					Path path = this.pathResolver.resolveJson(advancement.id());
					list.add(DataProvider.writeCodecToPath(writer, registries, Advancement.CODEC, advancement.value(), path));
				}
			};

			for (AdvancementTabGenerator advancementTabGenerator : this.tabGenerators) {
				advancementTabGenerator.accept(registries, consumer);
			}

			return CompletableFuture.allOf((CompletableFuture[])list.toArray(CompletableFuture[]::new));
		});
	}

	@Override
	public final String getName() {
		return "Advancements";
	}
}
