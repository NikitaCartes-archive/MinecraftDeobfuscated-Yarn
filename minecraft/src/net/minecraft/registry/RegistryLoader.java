package net.minecraft.registry;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Decoder;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.Lifecycle;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringWriter;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import net.minecraft.block.entity.BannerPattern;
import net.minecraft.block.jukebox.JukeboxSong;
import net.minecraft.block.spawner.TrialSpawnerConfig;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.provider.EnchantmentProvider;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.entity.decoration.painting.PaintingVariant;
import net.minecraft.entity.passive.WolfVariant;
import net.minecraft.item.Instrument;
import net.minecraft.item.equipment.trim.ArmorTrimMaterial;
import net.minecraft.item.equipment.trim.ArmorTrimPattern;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.message.MessageType;
import net.minecraft.registry.entry.RegistryEntryInfo;
import net.minecraft.registry.tag.TagGroupLoader;
import net.minecraft.registry.tag.TagPacketSerializer;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceFactory;
import net.minecraft.resource.ResourceFinder;
import net.minecraft.resource.ResourceManager;
import net.minecraft.structure.StructureSet;
import net.minecraft.structure.pool.StructurePool;
import net.minecraft.structure.processor.StructureProcessorType;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.crash.CrashCallable;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.math.noise.DoublePerlinNoiseSampler;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.MultiNoiseBiomeSourceParameterList;
import net.minecraft.world.dimension.DimensionOptions;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.FlatLevelGeneratorPreset;
import net.minecraft.world.gen.WorldPreset;
import net.minecraft.world.gen.carver.ConfiguredCarver;
import net.minecraft.world.gen.chunk.ChunkGeneratorSettings;
import net.minecraft.world.gen.densityfunction.DensityFunction;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.PlacedFeature;
import net.minecraft.world.gen.structure.Structure;
import org.slf4j.Logger;

