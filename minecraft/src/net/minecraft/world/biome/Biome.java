package net.minecraft.world.biome;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.longs.Long2FloatLinkedOpenHashMap;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
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
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.Util;
import net.minecraft.util.collection.IdList;
import net.minecraft.util.collection.WeightedPicker;
import net.minecraft.util.crash.CrashCallable;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.noise.OctaveSimplexNoiseSampler;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.LightType;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.WorldView;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.ChunkRandom;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.carver.Carver;
import net.minecraft.world.gen.carver.CarverConfig;
import net.minecraft.world.gen.carver.ConfiguredCarver;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.ConfiguredStructureFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.StructureFeature;
import net.minecraft.world.gen.surfacebuilder.ConfiguredSurfaceBuilder;
import net.minecraft.world.gen.surfacebuilder.SurfaceBuilder;
import net.minecraft.world.gen.surfacebuilder.SurfaceConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Biome {
	public static final Logger LOGGER = LogManager.getLogger();
	public static final Codec<Biome> field_24677 = RecordCodecBuilder.create(
		instance -> instance.group(
					Biome.Precipitation.field_24680.fieldOf("precipitation").forGetter(biome -> biome.precipitation),
					Biome.Category.field_24678.fieldOf("category").forGetter(biome -> biome.category),
					Codec.FLOAT.fieldOf("depth").forGetter(biome -> biome.depth),
					Codec.FLOAT.fieldOf("scale").forGetter(biome -> biome.scale),
					Codec.FLOAT.fieldOf("temperature").forGetter(biome -> biome.temperature),
					Codec.FLOAT.fieldOf("downfall").forGetter(biome -> biome.downfall),
					BiomeEffects.CODEC.fieldOf("effects").forGetter(biome -> biome.effects),
					Codec.INT.fieldOf("sky_color").forGetter(biome -> biome.skyColor),
					ConfiguredSurfaceBuilder.field_25015.fieldOf("surface_builder").forGetter(biome -> biome.surfaceBuilder),
					Codec.simpleMap(
							GenerationStep.Carver.field_24770,
							ConfiguredCarver.field_24828.listOf().promotePartial(Util.method_29188("Carver: ", LOGGER::error)),
							StringIdentifiable.method_28142(GenerationStep.Carver.values())
						)
						.fieldOf("carvers")
						.forGetter(biome -> biome.carvers),
					Codec.simpleMap(
							GenerationStep.Feature.CODEC,
							ConfiguredFeature.CODEC.listOf().promotePartial(Util.method_29188("Feature: ", LOGGER::error)),
							StringIdentifiable.method_28142(GenerationStep.Feature.values())
						)
						.fieldOf("features")
						.forGetter(biome -> biome.features),
					ConfiguredStructureFeature.field_24834
						.listOf()
						.promotePartial(Util.method_29188("Structure start: ", LOGGER::error))
						.fieldOf("starts")
						.forGetter(
							biome -> (List)biome.structureFeatures
									.values()
									.stream()
									.sorted(Comparator.comparing(configuredStructureFeature -> Registry.STRUCTURE_FEATURE.getId(configuredStructureFeature.field_24835)))
									.collect(Collectors.toList())
						),
					Codec.simpleMap(
							SpawnGroup.field_24655,
							Biome.SpawnEntry.CODEC.listOf().promotePartial(Util.method_29188("Spawn data: ", LOGGER::error)),
							StringIdentifiable.method_28142(SpawnGroup.values())
						)
						.fieldOf("spawners")
						.forGetter(biome -> biome.spawns),
					Biome.MixedNoisePoint.CODEC.listOf().fieldOf("climate_parameters").forGetter(biome -> biome.noisePoints),
					Codec.STRING.optionalFieldOf("parent").forGetter(biome -> Optional.ofNullable(biome.parent))
				)
				.apply(instance, Biome::new)
	);
	public static final Set<Biome> BIOMES = Sets.<Biome>newHashSet();
	public static final IdList<Biome> PARENT_BIOME_ID_MAP = new IdList<>();
	protected static final OctaveSimplexNoiseSampler TEMPERATURE_NOISE = new OctaveSimplexNoiseSampler(new ChunkRandom(1234L), ImmutableList.of(0));
	public static final OctaveSimplexNoiseSampler FOLIAGE_NOISE = new OctaveSimplexNoiseSampler(new ChunkRandom(2345L), ImmutableList.of(0));
	@Nullable
	protected String translationKey;
	protected final float depth;
	protected final float scale;
	protected final float temperature;
	protected final float downfall;
	private final int skyColor;
	@Nullable
	protected final String parent;
	protected final ConfiguredSurfaceBuilder<?> surfaceBuilder;
	protected final Biome.Category category;
	protected final Biome.Precipitation precipitation;
	protected final BiomeEffects effects;
	protected final Map<GenerationStep.Carver, List<ConfiguredCarver<?>>> carvers;
	protected final Map<GenerationStep.Feature, List<ConfiguredFeature<?, ?>>> features;
	protected final List<ConfiguredFeature<?, ?>> flowerFeatures = Lists.<ConfiguredFeature<?, ?>>newArrayList();
	private final Map<StructureFeature<?>, ConfiguredStructureFeature<?, ?>> structureFeatures;
	private final Map<SpawnGroup, List<Biome.SpawnEntry>> spawns;
	private final Map<EntityType<?>, Biome.SpawnDensity> spawnDensities = Maps.<EntityType<?>, Biome.SpawnDensity>newHashMap();
	private final List<Biome.MixedNoisePoint> noisePoints;
	private final ThreadLocal<Long2FloatLinkedOpenHashMap> temperatureCache = ThreadLocal.withInitial(() -> Util.make(() -> {
			Long2FloatLinkedOpenHashMap long2FloatLinkedOpenHashMap = new Long2FloatLinkedOpenHashMap(1024, 0.25F) {
				@Override
				protected void rehash(int i) {
				}
			};
			long2FloatLinkedOpenHashMap.defaultReturnValue(Float.NaN);
			return long2FloatLinkedOpenHashMap;
		}));

	@Nullable
	public static Biome getModifiedBiome(Biome biome) {
		return PARENT_BIOME_ID_MAP.get(Registry.BIOME.getRawId(biome));
	}

	public static <C extends CarverConfig> ConfiguredCarver<C> configureCarver(Carver<C> carver, C config) {
		return new ConfiguredCarver<>(carver, config);
	}

	protected Biome(Biome.Settings settings) {
		if (settings.surfaceBuilder != null
			&& settings.precipitation != null
			&& settings.category != null
			&& settings.depth != null
			&& settings.scale != null
			&& settings.temperature != null
			&& settings.downfall != null
			&& settings.specialEffects != null) {
			this.surfaceBuilder = settings.surfaceBuilder;
			this.precipitation = settings.precipitation;
			this.category = settings.category;
			this.depth = settings.depth;
			this.scale = settings.scale;
			this.temperature = settings.temperature;
			this.downfall = settings.downfall;
			this.skyColor = this.calculateSkyColor();
			this.parent = settings.parent;
			this.noisePoints = (List<Biome.MixedNoisePoint>)(settings.noises != null ? settings.noises : ImmutableList.of());
			this.effects = settings.specialEffects;
			this.carvers = Maps.<GenerationStep.Carver, List<ConfiguredCarver<?>>>newHashMap();
			this.structureFeatures = Maps.<StructureFeature<?>, ConfiguredStructureFeature<?, ?>>newHashMap();
			this.features = Maps.<GenerationStep.Feature, List<ConfiguredFeature<?, ?>>>newHashMap();

			for (GenerationStep.Feature feature : GenerationStep.Feature.values()) {
				this.features.put(feature, Lists.newArrayList());
			}

			this.spawns = Maps.<SpawnGroup, List<Biome.SpawnEntry>>newHashMap();

			for (SpawnGroup spawnGroup : SpawnGroup.values()) {
				this.spawns.put(spawnGroup, Lists.newArrayList());
			}
		} else {
			throw new IllegalStateException("You are missing parameters to build a proper biome for " + this.getClass().getSimpleName() + "\n" + settings);
		}
	}

	private Biome(
		Biome.Precipitation precipitation,
		Biome.Category category,
		float f,
		float g,
		float h,
		float i,
		BiomeEffects biomeEffects,
		int j,
		ConfiguredSurfaceBuilder<?> configuredSurfaceBuilder,
		Map<GenerationStep.Carver, List<ConfiguredCarver<?>>> map,
		Map<GenerationStep.Feature, List<ConfiguredFeature<?, ?>>> map2,
		List<ConfiguredStructureFeature<?, ?>> list,
		Map<SpawnGroup, List<Biome.SpawnEntry>> map3,
		List<Biome.MixedNoisePoint> list2,
		Optional<String> optional
	) {
		this.precipitation = precipitation;
		this.category = category;
		this.depth = f;
		this.scale = g;
		this.temperature = h;
		this.downfall = i;
		this.effects = biomeEffects;
		this.skyColor = j;
		this.surfaceBuilder = configuredSurfaceBuilder;
		this.carvers = map;
		this.features = map2;
		this.structureFeatures = (Map<StructureFeature<?>, ConfiguredStructureFeature<?, ?>>)list.stream()
			.collect(Collectors.toMap(configuredStructureFeature -> configuredStructureFeature.field_24835, Function.identity()));
		this.spawns = map3;
		this.noisePoints = list2;
		this.parent = (String)optional.orElse(null);
		map2.values()
			.stream()
			.flatMap(Collection::stream)
			.filter(configuredFeature -> configuredFeature.feature == Feature.DECORATED_FLOWER)
			.forEach(this.flowerFeatures::add);
	}

	public boolean hasParent() {
		return this.parent != null;
	}

	private int calculateSkyColor() {
		float f = this.temperature;
		f /= 3.0F;
		f = MathHelper.clamp(f, -1.0F, 1.0F);
		return MathHelper.hsvToRgb(0.62222224F - f * 0.05F, 0.5F + f * 0.1F, 1.0F);
	}

	@Environment(EnvType.CLIENT)
	public int getSkyColor() {
		return this.skyColor;
	}

	protected void addSpawn(SpawnGroup group, Biome.SpawnEntry spawnEntry) {
		((List)this.spawns.get(group)).add(spawnEntry);
	}

	protected void addSpawnDensity(EntityType<?> type, double maxMass, double mass) {
		this.spawnDensities.put(type, new Biome.SpawnDensity(mass, maxMass));
	}

	public List<Biome.SpawnEntry> getEntitySpawnList(SpawnGroup group) {
		return (List<Biome.SpawnEntry>)this.spawns.get(group);
	}

	@Nullable
	public Biome.SpawnDensity getSpawnDensity(EntityType<?> type) {
		return (Biome.SpawnDensity)this.spawnDensities.get(type);
	}

	public Biome.Precipitation getPrecipitation() {
		return this.precipitation;
	}

	public boolean hasHighHumidity() {
		return this.getRainfall() > 0.85F;
	}

	public float getMaxSpawnChance() {
		return 0.1F;
	}

	protected float computeTemperature(BlockPos blockPos) {
		if (blockPos.getY() > 64) {
			float f = (float)(TEMPERATURE_NOISE.sample((double)((float)blockPos.getX() / 8.0F), (double)((float)blockPos.getZ() / 8.0F), false) * 4.0);
			return this.getTemperature() - (f + (float)blockPos.getY() - 64.0F) * 0.05F / 30.0F;
		} else {
			return this.getTemperature();
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
		if (configuredFeature.feature == Feature.DECORATED_FLOWER) {
			this.flowerFeatures.add(configuredFeature);
		}

		((List)this.features.get(step)).add(configuredFeature);
	}

	public <C extends CarverConfig> void addCarver(GenerationStep.Carver step, ConfiguredCarver<C> configuredCarver) {
		((List)this.carvers.computeIfAbsent(step, carver -> Lists.newArrayList())).add(configuredCarver);
	}

	public List<ConfiguredCarver<?>> getCarversForStep(GenerationStep.Carver carver) {
		return (List<ConfiguredCarver<?>>)this.carvers.computeIfAbsent(carver, carverx -> Lists.newArrayList());
	}

	public void addStructureFeature(ConfiguredStructureFeature<?, ?> configuredStructureFeature) {
		this.structureFeatures.put(configuredStructureFeature.field_24835, configuredStructureFeature);
	}

	public boolean hasStructureFeature(StructureFeature<?> structureFeature) {
		return this.structureFeatures.containsKey(structureFeature);
	}

	public Iterable<ConfiguredStructureFeature<?, ?>> method_28413() {
		return this.structureFeatures.values();
	}

	public ConfiguredStructureFeature<?, ?> method_28405(ConfiguredStructureFeature<?, ?> configuredStructureFeature) {
		return (ConfiguredStructureFeature<?, ?>)this.structureFeatures.getOrDefault(configuredStructureFeature.field_24835, configuredStructureFeature);
	}

	public List<ConfiguredFeature<?, ?>> getFlowerFeatures() {
		return this.flowerFeatures;
	}

	public List<ConfiguredFeature<?, ?>> getFeaturesForStep(GenerationStep.Feature feature) {
		return (List<ConfiguredFeature<?, ?>>)this.features.get(feature);
	}

	public void generateFeatureStep(
		GenerationStep.Feature step,
		StructureAccessor structureAccessor,
		ChunkGenerator chunkGenerator,
		ServerWorldAccess serverWorldAccess,
		long populationSeed,
		ChunkRandom chunkRandom,
		BlockPos pos
	) {
		int i = 0;
		if (structureAccessor.method_27834()) {
			for (StructureFeature<?> structureFeature : Registry.STRUCTURE_FEATURE) {
				if (structureFeature.method_28663() == step) {
					chunkRandom.setDecoratorSeed(populationSeed, i, step.ordinal());
					int j = pos.getX() >> 4;
					int k = pos.getZ() >> 4;
					int l = j << 4;
					int m = k << 4;

					try {
						structureAccessor.getStructuresWithChildren(ChunkSectionPos.from(pos), structureFeature)
							.forEach(
								structureStart -> structureStart.generateStructure(
										serverWorldAccess, structureAccessor, chunkGenerator, chunkRandom, new BlockBox(l, m, l + 15, m + 15), new ChunkPos(j, k)
									)
							);
					} catch (Exception var19) {
						CrashReport crashReport = CrashReport.create(var19, "Feature placement");
						crashReport.addElement("Feature")
							.add("Id", Registry.STRUCTURE_FEATURE.getId(structureFeature))
							.add("Description", (CrashCallable<String>)(() -> structureFeature.toString()));
						throw new CrashException(crashReport);
					}

					i++;
				}
			}
		}

		for (ConfiguredFeature<?, ?> configuredFeature : (List)this.features.get(step)) {
			chunkRandom.setDecoratorSeed(populationSeed, i, step.ordinal());

			try {
				configuredFeature.generate(serverWorldAccess, structureAccessor, chunkGenerator, chunkRandom, pos);
			} catch (Exception var18) {
				CrashReport crashReport2 = CrashReport.create(var18, "Feature placement");
				crashReport2.addElement("Feature")
					.add("Id", Registry.FEATURE.getId(configuredFeature.feature))
					.add("Config", configuredFeature.config)
					.add("Description", (CrashCallable<String>)(() -> configuredFeature.feature.toString()));
				throw new CrashException(crashReport2);
			}

			i++;
		}
	}

	@Environment(EnvType.CLIENT)
	public int getFogColor() {
		return this.effects.getFogColor();
	}

	@Environment(EnvType.CLIENT)
	public int getGrassColorAt(double x, double z) {
		double d = (double)MathHelper.clamp(this.getTemperature(), 0.0F, 1.0F);
		double e = (double)MathHelper.clamp(this.getRainfall(), 0.0F, 1.0F);
		return GrassColors.getColor(d, e);
	}

	@Environment(EnvType.CLIENT)
	public int getFoliageColor() {
		double d = (double)MathHelper.clamp(this.getTemperature(), 0.0F, 1.0F);
		double e = (double)MathHelper.clamp(this.getRainfall(), 0.0F, 1.0F);
		return FoliageColors.getColor(d, e);
	}

	public void buildSurface(
		Random random, Chunk chunk, int x, int z, int worldHeight, double noise, BlockState defaultBlock, BlockState defaultFluid, int seaLevel, long seed
	) {
		this.surfaceBuilder.initSeed(seed);
		this.surfaceBuilder.generate(random, chunk, this, x, z, worldHeight, noise, defaultBlock, defaultFluid, seaLevel, seed);
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

	public final float getRainfall() {
		return this.downfall;
	}

	@Environment(EnvType.CLIENT)
	public Text getName() {
		return new TranslatableText(this.getTranslationKey());
	}

	public String getTranslationKey() {
		if (this.translationKey == null) {
			this.translationKey = Util.createTranslationKey("biome", Registry.BIOME.getId(this));
		}

		return this.translationKey;
	}

	public final float getScale() {
		return this.scale;
	}

	public final float getTemperature() {
		return this.temperature;
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
	public Optional<MusicSound> method_27343() {
		return this.effects.method_27345();
	}

	public final Biome.Category getCategory() {
		return this.category;
	}

	public ConfiguredSurfaceBuilder<?> getSurfaceBuilder() {
		return this.surfaceBuilder;
	}

	public SurfaceConfig getSurfaceConfig() {
		return this.surfaceBuilder.getConfig();
	}

	public Stream<Biome.MixedNoisePoint> streamNoises() {
		return this.noisePoints.stream();
	}

	@Nullable
	public String getParent() {
		return this.parent;
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

		public static final Codec<Biome.Category> field_24678 = StringIdentifiable.method_28140(Biome.Category::values, Biome.Category::method_28424);
		private static final Map<String, Biome.Category> NAME_MAP = (Map<String, Biome.Category>)Arrays.stream(values())
			.collect(Collectors.toMap(Biome.Category::getName, category -> category));
		private final String name;

		private Category(String name) {
			this.name = name;
		}

		public String getName() {
			return this.name;
		}

		public static Biome.Category method_28424(String string) {
			return (Biome.Category)NAME_MAP.get(string);
		}

		@Override
		public String asString() {
			return this.name;
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
						Codec.FLOAT.fieldOf("temperature").forGetter(mixedNoisePoint -> mixedNoisePoint.temperature),
						Codec.FLOAT.fieldOf("humidity").forGetter(mixedNoisePoint -> mixedNoisePoint.humidity),
						Codec.FLOAT.fieldOf("altitude").forGetter(mixedNoisePoint -> mixedNoisePoint.altitude),
						Codec.FLOAT.fieldOf("weirdness").forGetter(mixedNoisePoint -> mixedNoisePoint.weirdness),
						Codec.FLOAT.fieldOf("offset").forGetter(mixedNoisePoint -> mixedNoisePoint.weight)
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

		public static final Codec<Biome.Precipitation> field_24680 = StringIdentifiable.method_28140(Biome.Precipitation::values, Biome.Precipitation::method_28431);
		private static final Map<String, Biome.Precipitation> NAME_MAP = (Map<String, Biome.Precipitation>)Arrays.stream(values())
			.collect(Collectors.toMap(Biome.Precipitation::getName, precipitation -> precipitation));
		private final String name;

		private Precipitation(String name) {
			this.name = name;
		}

		public String getName() {
			return this.name;
		}

		public static Biome.Precipitation method_28431(String string) {
			return (Biome.Precipitation)NAME_MAP.get(string);
		}

		@Override
		public String asString() {
			return this.name;
		}
	}

	public static class Settings {
		@Nullable
		private ConfiguredSurfaceBuilder<?> surfaceBuilder;
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
		@Nullable
		private Float downfall;
		@Nullable
		private String parent;
		@Nullable
		private List<Biome.MixedNoisePoint> noises;
		@Nullable
		private BiomeEffects specialEffects;

		public <SC extends SurfaceConfig> Biome.Settings configureSurfaceBuilder(SurfaceBuilder<SC> surfaceBuilder, SC config) {
			this.surfaceBuilder = new ConfiguredSurfaceBuilder<>(surfaceBuilder, config);
			return this;
		}

		public Biome.Settings surfaceBuilder(ConfiguredSurfaceBuilder<?> surfaceBuilder) {
			this.surfaceBuilder = surfaceBuilder;
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

		public Biome.Settings noises(List<Biome.MixedNoisePoint> noises) {
			this.noises = noises;
			return this;
		}

		public Biome.Settings effects(BiomeEffects effects) {
			this.specialEffects = effects;
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
				+ ",\ndownfall="
				+ this.downfall
				+ ",\nspecialEffects="
				+ this.specialEffects
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

	public static enum TemperatureGroup {
		OCEAN("ocean"),
		COLD("cold"),
		MEDIUM("medium"),
		WARM("warm");

		private static final Map<String, Biome.TemperatureGroup> NAME_MAP = (Map<String, Biome.TemperatureGroup>)Arrays.stream(values())
			.collect(Collectors.toMap(Biome.TemperatureGroup::getName, temperatureGroup -> temperatureGroup));
		private final String name;

		private TemperatureGroup(String name) {
			this.name = name;
		}

		public String getName() {
			return this.name;
		}
	}
}
