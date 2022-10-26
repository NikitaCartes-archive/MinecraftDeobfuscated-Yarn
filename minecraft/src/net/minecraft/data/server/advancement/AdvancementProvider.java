package net.minecraft.data.server.advancement;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import net.minecraft.advancement.Advancement;
import net.minecraft.data.DataOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.DataWriter;
import net.minecraft.util.Identifier;

public class AdvancementProvider implements DataProvider {
	private final DataOutput.PathResolver pathResolver;
	private final List<AdvancementTabGenerator> tabGenerators;

	public AdvancementProvider(DataOutput output, List<AdvancementTabGenerator> tabGenerators) {
		this.pathResolver = output.getResolver(DataOutput.OutputType.DATA_PACK, "advancements");
		this.tabGenerators = tabGenerators;
	}

	@Override
	public CompletableFuture<?> run(DataWriter writer) {
		Set<Identifier> set = new HashSet();
		List<CompletableFuture<?>> list = new ArrayList();
		Consumer<Advancement> consumer = advancement -> {
			if (!set.add(advancement.getId())) {
				throw new IllegalStateException("Duplicate advancement " + advancement.getId());
			} else {
				Path path = this.pathResolver.resolveJson(advancement.getId());
				list.add(DataProvider.writeToPath(writer, advancement.createTask().toJson(), path));
			}
		};

		for (AdvancementTabGenerator advancementTabGenerator : this.tabGenerators) {
			advancementTabGenerator.accept(consumer);
		}

		return CompletableFuture.allOf((CompletableFuture[])list.toArray(CompletableFuture[]::new));
	}

	@Override
	public final String getName() {
		return "Advancements";
	}
}