public class RegistryLoader {
	private static final Logger LOGGER = LogUtils.getLogger();
	private static final Comparator<RegistryKey<?>> KEY_COMPARATOR = Comparator.comparing(RegistryKey::getRegistry).thenComparing(RegistryKey::getValue);
	private static final RegistryEntryInfo EXPERIMENTAL_ENTRY_INFO = new RegistryEntryInfo(Optional.empty(), Lifecycle.experimental());
	private static final Function<Optional<VersionedIdentifier>, RegistryEntryInfo> RESOURCE_ENTRY_INFO_GETTER = Util.memoize(
		(Function<Optional<VersionedIdentifier>, RegistryEntryInfo>)(knownPacks -> {
			Lifecycle lifecycle = (Lifecycle)knownPacks.map(VersionedIdentifier::isVanilla).map(vanilla -> Lifecycle.stable()).orElse(Lifecycle.experimental());
			return new RegistryEntryInfo(knownPacks, lifecycle);
		})
	);
	public static final List<RegistryLoader.Entry<?>> DYNAMIC_REGISTRIES = List.of(
		new RegistryLoader.Entry<>(RegistryKeys.DIMENSION_TYPE, DimensionType.CODEC),
		new RegistryLoader.Entry<>(RegistryKeys.BIOME, Biome.CODEC),
		new RegistryLoader.Entry<>(RegistryKeys.MESSAGE_TYPE, MessageType.CODEC),
		new RegistryLoader.Entry<>(RegistryKeys.CONFIGURED_CARVER, ConfiguredCarver.CODEC),
		new RegistryLoader.Entry<>(RegistryKeys.CONFIGURED_FEATURE, ConfiguredFeature.CODEC),
		new RegistryLoader.Entry<>(RegistryKeys.PLACED_FEATURE, PlacedFeature.CODEC),
		new RegistryLoader.Entry<>(RegistryKeys.STRUCTURE, Structure.STRUCTURE_CODEC),
		new RegistryLoader.Entry<>(RegistryKeys.STRUCTURE_SET, StructureSet.CODEC),
		new RegistryLoader.Entry<>(RegistryKeys.PROCESSOR_LIST, StructureProcessorType.PROCESSORS_CODEC),
		new RegistryLoader.Entry<>(RegistryKeys.TEMPLATE_POOL, StructurePool.CODEC),
		new RegistryLoader.Entry<>(RegistryKeys.CHUNK_GENERATOR_SETTINGS, ChunkGeneratorSettings.CODEC),
		new RegistryLoader.Entry<>(RegistryKeys.NOISE_PARAMETERS, DoublePerlinNoiseSampler.NoiseParameters.CODEC),
		new RegistryLoader.Entry<>(RegistryKeys.DENSITY_FUNCTION, DensityFunction.CODEC),
		new RegistryLoader.Entry<>(RegistryKeys.WORLD_PRESET, WorldPreset.CODEC),
		new RegistryLoader.Entry<>(RegistryKeys.FLAT_LEVEL_GENERATOR_PRESET, FlatLevelGeneratorPreset.CODEC),
		new RegistryLoader.Entry<>(RegistryKeys.TRIM_PATTERN, ArmorTrimPattern.CODEC),
		new RegistryLoader.Entry<>(RegistryKeys.TRIM_MATERIAL, ArmorTrimMaterial.CODEC),
		new RegistryLoader.Entry<>(RegistryKeys.TRIAL_SPAWNER, TrialSpawnerConfig.CODEC),
		new RegistryLoader.Entry<>(RegistryKeys.WOLF_VARIANT, WolfVariant.CODEC, true),
		new RegistryLoader.Entry<>(RegistryKeys.PAINTING_VARIANT, PaintingVariant.CODEC, true),
		new RegistryLoader.Entry<>(RegistryKeys.DAMAGE_TYPE, DamageType.CODEC),
		new RegistryLoader.Entry<>(RegistryKeys.MULTI_NOISE_BIOME_SOURCE_PARAMETER_LIST, MultiNoiseBiomeSourceParameterList.CODEC),
		new RegistryLoader.Entry<>(RegistryKeys.BANNER_PATTERN, BannerPattern.CODEC),
		new RegistryLoader.Entry<>(RegistryKeys.ENCHANTMENT, Enchantment.CODEC),
		new RegistryLoader.Entry<>(RegistryKeys.ENCHANTMENT_PROVIDER, EnchantmentProvider.CODEC),
		new RegistryLoader.Entry<>(RegistryKeys.JUKEBOX_SONG, JukeboxSong.CODEC),
		new RegistryLoader.Entry<>(RegistryKeys.INSTRUMENT, Instrument.CODEC)
	);
	public static final List<RegistryLoader.Entry<?>> DIMENSION_REGISTRIES = List.of(new RegistryLoader.Entry<>(RegistryKeys.DIMENSION, DimensionOptions.CODEC));
	public static final List<RegistryLoader.Entry<?>> SYNCED_REGISTRIES = List.of(
		new RegistryLoader.Entry<>(RegistryKeys.BIOME, Biome.NETWORK_CODEC),
		new RegistryLoader.Entry<>(RegistryKeys.MESSAGE_TYPE, MessageType.CODEC),
		new RegistryLoader.Entry<>(RegistryKeys.TRIM_PATTERN, ArmorTrimPattern.CODEC),
		new RegistryLoader.Entry<>(RegistryKeys.TRIM_MATERIAL, ArmorTrimMaterial.CODEC),
		new RegistryLoader.Entry<>(RegistryKeys.WOLF_VARIANT, WolfVariant.CODEC, true),
		new RegistryLoader.Entry<>(RegistryKeys.PAINTING_VARIANT, PaintingVariant.CODEC, true),
		new RegistryLoader.Entry<>(RegistryKeys.DIMENSION_TYPE, DimensionType.CODEC),
		new RegistryLoader.Entry<>(RegistryKeys.DAMAGE_TYPE, DamageType.CODEC),
		new RegistryLoader.Entry<>(RegistryKeys.BANNER_PATTERN, BannerPattern.CODEC),
		new RegistryLoader.Entry<>(RegistryKeys.ENCHANTMENT, Enchantment.CODEC),
		new RegistryLoader.Entry<>(RegistryKeys.JUKEBOX_SONG, JukeboxSong.CODEC),
		new RegistryLoader.Entry<>(RegistryKeys.INSTRUMENT, Instrument.CODEC)
	);

