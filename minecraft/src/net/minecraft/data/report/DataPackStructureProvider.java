package net.minecraft.data.report;

import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import net.minecraft.data.DataOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.DataWriter;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryLoader;
import net.minecraft.util.Identifier;
import net.minecraft.util.StringIdentifiable;

public class DataPackStructureProvider implements DataProvider {
	private final DataOutput output;
	private static final DataPackStructureProvider.Entry RELOADABLE_REGISTRY = new DataPackStructureProvider.Entry(true, false, true);
	private static final DataPackStructureProvider.Entry RELOADABLE_REGISTRY_WITH_TAGS = new DataPackStructureProvider.Entry(true, true, true);
	private static final DataPackStructureProvider.Entry DYNAMIC_REGISTRY = new DataPackStructureProvider.Entry(true, true, false);
	private static final DataPackStructureProvider.Entry STATIC_REGISTRY = new DataPackStructureProvider.Entry(false, true, true);
	private static final Map<RegistryKey<? extends Registry<?>>, DataPackStructureProvider.Entry> RELOADABLE_REGISTRIES = Map.of(
		RegistryKeys.RECIPE,
		RELOADABLE_REGISTRY,
		RegistryKeys.ADVANCEMENT,
		RELOADABLE_REGISTRY,
		RegistryKeys.LOOT_TABLE,
		RELOADABLE_REGISTRY_WITH_TAGS,
		RegistryKeys.ITEM_MODIFIER,
		RELOADABLE_REGISTRY_WITH_TAGS,
		RegistryKeys.PREDICATE,
		RELOADABLE_REGISTRY_WITH_TAGS
	);
	private static final Map<String, DataPackStructureProvider.FakeRegistry> FAKE_REGISTRIES = Map.of(
		"structure",
		new DataPackStructureProvider.FakeRegistry(DataPackStructureProvider.Format.STRUCTURE, new DataPackStructureProvider.Entry(true, false, true)),
		"function",
		new DataPackStructureProvider.FakeRegistry(DataPackStructureProvider.Format.MCFUNCTION, new DataPackStructureProvider.Entry(true, true, true))
	);
	static final Codec<RegistryKey<? extends Registry<?>>> REGISTRY_KEY_CODEC = Identifier.CODEC.xmap(RegistryKey::ofRegistry, RegistryKey::getValue);

	public DataPackStructureProvider(DataOutput output) {
		this.output = output;
	}

	@Override
	public CompletableFuture<?> run(DataWriter writer) {
		DataPackStructureProvider.Registries registries = new DataPackStructureProvider.Registries(this.buildEntries(), FAKE_REGISTRIES);
		Path path = this.output.resolvePath(DataOutput.OutputType.REPORTS).resolve("datapack.json");
		return DataProvider.writeToPath(writer, DataPackStructureProvider.Registries.CODEC.encodeStart(JsonOps.INSTANCE, registries).getOrThrow(), path);
	}

	@Override
	public String getName() {
		return "Datapack Structure";
	}

	private void addEntry(
		Map<RegistryKey<? extends Registry<?>>, DataPackStructureProvider.Entry> map, RegistryKey<? extends Registry<?>> key, DataPackStructureProvider.Entry entry
	) {
		DataPackStructureProvider.Entry entry2 = (DataPackStructureProvider.Entry)map.putIfAbsent(key, entry);
		if (entry2 != null) {
			throw new IllegalStateException("Duplicate entry for key " + key.getValue());
		}
	}

	private Map<RegistryKey<? extends Registry<?>>, DataPackStructureProvider.Entry> buildEntries() {
		Map<RegistryKey<? extends Registry<?>>, DataPackStructureProvider.Entry> map = new HashMap();
		net.minecraft.registry.Registries.REGISTRIES.forEach(registry -> this.addEntry(map, registry.getKey(), STATIC_REGISTRY));
		RegistryLoader.DYNAMIC_REGISTRIES.forEach(registry -> this.addEntry(map, registry.key(), DYNAMIC_REGISTRY));
		RegistryLoader.DIMENSION_REGISTRIES.forEach(registry -> this.addEntry(map, registry.key(), DYNAMIC_REGISTRY));
		RELOADABLE_REGISTRIES.forEach((key, entry) -> this.addEntry(map, key, entry));
		return map;
	}

	static record Entry(boolean elements, boolean tags, boolean stable) {
		public static final MapCodec<DataPackStructureProvider.Entry> MAP_CODEC = RecordCodecBuilder.mapCodec(
			instance -> instance.group(
						Codec.BOOL.fieldOf("elements").forGetter(DataPackStructureProvider.Entry::elements),
						Codec.BOOL.fieldOf("tags").forGetter(DataPackStructureProvider.Entry::tags),
						Codec.BOOL.fieldOf("stable").forGetter(DataPackStructureProvider.Entry::stable)
					)
					.apply(instance, DataPackStructureProvider.Entry::new)
		);
		public static final Codec<DataPackStructureProvider.Entry> CODEC = MAP_CODEC.codec();
	}

	static record FakeRegistry(DataPackStructureProvider.Format format, DataPackStructureProvider.Entry entry) {
		public static final Codec<DataPackStructureProvider.FakeRegistry> CODEC = RecordCodecBuilder.create(
			instance -> instance.group(
						DataPackStructureProvider.Format.CODEC.fieldOf("format").forGetter(DataPackStructureProvider.FakeRegistry::format),
						DataPackStructureProvider.Entry.MAP_CODEC.forGetter(DataPackStructureProvider.FakeRegistry::entry)
					)
					.apply(instance, DataPackStructureProvider.FakeRegistry::new)
		);
	}

	static enum Format implements StringIdentifiable {
		STRUCTURE("structure"),
		MCFUNCTION("mcfunction");

		public static final Codec<DataPackStructureProvider.Format> CODEC = StringIdentifiable.createCodec(DataPackStructureProvider.Format::values);
		private final String id;

		private Format(final String id) {
			this.id = id;
		}

		@Override
		public String asString() {
			return this.id;
		}
	}

	static record Registries(
		Map<RegistryKey<? extends Registry<?>>, DataPackStructureProvider.Entry> registries, Map<String, DataPackStructureProvider.FakeRegistry> others
	) {
		public static final Codec<DataPackStructureProvider.Registries> CODEC = RecordCodecBuilder.create(
			instance -> instance.group(
						Codec.unboundedMap(DataPackStructureProvider.REGISTRY_KEY_CODEC, DataPackStructureProvider.Entry.CODEC)
							.fieldOf("registries")
							.forGetter(DataPackStructureProvider.Registries::registries),
						Codec.unboundedMap(Codec.STRING, DataPackStructureProvider.FakeRegistry.CODEC).fieldOf("others").forGetter(DataPackStructureProvider.Registries::others)
					)
					.apply(instance, DataPackStructureProvider.Registries::new)
		);
	}
}
