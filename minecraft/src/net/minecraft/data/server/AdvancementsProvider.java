package net.minecraft.data.server;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import net.minecraft.advancement.SimpleAdvancement;
import net.minecraft.data.DataCache;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AdvancementsProvider implements DataProvider {
	private static final Logger LOGGER = LogManager.getLogger();
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
	private final DataGenerator root;
	private final List<Consumer<Consumer<SimpleAdvancement>>> tabGenerators = ImmutableList.of(
		new EndTabAdvancementGenerator(),
		new HusbandryTabAdvancementGenerator(),
		new AdventureTabAdvancementGenerator(),
		new NetherTabAdvancementGenerator(),
		new StoryTabAdvancementGenerator()
	);

	public AdvancementsProvider(DataGenerator dataGenerator) {
		this.root = dataGenerator;
	}

	@Override
	public void method_10319(DataCache dataCache) throws IOException {
		Path path = this.root.getOutput();
		Set<Identifier> set = Sets.<Identifier>newHashSet();
		Consumer<SimpleAdvancement> consumer = simpleAdvancement -> {
			if (!set.add(simpleAdvancement.method_688())) {
				throw new IllegalStateException("Duplicate advancement " + simpleAdvancement.method_688());
			} else {
				Path path2 = method_10334(path, simpleAdvancement);

				try {
					DataProvider.method_10320(GSON, dataCache, simpleAdvancement.createTask().toJson(), path2);
				} catch (IOException var6x) {
					LOGGER.error("Couldn't save advancement {}", path2, var6x);
				}
			}
		};

		for (Consumer<Consumer<SimpleAdvancement>> consumer2 : this.tabGenerators) {
			consumer2.accept(consumer);
		}
	}

	private static Path method_10334(Path path, SimpleAdvancement simpleAdvancement) {
		return path.resolve("data/" + simpleAdvancement.method_688().getNamespace() + "/advancements/" + simpleAdvancement.method_688().getPath() + ".json");
	}

	@Override
	public String getName() {
		return "Advancements";
	}
}