	public static DynamicRegistryManager.Immutable loadFromResource(
		ResourceManager resourceManager, List<RegistryWrapper.Impl<?>> registries, List<RegistryLoader.Entry<?>> entries
	) {
		return load((loader, infoGetter) -> loader.loadFromResource(resourceManager, infoGetter), registries, entries);
	}

	public static DynamicRegistryManager.Immutable loadFromNetwork(
		Map<RegistryKey<? extends Registry<?>>, RegistryLoader.ElementsAndTags> data,
		ResourceFactory factory,
		List<RegistryWrapper.Impl<?>> registries,
		List<RegistryLoader.Entry<?>> entries
	) {
		return load((loader, infoGetter) -> loader.loadFromNetwork(data, factory, infoGetter), registries, entries);
	}

	private static DynamicRegistryManager.Immutable load(
		RegistryLoader.RegistryLoadable loadable, List<RegistryWrapper.Impl<?>> registries, List<RegistryLoader.Entry<?>> entries
	) {
		Map<RegistryKey<?>, Exception> map = new HashMap();
		List<RegistryLoader.Loader<?>> list = (List<RegistryLoader.Loader<?>>)entries.stream()
			.map(entry -> entry.getLoader(Lifecycle.stable(), map))
			.collect(Collectors.toUnmodifiableList());
		RegistryOps.RegistryInfoGetter registryInfoGetter = createInfoGetter(registries, list);
		list.forEach(loader -> loadable.apply(loader, registryInfoGetter));
		list.forEach(loader -> {
			Registry<?> registry = loader.registry();

			try {
				registry.freeze();
			} catch (Exception var4x) {
				map.put(registry.getKey(), var4x);
			}

			if (loader.data.requiredNonEmpty && registry.size() == 0) {
				map.put(registry.getKey(), new IllegalStateException("Registry must be non-empty"));
			}
		});
		if (!map.isEmpty()) {
			throw writeAndCreateLoadingException(map);
		} else {
			return new DynamicRegistryManager.ImmutableImpl(list.stream().map(RegistryLoader.Loader::registry).toList()).toImmutable();
		}
	}

	private static RegistryOps.RegistryInfoGetter createInfoGetter(List<RegistryWrapper.Impl<?>> registries, List<RegistryLoader.Loader<?>> additionalRegistries) {
		final Map<RegistryKey<? extends Registry<?>>, RegistryOps.RegistryInfo<?>> map = new HashMap();
		registries.forEach(registry -> map.put(registry.getKey(), createInfo(registry)));
		additionalRegistries.forEach(loader -> map.put(loader.registry.getKey(), createInfo(loader.registry)));
		return new RegistryOps.RegistryInfoGetter() {
			@Override
			public <T> Optional<RegistryOps.RegistryInfo<T>> getRegistryInfo(RegistryKey<? extends Registry<? extends T>> registryRef) {
				return Optional.ofNullable((RegistryOps.RegistryInfo)map.get(registryRef));
			}
		};
	}

	private static <T> RegistryOps.RegistryInfo<T> createInfo(MutableRegistry<T> registry) {
		return new RegistryOps.RegistryInfo<>(registry, registry.createMutableRegistryLookup(), registry.getLifecycle());
	}

	private static <T> RegistryOps.RegistryInfo<T> createInfo(RegistryWrapper.Impl<T> registry) {
		return new RegistryOps.RegistryInfo<>(registry, registry, registry.getLifecycle());
	}

	private static CrashException writeAndCreateLoadingException(Map<RegistryKey<?>, Exception> exceptions) {
		writeLoadingError(exceptions);
		return createLoadingException(exceptions);
	}

