package net.minecraft.world.biome;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.longs.Long2FloatLinkedOpenHashMap;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FluidBlock;
import net.minecraft.client.color.world.FoliageColors;
import net.minecraft.client.color.world.GrassColors;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.sound.BiomeAdditionsSound;
import net.minecraft.sound.BiomeMoodSound;
import net.minecraft.sound.MusicSound;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.Util;
import net.minecraft.util.collection.WeightedPicker;
import net.minecraft.util.crash.CrashCallable;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.dynamic.RegistryElementCodec;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.noise.OctaveSimplexNoiseSampler;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.ChunkRegion;
import net.minecraft.world.LightType;
import net.minecraft.world.WorldView;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.ChunkRandom;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.carver.CarverConfig;
import net.minecraft.world.gen.carver.ConfiguredCarver;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.ConfiguredStructureFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.StructureFeature;
import net.minecraft.world.gen.surfacebuilder.ConfiguredSurfaceBuilder;
import net.minecraft.world.gen.surfacebuilder.SurfaceConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class Biome {
	public static final Logger LOGGER = LogManager.getLogger();
	public static final MapCodec<Biome> CODEC = RecordCodecBuilder.mapCodec(
		instance -> instance.group(
					Biome.Weather.CODEC.forGetter(biome -> biome.weather),
					Biome.Category.CODEC.fieldOf("category").forGetter(biome -> biome.category),
					Codec.FLOAT.fieldOf("depth").forGetter(biome -> biome.depth),
					Codec.FLOAT.fieldOf("scale").forGetter(biome -> biome.scale),
					BiomeEffects.CODEC.fieldOf("effects").forGetter(biome -> biome.effects),
					Biome.GenerationSettings.CODEC.forGetter(biome -> biome.generationSettings),
					Biome.SpawnSettings.CODEC.forGetter(biome -> biome.spawnSettings),
					Codec.STRING.optionalFieldOf("parent").forGetter(biome -> Optional.ofNullable(biome.parent))
				)
				.apply(instance, Biome::new)
	);
	public static final Codec<Supplier<Biome>> REGISTRY_CODEC = RegistryElementCodec.of(Registry.BIOME_KEY, CODEC);
	public static final Set<Biome> BIOMES = Sets.<Biome>newHashSet();
	private static final OctaveSimplexNoiseSampler TEMPERATURE_NOISE = new OctaveSimplexNoiseSampler(new ChunkRandom(1234L), ImmutableList.of(0));
	private static final OctaveSimplexNoiseSampler field_26392 = new OctaveSimplexNoiseSampler(new ChunkRandom(3456L), ImmutableList.of(-2, -1, 0));
	public static final OctaveSimplexNoiseSampler FOLIAGE_NOISE = new OctaveSimplexNoiseSampler(new ChunkRandom(2345L), ImmutableList.of(0));
	private final Biome.Weather weather;
	private final Biome.GenerationSettings generationSettings;
	private final Biome.SpawnSettings spawnSettings;
	private final float depth;
	private final float scale;
	@Nullable
	protected final String parent;
	private final Biome.Category category;
	private final BiomeEffects effects;
	private final List<ConfiguredFeature<?, ?>> flowerFeatures;
	private final ThreadLocal<Long2FloatLinkedOpenHashMap> temperatureCache = ThreadLocal.withInitial(() -> Util.make(() -> {
			Long2FloatLinkedOpenHashMap long2FloatLinkedOpenHashMap = new Long2FloatLinkedOpenHashMap(1024, 0.25F) {
				@Override
				protected void rehash(int i) {
				}
			};
			long2FloatLinkedOpenHashMap.defaultReturnValue(Float.NaN);
			return long2FloatLinkedOpenHashMap;
		}));

	public Biome(Biome.Settings settings) {
		if (settings.surfaceBuilder != null
			&& settings.precipitation != null
			&& settings.category != null
			&& settings.depth != null
			&& settings.scale != null
			&& settings.temperature != null
			&& settings.downfall != null
			&& settings.specialEffects != null) {
			this.weather = new Biome.Weather(settings.precipitation, settings.temperature, settings.temperatureModifier, settings.downfall);
			this.category = settings.category;
			this.depth = settings.depth;
			this.scale = settings.scale;
			this.parent = settings.parent;
			this.effects = settings.specialEffects;
			this.spawnSettings = new Biome.SpawnSettings(settings.creatureGenerationProbability);
			this.generationSettings = new Biome.GenerationSettings(settings.surfaceBuilder);
			this.flowerFeatures = Lists.<ConfiguredFeature<?, ?>>newArrayList();
		} else {
			throw new IllegalStateException("You are missing parameters to build a proper biome for " + this.getClass().getSimpleName() + "\n" + settings);
		}
	}

	private Biome(
		Biome.Weather weather,
		Biome.Category category,
		float depth,
		float scale,
		BiomeEffects effects,
		Biome.GenerationSettings generationSettings,
		Biome.SpawnSettings spawnSettings,
		Optional<String> parent
	) {
		this.weather = weather;
		this.generationSettings = generationSettings;
		this.spawnSettings = spawnSettings;
		this.category = category;
		this.depth = depth;
		this.scale = scale;
		this.effects = effects;
		this.parent = (String)parent.orElse(null);
		this.flowerFeatures = (List<ConfiguredFeature<?, ?>>)generationSettings.features
			.stream()
			.flatMap(Collection::stream)
			.map(Supplier::get)
			.flatMap(ConfiguredFeature::method_30648)
			.filter(configuredFeature -> configuredFeature.feature == Feature.FLOWER)
			.collect(Collectors.toList());
	}

	public boolean hasParent() {
		return this.parent != null;
	}

	@Environment(EnvType.CLIENT)
	public int getSkyColor() {
		return this.effects.getSkyColor();
	}

	public void addSpawn(SpawnGroup group, Biome.SpawnEntry spawnEntry) {
		((List)this.spawnSettings.spawners.get(group)).add(spawnEntry);
	}

	public void addSpawnDensity(EntityType<?> type, double maxMass, double mass) {
		this.spawnSettings.spawnCosts.put(type, new Biome.SpawnDensity(mass, maxMass));
	}

	public List<Biome.SpawnEntry> getEntitySpawnList(SpawnGroup group) {
		return (List<Biome.SpawnEntry>)this.spawnSettings.spawners.get(group);
	}

	@Nullable
	public Biome.SpawnDensity getSpawnDensity(EntityType<?> type) {
		return (Biome.SpawnDensity)this.spawnSettings.spawnCosts.get(type);
	}

	public Biome.Precipitation getPrecipitation() {
		return this.weather.precipitation;
	}

	public boolean hasHighHumidity() {
		return this.getDownfall() > 0.85F;
	}

	public float getMaxSpawnChance() {
		return this.spawnSettings.creatureSpawnProbability;
	}

	private float computeTemperature(BlockPos pos) {
		float f = this.weather.temperatureModifier.getModifiedTemperature(pos, this.getTemperature());
		if (pos.getY() > 64) {
			float g = (float)(TEMPERATURE_NOISE.sample((double)((float)pos.getX() / 8.0F), (double)((float)pos.getZ() / 8.0F), false) * 4.0);
			return f - (g + (float)pos.getY() - 64.0F) * 0.05F / 30.0F;
		} else {
			return f;
		}
	}

	public final float getTemperature(BlockPos blockPos) {
		long l = blockPos.asLong();
		Long2FloatLinkedOpenHashMap long2FloatLinkedOpenHashMap = (Long2FloatLinkedOpenHashMap)this.temperatureCache.get();
		float f = long2FloatLinkedOpenHashMap.get(l);
		if (!Float.isNaN(f)) {
			return f;
		} else {
			float g = this.computeTemperature(blockPos);
			if (long2FloatLinkedOpenHashMap.size() == 1024) {
				long2FloatLinkedOpenHashMap.removeFirstFloat();
			}

			long2FloatLinkedOpenHashMap.put(l, g);
			return g;
		}
	}

	public boolean canSetIce(WorldView world, BlockPos blockPos) {
		return this.canSetIce(world, blockPos, true);
	}

	public boolean canSetIce(WorldView world, BlockPos pos, boolean doWaterCheck) {
		if (this.getTemperature(pos) >= 0.15F) {
			return false;
		} else {
			if (pos.getY() >= 0 && pos.getY() < 256 && world.getLightLevel(LightType.BLOCK, pos) < 10) {
				BlockState blockState = world.getBlockState(pos);
				FluidState fluidState = world.getFluidState(pos);
				if (fluidState.getFluid() == Fluids.WATER && blockState.getBlock() instanceof FluidBlock) {
					if (!doWaterCheck) {
						return true;
					}

					boolean bl = world.isWater(pos.west()) && world.isWater(pos.east()) && world.isWater(pos.north()) && world.isWater(pos.south());
					if (!bl) {
						return true;
					}
				}
			}

			return false;
		}
	}

	public boolean canSetSnow(WorldView world, BlockPos blockPos) {
		if (this.getTemperature(blockPos) >= 0.15F) {
			return false;
		} else {
			if (blockPos.getY() >= 0 && blockPos.getY() < 256 && world.getLightLevel(LightType.BLOCK, blockPos) < 10) {
				BlockState blockState = world.getBlockState(blockPos);
				if (blockState.isAir() && Blocks.SNOW.getDefaultState().canPlaceAt(world, blockPos)) {
					return true;
				}
			}

			return false;
		}
	}

	public void addFeature(GenerationStep.Feature step, ConfiguredFeature<?, ?> configuredFeature) {
		this.addFeature(step.ordinal(), () -> configuredFeature);
	}

	public void addFeature(int stepIndex, Supplier<ConfiguredFeature<?, ?>> supplier) {
		((ConfiguredFeature)supplier.get()).method_30648().filter(configuredFeature -> configuredFeature.feature == Feature.FLOWER).forEach(this.flowerFeatures::add);
		this.method_30775(stepIndex);
		((List)this.generationSettings.features.get(stepIndex)).add(supplier);
	}

	public <C extends CarverConfig> void addCarver(GenerationStep.Carver step, ConfiguredCarver<C> configuredCarver) {
		((List)this.generationSettings.carvers.computeIfAbsent(step, carver -> Lists.newArrayList())).add((Supplier)() -> configuredCarver);
	}

	public List<Supplier<ConfiguredCarver<?>>> getCarversForStep(GenerationStep.Carver carver) {
		return (List<Supplier<ConfiguredCarver<?>>>)this.generationSettings.carvers.getOrDefault(carver, ImmutableList.of());
	}

	public void addStructureFeature(ConfiguredStructureFeature<?, ?> configuredStructureFeature) {
		this.generationSettings.starts.add((Supplier)() -> configuredStructureFeature);
		this.method_30775(configuredStructureFeature.feature.getGenerationStep().ordinal());
	}

	public boolean hasStructureFeature(StructureFeature<?> structureFeature) {
		return this.generationSettings.starts.stream().anyMatch(supplier -> ((ConfiguredStructureFeature)supplier.get()).feature == structureFeature);
	}

	public Iterable<Supplier<ConfiguredStructureFeature<?, ?>>> getStructureFeatures() {
		return this.generationSettings.starts;
	}

	private void method_30775(int i) {
		while (this.generationSettings.features.size() <= i) {
			this.generationSettings.features.add(Lists.newArrayList());
		}
	}

	public ConfiguredStructureFeature<?, ?> method_28405(ConfiguredStructureFeature<?, ?> configuredStructureFeature) {
		return DataFixUtils.orElse(
			this.generationSettings
				.starts
				.stream()
				.map(Supplier::get)
				.filter(configuredStructureFeature2 -> configuredStructureFeature2.feature == configuredStructureFeature.feature)
				.findAny(),
			configuredStructureFeature
		);
	}

	public List<ConfiguredFeature<?, ?>> getFlowerFeatures() {
		return this.flowerFeatures;
	}

	/**
	 * Returns the lists of features configured for each {@link net.minecraft.world.gen.GenerationStep.Feature feature generation step}, up to the highest step that has a configured feature.
	 * Entries are guaranteed to not be null, but may be empty lists if an earlier step has no features, but a later step does.
	 */
	public List<List<Supplier<ConfiguredFeature<?, ?>>>> getFeatures() {
		return this.generationSettings.features;
	}

	public void generateFeatureStep(
		StructureAccessor structureAccessor, ChunkGenerator chunkGenerator, ChunkRegion region, long populationSeed, ChunkRandom random, BlockPos pos
	) {
		for (int i = 0; i < this.generationSettings.features.size(); i++) {
			int j = 0;
			if (structureAccessor.shouldGenerateStructures()) {
				for (StructureFeature<?> structureFeature : Registry.STRUCTURE_FEATURE) {
					if (structureFeature.getGenerationStep().ordinal() == i) {
						random.setDecoratorSeed(populationSeed, j, i);
						int k = pos.getX() >> 4;
						int l = pos.getZ() >> 4;
						int m = k << 4;
						int n = l << 4;

						try {
							structureAccessor.getStructuresWithChildren(ChunkSectionPos.from(pos), structureFeature)
								.forEach(
									structureStart -> structureStart.generateStructure(
											region, structureAccessor, chunkGenerator, random, new BlockBox(m, n, m + 15, n + 15), new ChunkPos(k, l)
										)
								);
						} catch (Exception var18) {
							CrashReport crashReport = CrashReport.create(var18, "Feature placement");
							crashReport.addElement("Feature")
								.add("Id", Registry.STRUCTURE_FEATURE.getId(structureFeature))
								.add("Description", (CrashCallable<String>)(() -> structureFeature.toString()));
							throw new CrashException(crashReport);
						}

						j++;
					}
				}
			}

			for (Supplier<ConfiguredFeature<?, ?>> supplier : (List)this.generationSettings.features.get(i)) {
				ConfiguredFeature<?, ?> configuredFeature = (ConfiguredFeature<?, ?>)supplier.get();
				random.setDecoratorSeed(populationSeed, j, i);

				try {
					configuredFeature.generate(region, chunkGenerator, random, pos);
				} catch (Exception var19) {
					CrashReport crashReport2 = CrashReport.create(var19, "Feature placement");
					crashReport2.addElement("Feature")
						.add("Id", Registry.FEATURE.getId(configuredFeature.feature))
						.add("Config", configuredFeature.config)
						.add("Description", (CrashCallable<String>)(() -> configuredFeature.feature.toString()));
					throw new CrashException(crashReport2);
				}

				j++;
			}
		}
	}

	@Environment(EnvType.CLIENT)
	public int getFogColor() {
		return this.effects.getFogColor();
	}

	@Environment(EnvType.CLIENT)
	public int getGrassColorAt(double x, double z) {
		int i = (Integer)this.effects.getGrassColor().orElseGet(this::getDefaultGrassColor);
		return this.effects.getGrassColorModifier().getModifiedGrassColor(x, z, i);
	}

	@Environment(EnvType.CLIENT)
	private int getDefaultGrassColor() {
		double d = (double)MathHelper.clamp(this.weather.temperature, 0.0F, 1.0F);
		double e = (double)MathHelper.clamp(this.weather.downfall, 0.0F, 1.0F);
		return GrassColors.getColor(d, e);
	}

	@Environment(EnvType.CLIENT)
	public int getFoliageColor() {
		return (Integer)this.effects.getFoliageColor().orElseGet(this::getDefaultFoliageColor);
	}

	@Environment(EnvType.CLIENT)
	private int getDefaultFoliageColor() {
		double d = (double)MathHelper.clamp(this.weather.temperature, 0.0F, 1.0F);
		double e = (double)MathHelper.clamp(this.weather.downfall, 0.0F, 1.0F);
		return FoliageColors.getColor(d, e);
	}

	public void buildSurface(
		Random random, Chunk chunk, int x, int z, int worldHeight, double noise, BlockState defaultBlock, BlockState defaultFluid, int seaLevel, long seed
	) {
		ConfiguredSurfaceBuilder<?> configuredSurfaceBuilder = (ConfiguredSurfaceBuilder<?>)this.generationSettings.surfaceBuilder.get();
		configuredSurfaceBuilder.initSeed(seed);
		configuredSurfaceBuilder.generate(random, chunk, this, x, z, worldHeight, noise, defaultBlock, defaultFluid, seaLevel, seed);
	}

	public Biome.TemperatureGroup getTemperatureGroup() {
		if (this.category == Biome.Category.OCEAN) {
			return Biome.TemperatureGroup.OCEAN;
		} else if ((double)this.getTemperature() < 0.2) {
			return Biome.TemperatureGroup.COLD;
		} else {
			return (double)this.getTemperature() < 1.0 ? Biome.TemperatureGroup.MEDIUM : Biome.TemperatureGroup.WARM;
		}
	}

	public final float getDepth() {
		return this.depth;
	}

	public final float getDownfall() {
		return this.weather.downfall;
	}

	public final float getScale() {
		return this.scale;
	}

	public final float getTemperature() {
		return this.weather.temperature;
	}

	public BiomeEffects getEffects() {
		return this.effects;
	}

	@Environment(EnvType.CLIENT)
	public final int getWaterColor() {
		return this.effects.getWaterColor();
	}

	@Environment(EnvType.CLIENT)
	public final int getWaterFogColor() {
		return this.effects.getWaterFogColor();
	}

	@Environment(EnvType.CLIENT)
	public Optional<BiomeParticleConfig> getParticleConfig() {
		return this.effects.getParticleConfig();
	}

	@Environment(EnvType.CLIENT)
	public Optional<SoundEvent> getLoopSound() {
		return this.effects.getLoopSound();
	}

	@Environment(EnvType.CLIENT)
	public Optional<BiomeMoodSound> getMoodSound() {
		return this.effects.getMoodSound();
	}

	@Environment(EnvType.CLIENT)
	public Optional<BiomeAdditionsSound> getAdditionsSound() {
		return this.effects.getAdditionsSound();
	}

	@Environment(EnvType.CLIENT)
	public Optional<MusicSound> getMusic() {
		return this.effects.getMusic();
	}

	public final Biome.Category getCategory() {
		return this.category;
	}

	public Supplier<ConfiguredSurfaceBuilder<?>> getSurfaceBuilder() {
		return this.generationSettings.surfaceBuilder;
	}

	public SurfaceConfig getSurfaceConfig() {
		return ((ConfiguredSurfaceBuilder)this.generationSettings.surfaceBuilder.get()).getConfig();
	}

	@Nullable
	public String getParent() {
		return this.parent;
	}

	public String toString() {
		Identifier identifier = BuiltinRegistries.BIOME.getId(this);
		return identifier == null ? super.toString() : identifier.toString();
	}

	public static enum Category implements StringIdentifiable {
		NONE("none"),
		TAIGA("taiga"),
		EXTREME_HILLS("extreme_hills"),
		JUNGLE("jungle"),
		MESA("mesa"),
		PLAINS("plains"),
		SAVANNA("savanna"),
		ICY("icy"),
		THEEND("the_end"),
		BEACH("beach"),
		FOREST("forest"),
		OCEAN("ocean"),
		DESERT("desert"),
		RIVER("river"),
		SWAMP("swamp"),
		MUSHROOM("mushroom"),
		NETHER("nether");

		public static final Codec<Biome.Category> CODEC = StringIdentifiable.createCodec(Biome.Category::values, Biome.Category::byName);
		private static final Map<String, Biome.Category> BY_NAME = (Map<String, Biome.Category>)Arrays.stream(values())
			.collect(Collectors.toMap(Biome.Category::getName, category -> category));
		private final String name;

		private Category(String name) {
			this.name = name;
		}

		public String getName() {
			return this.name;
		}

		public static Biome.Category byName(String name) {
			return (Biome.Category)BY_NAME.get(name);
		}

		@Override
		public String asString() {
			return this.name;
		}
	}

	static class GenerationSettings {
		public static final MapCodec<Biome.GenerationSettings> CODEC = RecordCodecBuilder.mapCodec(
			instance -> instance.group(
						ConfiguredSurfaceBuilder.field_25015.fieldOf("surface_builder").forGetter(generationSettings -> generationSettings.surfaceBuilder),
						Codec.simpleMap(
								GenerationStep.Carver.field_24770,
								ConfiguredCarver.field_24828.listOf().promotePartial(Util.method_29188("Carver: ", Biome.LOGGER::error)),
								StringIdentifiable.method_28142(GenerationStep.Carver.values())
							)
							.fieldOf("carvers")
							.forGetter(generationSettings -> generationSettings.carvers),
						ConfiguredFeature.CODEC
							.listOf()
							.promotePartial(Util.method_29188("Feature: ", Biome.LOGGER::error))
							.listOf()
							.fieldOf("features")
							.forGetter(generationSettings -> generationSettings.features),
						ConfiguredStructureFeature.REGISTRY_CODEC
							.listOf()
							.promotePartial(Util.method_29188("Structure start: ", Biome.LOGGER::error))
							.fieldOf("starts")
							.forGetter(generationSettings -> generationSettings.starts)
					)
					.apply(instance, Biome.GenerationSettings::new)
		);
		private final Supplier<ConfiguredSurfaceBuilder<?>> surfaceBuilder;
		private final Map<GenerationStep.Carver, List<Supplier<ConfiguredCarver<?>>>> carvers;
		private final List<List<Supplier<ConfiguredFeature<?, ?>>>> features;
		private final List<Supplier<ConfiguredStructureFeature<?, ?>>> starts;

		private GenerationSettings(
			Supplier<ConfiguredSurfaceBuilder<?>> surfaceBuilder,
			Map<GenerationStep.Carver, List<Supplier<ConfiguredCarver<?>>>> carvers,
			List<List<Supplier<ConfiguredFeature<?, ?>>>> features,
			List<Supplier<ConfiguredStructureFeature<?, ?>>> starts
		) {
			this.surfaceBuilder = surfaceBuilder;
			this.carvers = carvers;
			this.features = features;
			this.starts = starts;
		}

		private GenerationSettings(Supplier<ConfiguredSurfaceBuilder<?>> surfaceBuilder) {
			this(
				surfaceBuilder,
				Maps.<GenerationStep.Carver, List<Supplier<ConfiguredCarver<?>>>>newLinkedHashMap(),
				Lists.<List<Supplier<ConfiguredFeature<?, ?>>>>newArrayList(),
				Lists.<Supplier<ConfiguredStructureFeature<?, ?>>>newArrayList()
			);
		}
	}

	/**
	 * Represents a point in a multi-dimensional cartesian plane. Mixed-noise
	 * biome generator picks the closest noise point from its selected point
	 * and choose the biome associated to that closest point. Another factor,
	 * rarity potential, favors larger differences in values instead, contrary
	 * to other point values.
	 */
	public static class MixedNoisePoint {
		public static final Codec<Biome.MixedNoisePoint> CODEC = RecordCodecBuilder.create(
			instance -> instance.group(
						Codec.floatRange(-2.0F, 2.0F).fieldOf("temperature").forGetter(mixedNoisePoint -> mixedNoisePoint.temperature),
						Codec.floatRange(-2.0F, 2.0F).fieldOf("humidity").forGetter(mixedNoisePoint -> mixedNoisePoint.humidity),
						Codec.floatRange(-2.0F, 2.0F).fieldOf("altitude").forGetter(mixedNoisePoint -> mixedNoisePoint.altitude),
						Codec.floatRange(-2.0F, 2.0F).fieldOf("weirdness").forGetter(mixedNoisePoint -> mixedNoisePoint.weirdness),
						Codec.floatRange(0.0F, 1.0F).fieldOf("offset").forGetter(mixedNoisePoint -> mixedNoisePoint.weight)
					)
					.apply(instance, Biome.MixedNoisePoint::new)
		);
		private final float temperature;
		private final float humidity;
		private final float altitude;
		private final float weirdness;
		/**
		 * This value awards another point with value farthest from this one; i.e.
		 * unlike other points where closer distance is better, for this value the
		 * farther the better. The result of the different values can be
		 * approximately modeled by a hyperbola weight=cosh(peak-1) as used by the
		 * mixed-noise generator.
		 */
		private final float weight;

		public MixedNoisePoint(float temperature, float humidity, float altitude, float weirdness, float weight) {
			this.temperature = temperature;
			this.humidity = humidity;
			this.altitude = altitude;
			this.weirdness = weirdness;
			this.weight = weight;
		}

		public boolean equals(Object object) {
			if (this == object) {
				return true;
			} else if (object != null && this.getClass() == object.getClass()) {
				Biome.MixedNoisePoint mixedNoisePoint = (Biome.MixedNoisePoint)object;
				if (Float.compare(mixedNoisePoint.temperature, this.temperature) != 0) {
					return false;
				} else if (Float.compare(mixedNoisePoint.humidity, this.humidity) != 0) {
					return false;
				} else {
					return Float.compare(mixedNoisePoint.altitude, this.altitude) != 0 ? false : Float.compare(mixedNoisePoint.weirdness, this.weirdness) == 0;
				}
			} else {
				return false;
			}
		}

		public int hashCode() {
			int i = this.temperature != 0.0F ? Float.floatToIntBits(this.temperature) : 0;
			i = 31 * i + (this.humidity != 0.0F ? Float.floatToIntBits(this.humidity) : 0);
			i = 31 * i + (this.altitude != 0.0F ? Float.floatToIntBits(this.altitude) : 0);
			return 31 * i + (this.weirdness != 0.0F ? Float.floatToIntBits(this.weirdness) : 0);
		}

		/**
		 * Calculates the distance from this noise point to another one. The
		 * distance is a squared distance in a multi-dimensional cartesian plane
		 * from a mathematical point of view, with a special parameter that
		 * reduces the calculated distance.
		 * 
		 * <p>For most fields except rarity potential, smaller difference between
		 * two points' fields will lead to smaller distance. For rarity potential,
		 * larger differences lead to smaller distance.
		 * 
		 * <p>This distance is used by the mixed-noise biome layer source. The
		 * layer source calculates an arbitrary noise point, and selects the
		 * biome that offers a closest point to its arbitrary point.
		 * 
		 * @param other the other noise point
		 */
		public float calculateDistanceTo(Biome.MixedNoisePoint other) {
			return (this.temperature - other.temperature) * (this.temperature - other.temperature)
				+ (this.humidity - other.humidity) * (this.humidity - other.humidity)
				+ (this.altitude - other.altitude) * (this.altitude - other.altitude)
				+ (this.weirdness - other.weirdness) * (this.weirdness - other.weirdness)
				+ (this.weight - other.weight) * (this.weight - other.weight);
		}
	}

	public static enum Precipitation implements StringIdentifiable {
		NONE("none"),
		RAIN("rain"),
		SNOW("snow");

		public static final Codec<Biome.Precipitation> CODEC = StringIdentifiable.createCodec(Biome.Precipitation::values, Biome.Precipitation::byName);
		private static final Map<String, Biome.Precipitation> BY_NAME = (Map<String, Biome.Precipitation>)Arrays.stream(values())
			.collect(Collectors.toMap(Biome.Precipitation::getName, precipitation -> precipitation));
		private final String name;

		private Precipitation(String name) {
			this.name = name;
		}

		public String getName() {
			return this.name;
		}

		public static Biome.Precipitation byName(String name) {
			return (Biome.Precipitation)BY_NAME.get(name);
		}

		@Override
		public String asString() {
			return this.name;
		}
	}

	public static class Settings {
		@Nullable
		private Supplier<ConfiguredSurfaceBuilder<?>> surfaceBuilder;
		@Nullable
		private Biome.Precipitation precipitation;
		@Nullable
		private Biome.Category category;
		@Nullable
		private Float depth;
		@Nullable
		private Float scale;
		@Nullable
		private Float temperature;
		private Biome.TemperatureModifier temperatureModifier = Biome.TemperatureModifier.NONE;
		@Nullable
		private Float downfall;
		@Nullable
		private String parent;
		@Nullable
		private BiomeEffects specialEffects;
		private float creatureGenerationProbability = 0.1F;

		public Biome.Settings surfaceBuilder(ConfiguredSurfaceBuilder<?> surfaceBuilder) {
			return this.surfaceBuilder(() -> surfaceBuilder);
		}

		public Biome.Settings surfaceBuilder(Supplier<ConfiguredSurfaceBuilder<?>> supplier) {
			this.surfaceBuilder = supplier;
			return this;
		}

		public Biome.Settings precipitation(Biome.Precipitation precipitation) {
			this.precipitation = precipitation;
			return this;
		}

		public Biome.Settings category(Biome.Category category) {
			this.category = category;
			return this;
		}

		public Biome.Settings depth(float depth) {
			this.depth = depth;
			return this;
		}

		public Biome.Settings scale(float scale) {
			this.scale = scale;
			return this;
		}

		public Biome.Settings temperature(float temperature) {
			this.temperature = temperature;
			return this;
		}

		public Biome.Settings downfall(float downfall) {
			this.downfall = downfall;
			return this;
		}

		/**
		 * Sets the biome that this will replace as a modified version of the biome.
		 * 
		 * @param parent the string identifier of the biome to be replaced
		 */
		public Biome.Settings parent(@Nullable String parent) {
			this.parent = parent;
			return this;
		}

		public Biome.Settings effects(BiomeEffects effects) {
			this.specialEffects = effects;
			return this;
		}

		public Biome.Settings creatureGenerationProbability(float probability) {
			this.creatureGenerationProbability = probability;
			return this;
		}

		public Biome.Settings temperatureModifier(Biome.TemperatureModifier temperatureModifier) {
			this.temperatureModifier = temperatureModifier;
			return this;
		}

		public String toString() {
			return "BiomeBuilder{\nsurfaceBuilder="
				+ this.surfaceBuilder
				+ ",\nprecipitation="
				+ this.precipitation
				+ ",\nbiomeCategory="
				+ this.category
				+ ",\ndepth="
				+ this.depth
				+ ",\nscale="
				+ this.scale
				+ ",\ntemperature="
				+ this.temperature
				+ ",\ntemperatureModifier="
				+ this.temperatureModifier
				+ ",\ndownfall="
				+ this.downfall
				+ ",\nspecialEffects="
				+ this.specialEffects
				+ ",\ncreatureGenerationProbability="
				+ this.creatureGenerationProbability
				+ ",\nparent='"
				+ this.parent
				+ '\''
				+ "\n"
				+ '}';
		}
	}

	/**
	 * Embodies the density limit information of a type of entity in entity
	 * spawning logic. The density field is generated for all entities spawned
	 * than a specific type of entity.
	 */
	public static class SpawnDensity {
		public static final Codec<Biome.SpawnDensity> CODEC = RecordCodecBuilder.create(
			instance -> instance.group(
						Codec.DOUBLE.fieldOf("energy_budget").forGetter(Biome.SpawnDensity::getGravityLimit),
						Codec.DOUBLE.fieldOf("charge").forGetter(Biome.SpawnDensity::getMass)
					)
					.apply(instance, Biome.SpawnDensity::new)
		);
		private final double gravityLimit;
		private final double mass;

		public SpawnDensity(double gravityLimit, double mass) {
			this.gravityLimit = gravityLimit;
			this.mass = mass;
		}

		/**
		 * Represents the cap of gravity as in {@link
		 * net.minecraft.util.math.GravityField#calculate(BlockPos, double)} for
		 * entity spawning. If the cap is exceeded, the entity spawning attempt
		 * will skip.
		 */
		public double getGravityLimit() {
			return this.gravityLimit;
		}

		/**
		 * Represents the mass of each entity spawned. Will affect gravity
		 * calculation.
		 */
		public double getMass() {
			return this.mass;
		}
	}

	public static class SpawnEntry extends WeightedPicker.Entry {
		public static final Codec<Biome.SpawnEntry> CODEC = RecordCodecBuilder.create(
			instance -> instance.group(
						Registry.ENTITY_TYPE.fieldOf("type").forGetter(spawnEntry -> spawnEntry.type),
						Codec.INT.fieldOf("weight").forGetter(spawnEntry -> spawnEntry.weight),
						Codec.INT.fieldOf("minCount").forGetter(spawnEntry -> spawnEntry.minGroupSize),
						Codec.INT.fieldOf("maxCount").forGetter(spawnEntry -> spawnEntry.maxGroupSize)
					)
					.apply(instance, Biome.SpawnEntry::new)
		);
		public final EntityType<?> type;
		public final int minGroupSize;
		public final int maxGroupSize;

		public SpawnEntry(EntityType<?> type, int weight, int minGroupSize, int maxGroupSize) {
			super(weight);
			this.type = type.getSpawnGroup() == SpawnGroup.MISC ? EntityType.PIG : type;
			this.minGroupSize = minGroupSize;
			this.maxGroupSize = maxGroupSize;
		}

		public String toString() {
			return EntityType.getId(this.type) + "*(" + this.minGroupSize + "-" + this.maxGroupSize + "):" + this.weight;
		}
	}

	static class SpawnSettings {
		public static final MapCodec<Biome.SpawnSettings> CODEC = RecordCodecBuilder.mapCodec(
			instance -> instance.group(
						Codec.FLOAT.optionalFieldOf("creature_spawn_probability", Float.valueOf(0.1F)).forGetter(spawnSettings -> spawnSettings.creatureSpawnProbability),
						Codec.simpleMap(
								SpawnGroup.field_24655,
								Biome.SpawnEntry.CODEC.listOf().promotePartial(Util.method_29188("Spawn data: ", Biome.LOGGER::error)),
								StringIdentifiable.method_28142(SpawnGroup.values())
							)
							.fieldOf("spawners")
							.forGetter(spawnSettings -> spawnSettings.spawners),
						Codec.simpleMap(Registry.ENTITY_TYPE, Biome.SpawnDensity.CODEC, Registry.ENTITY_TYPE)
							.fieldOf("spawn_costs")
							.forGetter(spawnSettings -> spawnSettings.spawnCosts)
					)
					.apply(instance, Biome.SpawnSettings::new)
		);
		private final float creatureSpawnProbability;
		private final Map<SpawnGroup, List<Biome.SpawnEntry>> spawners;
		private final Map<EntityType<?>, Biome.SpawnDensity> spawnCosts;

		private SpawnSettings(float creatureSpawnProbability, Map<SpawnGroup, List<Biome.SpawnEntry>> spawners, Map<EntityType<?>, Biome.SpawnDensity> spawnCosts) {
			this.creatureSpawnProbability = creatureSpawnProbability;
			this.spawners = spawners;
			this.spawnCosts = spawnCosts;
		}

		private SpawnSettings(float creatureSpawnProbability) {
			this(creatureSpawnProbability, Maps.<SpawnGroup, List<Biome.SpawnEntry>>newLinkedHashMap(), Maps.<EntityType<?>, Biome.SpawnDensity>newLinkedHashMap());

			for (SpawnGroup spawnGroup : SpawnGroup.values()) {
				this.spawners.put(spawnGroup, Lists.newArrayList());
			}
		}
	}

	public static enum TemperatureGroup {
		OCEAN("ocean"),
		COLD("cold"),
		MEDIUM("medium"),
		WARM("warm");

		private static final Map<String, Biome.TemperatureGroup> BY_NAME = (Map<String, Biome.TemperatureGroup>)Arrays.stream(values())
			.collect(Collectors.toMap(Biome.TemperatureGroup::getName, temperatureGroup -> temperatureGroup));
		private final String name;

		private TemperatureGroup(String name) {
			this.name = name;
		}

		public String getName() {
			return this.name;
		}
	}

	public static enum TemperatureModifier implements StringIdentifiable {
		NONE("none") {
			@Override
			public float getModifiedTemperature(BlockPos pos, float temperature) {
				return temperature;
			}
		},
		FROZEN("frozen") {
			@Override
			public float getModifiedTemperature(BlockPos pos, float temperature) {
				double d = Biome.field_26392.sample((double)pos.getX() * 0.05, (double)pos.getZ() * 0.05, false) * 7.0;
				double e = Biome.FOLIAGE_NOISE.sample((double)pos.getX() * 0.2, (double)pos.getZ() * 0.2, false);
				double f = d + e;
				if (f < 0.3) {
					double g = Biome.FOLIAGE_NOISE.sample((double)pos.getX() * 0.09, (double)pos.getZ() * 0.09, false);
					if (g < 0.8) {
						return 0.2F;
					}
				}

				return temperature;
			}
		};

		private final String name;
		public static final Codec<Biome.TemperatureModifier> CODEC = StringIdentifiable.createCodec(
			Biome.TemperatureModifier::values, Biome.TemperatureModifier::byName
		);
		private static final Map<String, Biome.TemperatureModifier> BY_NAME = (Map<String, Biome.TemperatureModifier>)Arrays.stream(values())
			.collect(Collectors.toMap(Biome.TemperatureModifier::getName, temperatureModifier -> temperatureModifier));

		public abstract float getModifiedTemperature(BlockPos pos, float temperature);

		private TemperatureModifier(String name) {
			this.name = name;
		}

		public String getName() {
			return this.name;
		}

		@Override
		public String asString() {
			return this.name;
		}

		public static Biome.TemperatureModifier byName(String name) {
			return (Biome.TemperatureModifier)BY_NAME.get(name);
		}
	}

	static class Weather {
		public static final MapCodec<Biome.Weather> CODEC = RecordCodecBuilder.mapCodec(
			instance -> instance.group(
						Biome.Precipitation.CODEC.fieldOf("precipitation").forGetter(weather -> weather.precipitation),
						Codec.FLOAT.fieldOf("temperature").forGetter(weather -> weather.temperature),
						Biome.TemperatureModifier.CODEC.optionalFieldOf("temperature_modifier", Biome.TemperatureModifier.NONE).forGetter(weather -> weather.temperatureModifier),
						Codec.FLOAT.fieldOf("downfall").forGetter(weather -> weather.downfall)
					)
					.apply(instance, Biome.Weather::new)
		);
		private final Biome.Precipitation precipitation;
		private final float temperature;
		private final Biome.TemperatureModifier temperatureModifier;
		private final float downfall;

		private Weather(Biome.Precipitation precipitation, float temperature, Biome.TemperatureModifier temperatureModifier, float downfall) {
			this.precipitation = precipitation;
			this.temperature = temperature;
			this.temperatureModifier = temperatureModifier;
			this.downfall = downfall;
		}
	}
}