	private static void writeLoadingError(Map<RegistryKey<?>, Exception> exceptions) {
		StringWriter stringWriter = new StringWriter();
		PrintWriter printWriter = new PrintWriter(stringWriter);
		Map<Identifier, Map<Identifier, Exception>> map = (Map<Identifier, Map<Identifier, Exception>>)exceptions.entrySet()
			.stream()
			.collect(
				Collectors.groupingBy(
					entry -> ((RegistryKey)entry.getKey()).getRegistry(), Collectors.toMap(entry -> ((RegistryKey)entry.getKey()).getValue(), java.util.Map.Entry::getValue)
				)
			);
		map.entrySet().stream().sorted(java.util.Map.Entry.comparingByKey()).forEach(entry -> {
			printWriter.printf("> Errors in registry %s:%n", entry.getKey());
			((Map)entry.getValue()).entrySet().stream().sorted(java.util.Map.Entry.comparingByKey()).forEach(element -> {
				printWriter.printf(">> Errors in element %s:%n", element.getKey());
				((Exception)element.getValue()).printStackTrace(printWriter);
			});
		});
		printWriter.flush();
		LOGGER.error("Registry loading errors:\n{}", stringWriter);
	}

	private static CrashException createLoadingException(Map<RegistryKey<?>, Exception> exceptions) {
		CrashReport crashReport = CrashReport.create(new IllegalStateException("Failed to load registries due to errors"), "Registry Loading");
		CrashReportSection crashReportSection = crashReport.addElement("Loading info");
		crashReportSection.add(
			"Errors",
			(CrashCallable<String>)(() -> {
				StringBuilder stringBuilder = new StringBuilder();
				exceptions.entrySet()
					.stream()
					.sorted(java.util.Map.Entry.comparingByKey(KEY_COMPARATOR))
					.forEach(
						entry -> stringBuilder.append("\n\t\t")
								.append(((RegistryKey)entry.getKey()).getRegistry())
								.append("/")
								.append(((RegistryKey)entry.getKey()).getValue())
								.append(": ")
								.append(((Exception)entry.getValue()).getMessage())
					);
				return stringBuilder.toString();
			})
		);
		return new CrashException(crashReport);
	}

	private static <E> void parseAndAdd(
		MutableRegistry<E> registry, Decoder<E> decoder, RegistryOps<JsonElement> ops, RegistryKey<E> key, Resource resource, RegistryEntryInfo entryInfo
	) throws IOException {
		Reader reader = resource.getReader();

		try {
			JsonElement jsonElement = JsonParser.parseReader(reader);
			DataResult<E> dataResult = decoder.parse(ops, jsonElement);
			E object = dataResult.getOrThrow();
			registry.add(key, object, entryInfo);
		} catch (Throwable var11) {
			if (reader != null) {
				try {
					reader.close();
				} catch (Throwable var10) {
					var11.addSuppressed(var10);
				}
			}

			throw var11;
		}

		if (reader != null) {
			reader.close();
		}
	}

	static <E> void loadFromResource(
		ResourceManager resourceManager,
		RegistryOps.RegistryInfoGetter infoGetter,
		MutableRegistry<E> registry,
		Decoder<E> elementDecoder,
		Map<RegistryKey<?>, Exception> errors
	) {
		String string = RegistryKeys.getPath(registry.getKey());
		ResourceFinder resourceFinder = ResourceFinder.json(string);
		RegistryOps<JsonElement> registryOps = RegistryOps.of(JsonOps.INSTANCE, infoGetter);

		for (java.util.Map.Entry<Identifier, Resource> entry : resourceFinder.findResources(resourceManager).entrySet()) {
			Identifier identifier = (Identifier)entry.getKey();
			RegistryKey<E> registryKey = RegistryKey.of(registry.getKey(), resourceFinder.toResourceId(identifier));
			Resource resource = (Resource)entry.getValue();
			RegistryEntryInfo registryEntryInfo = (RegistryEntryInfo)RESOURCE_ENTRY_INFO_GETTER.apply(resource.getKnownPackInfo());

			try {
				parseAndAdd(registry, elementDecoder, registryOps, registryKey, resource, registryEntryInfo);
			} catch (Exception var15) {
				errors.put(registryKey, new IllegalStateException(String.format(Locale.ROOT, "Failed to parse %s from pack %s", identifier, resource.getPackId()), var15));
			}
		}

		TagGroupLoader.loadInitial(resourceManager, registry);
	}

	static <E> void loadFromNetwork(
		Map<RegistryKey<? extends Registry<?>>, RegistryLoader.ElementsAndTags> data,
		ResourceFactory factory,
		RegistryOps.RegistryInfoGetter infoGetter,
		MutableRegistry<E> registry,
		Decoder<E> decoder,
		Map<RegistryKey<?>, Exception> loadingErrors
	) {
		RegistryLoader.ElementsAndTags elementsAndTags = (RegistryLoader.ElementsAndTags)data.get(registry.getKey());
		if (elementsAndTags != null) {
			RegistryOps<NbtElement> registryOps = RegistryOps.of(NbtOps.INSTANCE, infoGetter);
			RegistryOps<JsonElement> registryOps2 = RegistryOps.of(JsonOps.INSTANCE, infoGetter);
			String string = RegistryKeys.getPath(registry.getKey());
			ResourceFinder resourceFinder = ResourceFinder.json(string);

			for (SerializableRegistries.SerializedRegistryEntry serializedRegistryEntry : elementsAndTags.elements) {
				RegistryKey<E> registryKey = RegistryKey.of(registry.getKey(), serializedRegistryEntry.id());
				Optional<NbtElement> optional = serializedRegistryEntry.data();
				if (optional.isPresent()) {
					try {
						DataResult<E> dataResult = decoder.parse(registryOps, (NbtElement)optional.get());
						E object = dataResult.getOrThrow();
						registry.add(registryKey, object, EXPERIMENTAL_ENTRY_INFO);
					} catch (Exception var17) {
						loadingErrors.put(registryKey, new IllegalStateException(String.format(Locale.ROOT, "Failed to parse value %s from server", optional.get()), var17));
					}
				} else {
					Identifier identifier = resourceFinder.toResourcePath(serializedRegistryEntry.id());

					try {
						Resource resource = factory.getResourceOrThrow(identifier);
						parseAndAdd(registry, decoder, registryOps2, registryKey, resource, EXPERIMENTAL_ENTRY_INFO);
					} catch (Exception var18) {
						loadingErrors.put(registryKey, new IllegalStateException("Failed to parse local data", var18));
					}
				}
			}

			TagGroupLoader.loadFromNetwork(elementsAndTags.tags, registry);
		}
	}

	public static record ElementsAndTags(List<SerializableRegistries.SerializedRegistryEntry> elements, TagPacketSerializer.Serialized tags) {
	}

	public static record Entry<T>(RegistryKey<? extends Registry<T>> key, Codec<T> elementCodec, boolean requiredNonEmpty) {

		Entry(RegistryKey<? extends Registry<T>> key, Codec<T> codec) {
			this(key, codec, false);
		}

		RegistryLoader.Loader<T> getLoader(Lifecycle lifecycle, Map<RegistryKey<?>, Exception> errors) {
			MutableRegistry<T> mutableRegistry = new SimpleRegistry<>(this.key, lifecycle);
			return new RegistryLoader.Loader<>(this, mutableRegistry, errors);
		}

		public void addToCloner(BiConsumer<RegistryKey<? extends Registry<T>>, Codec<T>> callback) {
			callback.accept(this.key, this.elementCodec);
		}
	}

	static record Loader<T>(RegistryLoader.Entry<T> data, MutableRegistry<T> registry, Map<RegistryKey<?>, Exception> loadingErrors) {

		public void loadFromResource(ResourceManager resourceManager, RegistryOps.RegistryInfoGetter infoGetter) {
			RegistryLoader.loadFromResource(resourceManager, infoGetter, this.registry, this.data.elementCodec, this.loadingErrors);
		}

		public void loadFromNetwork(
			Map<RegistryKey<? extends Registry<?>>, RegistryLoader.ElementsAndTags> data, ResourceFactory factory, RegistryOps.RegistryInfoGetter infoGetter
		) {
			RegistryLoader.loadFromNetwork(data, factory, infoGetter, this.registry, this.data.elementCodec, this.loadingErrors);
		}
	}

	@FunctionalInterface
	interface RegistryLoadable {
		void apply(RegistryLoader.Loader<?> loader, RegistryOps.RegistryInfoGetter infoGetter);
	}
}
