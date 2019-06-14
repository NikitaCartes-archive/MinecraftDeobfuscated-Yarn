package net.minecraft.world.biome;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FluidBlock;
import net.minecraft.client.color.world.FoliageColors;
import net.minecraft.client.color.world.GrassColors;
import net.minecraft.entity.EntityCategory;
import net.minecraft.entity.EntityType;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.IdList;
import net.minecraft.util.SystemUtil;
import net.minecraft.util.WeightedPicker;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.noise.OctaveSimplexNoiseSampler;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.IWorld;
import net.minecraft.world.LightType;
import net.minecraft.world.ViewableWorld;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.ChunkRandom;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.carver.Carver;
import net.minecraft.world.gen.carver.CarverConfig;
import net.minecraft.world.gen.carver.ConfiguredCarver;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;
import net.minecraft.world.gen.decorator.Decorator;
import net.minecraft.world.gen.decorator.DecoratorConfig;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.DecoratedFeatureConfig;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.FeatureConfig;
import net.minecraft.world.gen.feature.FlowerFeature;
import net.minecraft.world.gen.feature.StructureFeature;
import net.minecraft.world.gen.surfacebuilder.ConfiguredSurfaceBuilder;
import net.minecraft.world.gen.surfacebuilder.SurfaceBuilder;
import net.minecraft.world.gen.surfacebuilder.SurfaceConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class Biome {
	public static final Logger LOGGER = LogManager.getLogger();
	public static final Set<Biome> BIOMES = Sets.<Biome>newHashSet();
	public static final IdList<Biome> PARENT_BIOME_ID_MAP = new IdList<>();
	protected static final OctaveSimplexNoiseSampler field_9335 = new OctaveSimplexNoiseSampler(new Random(1234L), 1);
	public static final OctaveSimplexNoiseSampler field_9324 = new OctaveSimplexNoiseSampler(new Random(2345L), 1);
	@Nullable
	protected String translationKey;
	protected final float depth;
	protected final float scale;
	protected final float temperature;
	protected final float downfall;
	protected final int waterColor;
	protected final int waterFogColor;
	@Nullable
	protected final String parent;
	protected final ConfiguredSurfaceBuilder<?> field_9336;
	protected final Biome.Category category;
	protected final Biome.Precipitation precipitation;
	protected final Map<GenerationStep.Carver, List<ConfiguredCarver<?>>> carvers = Maps.<GenerationStep.Carver, List<ConfiguredCarver<?>>>newHashMap();
	protected final Map<GenerationStep.Feature, List<ConfiguredFeature<?>>> features = Maps.<GenerationStep.Feature, List<ConfiguredFeature<?>>>newHashMap();
	protected final List<ConfiguredFeature<?>> flowerFeatures = Lists.<ConfiguredFeature<?>>newArrayList();
	protected final Map<StructureFeature<?>, FeatureConfig> structureFeatures = Maps.<StructureFeature<?>, FeatureConfig>newHashMap();
	private final Map<EntityCategory, List<Biome.SpawnEntry>> spawns = Maps.<EntityCategory, List<Biome.SpawnEntry>>newHashMap();

	@Nullable
	public static Biome getParentBiome(Biome biome) {
		return PARENT_BIOME_ID_MAP.get(Registry.BIOME.getRawId(biome));
	}

	public static <C extends CarverConfig> ConfiguredCarver<C> method_8714(Carver<C> carver, C carverConfig) {
		return new ConfiguredCarver<>(carver, carverConfig);
	}

	public static <F extends FeatureConfig, D extends DecoratorConfig> ConfiguredFeature<?> method_8699(
		Feature<F> feature, F featureConfig, Decorator<D> decorator, D decoratorConfig
	) {
		Feature<DecoratedFeatureConfig> feature2 = feature instanceof FlowerFeature ? Feature.field_13561 : Feature.field_13572;
		return new ConfiguredFeature<>(feature2, new DecoratedFeatureConfig(feature, featureConfig, decorator, decoratorConfig));
	}

	protected Biome(Biome.Settings settings) {
		if (settings.field_9353 != null
			&& settings.precipitation != null
			&& settings.category != null
			&& settings.depth != null
			&& settings.scale != null
			&& settings.temperature != null
			&& settings.downfall != null
			&& settings.waterColor != null
			&& settings.waterFogColor != null) {
			this.field_9336 = settings.field_9353;
			this.precipitation = settings.precipitation;
			this.category = settings.category;
			this.depth = settings.depth;
			this.scale = settings.scale;
			this.temperature = settings.temperature;
			this.downfall = settings.downfall;
			this.waterColor = settings.waterColor;
			this.waterFogColor = settings.waterFogColor;
			this.parent = settings.parent;

			for (GenerationStep.Feature feature : GenerationStep.Feature.values()) {
				this.features.put(feature, Lists.newArrayList());
			}

			for (EntityCategory entityCategory : EntityCategory.values()) {
				this.spawns.put(entityCategory, Lists.newArrayList());
			}
		} else {
			throw new IllegalStateException("You are missing parameters to build a proper biome for " + this.getClass().getSimpleName() + "\n" + settings);
		}
	}

	public boolean hasParent() {
		return this.parent != null;
	}

	@Environment(EnvType.CLIENT)
	public int getSkyColor(float f) {
		f /= 3.0F;
		f = MathHelper.clamp(f, -1.0F, 1.0F);
		return MathHelper.hsvToRgb(0.62222224F - f * 0.05F, 0.5F + f * 0.1F, 1.0F);
	}

	protected void addSpawn(EntityCategory entityCategory, Biome.SpawnEntry spawnEntry) {
		((List)this.spawns.get(entityCategory)).add(spawnEntry);
	}

	public List<Biome.SpawnEntry> getEntitySpawnList(EntityCategory entityCategory) {
		return (List<Biome.SpawnEntry>)this.spawns.get(entityCategory);
	}

	public Biome.Precipitation getPrecipitation() {
		return this.precipitation;
	}

	public boolean hasHighHumidity() {
		return this.getRainfall() > 0.85F;
	}

	public float getMaxSpawnLimit() {
		return 0.1F;
	}

	public float getTemperature(BlockPos blockPos) {
		if (blockPos.getY() > 64) {
			float f = (float)(field_9335.sample((double)((float)blockPos.getX() / 8.0F), (double)((float)blockPos.getZ() / 8.0F)) * 4.0);
			return this.getTemperature() - (f + (float)blockPos.getY() - 64.0F) * 0.05F / 30.0F;
		} else {
			return this.getTemperature();
		}
	}

	public boolean canSetSnow(ViewableWorld viewableWorld, BlockPos blockPos) {
		return this.canSetSnow(viewableWorld, blockPos, true);
	}

	public boolean canSetSnow(ViewableWorld viewableWorld, BlockPos blockPos, boolean bl) {
		if (this.getTemperature(blockPos) >= 0.15F) {
			return false;
		} else {
			if (blockPos.getY() >= 0 && blockPos.getY() < 256 && viewableWorld.method_8314(LightType.field_9282, blockPos) < 10) {
				BlockState blockState = viewableWorld.method_8320(blockPos);
				FluidState fluidState = viewableWorld.method_8316(blockPos);
				if (fluidState.getFluid() == Fluids.WATER && blockState.getBlock() instanceof FluidBlock) {
					if (!bl) {
						return true;
					}

					boolean bl2 = viewableWorld.isWaterAt(blockPos.west())
						&& viewableWorld.isWaterAt(blockPos.east())
						&& viewableWorld.isWaterAt(blockPos.north())
						&& viewableWorld.isWaterAt(blockPos.south());
					if (!bl2) {
						return true;
					}
				}
			}

			return false;
		}
	}

	public boolean canSetIce(ViewableWorld viewableWorld, BlockPos blockPos) {
		if (this.getTemperature(blockPos) >= 0.15F) {
			return false;
		} else {
			if (blockPos.getY() >= 0 && blockPos.getY() < 256 && viewableWorld.method_8314(LightType.field_9282, blockPos) < 10) {
				BlockState blockState = viewableWorld.method_8320(blockPos);
				if (blockState.isAir() && Blocks.field_10477.method_9564().canPlaceAt(viewableWorld, blockPos)) {
					return true;
				}
			}

			return false;
		}
	}

	public void method_8719(GenerationStep.Feature feature, ConfiguredFeature<?> configuredFeature) {
		if (configuredFeature.field_13376 == Feature.field_13561) {
			this.flowerFeatures.add(configuredFeature);
		}

		((List)this.features.get(feature)).add(configuredFeature);
	}

	public <C extends CarverConfig> void method_8691(GenerationStep.Carver carver, ConfiguredCarver<C> configuredCarver) {
		((List)this.carvers.computeIfAbsent(carver, carverx -> Lists.newArrayList())).add(configuredCarver);
	}

	public List<ConfiguredCarver<?>> method_8717(GenerationStep.Carver carver) {
		return (List<ConfiguredCarver<?>>)this.carvers.computeIfAbsent(carver, carverx -> Lists.newArrayList());
	}

	public <C extends FeatureConfig> void method_8710(StructureFeature<C> structureFeature, C featureConfig) {
		this.structureFeatures.put(structureFeature, featureConfig);
	}

	public <C extends FeatureConfig> boolean method_8684(StructureFeature<C> structureFeature) {
		return this.structureFeatures.containsKey(structureFeature);
	}

	@Nullable
	public <C extends FeatureConfig> C method_8706(StructureFeature<C> structureFeature) {
		return (C)this.structureFeatures.get(structureFeature);
	}

	public List<ConfiguredFeature<?>> getFlowerFeatures() {
		return this.flowerFeatures;
	}

	public List<ConfiguredFeature<?>> method_8721(GenerationStep.Feature feature) {
		return (List<ConfiguredFeature<?>>)this.features.get(feature);
	}

	public void method_8702(
		GenerationStep.Feature feature,
		ChunkGenerator<? extends ChunkGeneratorConfig> chunkGenerator,
		IWorld iWorld,
		long l,
		ChunkRandom chunkRandom,
		BlockPos blockPos
	) {
		int i = 0;

		for (ConfiguredFeature<?> configuredFeature : (List)this.features.get(feature)) {
			chunkRandom.setFeatureSeed(l, i, feature.ordinal());
			configuredFeature.generate(iWorld, chunkGenerator, chunkRandom, blockPos);
			i++;
		}
	}

	@Environment(EnvType.CLIENT)
	public int getGrassColorAt(BlockPos blockPos) {
		double d = (double)MathHelper.clamp(this.getTemperature(blockPos), 0.0F, 1.0F);
		double e = (double)MathHelper.clamp(this.getRainfall(), 0.0F, 1.0F);
		return GrassColors.getColor(d, e);
	}

	@Environment(EnvType.CLIENT)
	public int getFoliageColorAt(BlockPos blockPos) {
		double d = (double)MathHelper.clamp(this.getTemperature(blockPos), 0.0F, 1.0F);
		double e = (double)MathHelper.clamp(this.getRainfall(), 0.0F, 1.0F);
		return FoliageColors.getColor(d, e);
	}

	public void method_8703(Random random, Chunk chunk, int i, int j, int k, double d, BlockState blockState, BlockState blockState2, int l, long m) {
		this.field_9336.initSeed(m);
		this.field_9336.generate(random, chunk, this, i, j, k, d, blockState, blockState2, l, m);
	}

	public Biome.TemperatureGroup getTemperatureGroup() {
		if (this.category == Biome.Category.field_9367) {
			return Biome.TemperatureGroup.field_9379;
		} else if ((double)this.getTemperature() < 0.2) {
			return Biome.TemperatureGroup.field_9377;
		} else {
			return (double)this.getTemperature() < 1.0 ? Biome.TemperatureGroup.field_9375 : Biome.TemperatureGroup.field_9378;
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
			this.translationKey = SystemUtil.createTranslationKey("biome", Registry.BIOME.getId(this));
		}

		return this.translationKey;
	}

	public final float getScale() {
		return this.scale;
	}

	public final float getTemperature() {
		return this.temperature;
	}

	public final int getWaterColor() {
		return this.waterColor;
	}

	public final int getWaterFogColor() {
		return this.waterFogColor;
	}

	public final Biome.Category getCategory() {
		return this.category;
	}

	public ConfiguredSurfaceBuilder<?> method_8692() {
		return this.field_9336;
	}

	public SurfaceConfig method_8722() {
		return this.field_9336.method_15197();
	}

	@Nullable
	public String getParent() {
		return this.parent;
	}

	public static enum Category {
		field_9371("none"),
		field_9361("taiga"),
		field_9357("extreme_hills"),
		field_9358("jungle"),
		field_9354("mesa"),
		field_9355("plains"),
		field_9356("savanna"),
		field_9362("icy"),
		THEEND("the_end"),
		field_9363("beach"),
		field_9370("forest"),
		field_9367("ocean"),
		field_9368("desert"),
		field_9369("river"),
		field_9364("swamp"),
		field_9365("mushroom"),
		field_9366("nether");

		private static final Map<String, Biome.Category> NAME_MAP = (Map<String, Biome.Category>)Arrays.stream(values())
			.collect(Collectors.toMap(Biome.Category::getName, category -> category));
		private final String name;

		private Category(String string2) {
			this.name = string2;
		}

		public String getName() {
			return this.name;
		}
	}

	public static enum Precipitation {
		NONE("none"),
		RAIN("rain"),
		SNOW("snow");

		private static final Map<String, Biome.Precipitation> NAME_MAP = (Map<String, Biome.Precipitation>)Arrays.stream(values())
			.collect(Collectors.toMap(Biome.Precipitation::getName, precipitation -> precipitation));
		private final String name;

		private Precipitation(String string2) {
			this.name = string2;
		}

		public String getName() {
			return this.name;
		}
	}

	public static class Settings {
		@Nullable
		private ConfiguredSurfaceBuilder<?> field_9353;
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
		private Integer waterColor;
		@Nullable
		private Integer waterFogColor;
		@Nullable
		private String parent;

		public <SC extends SurfaceConfig> Biome.Settings method_8737(SurfaceBuilder<SC> surfaceBuilder, SC surfaceConfig) {
			this.field_9353 = new ConfiguredSurfaceBuilder<>(surfaceBuilder, surfaceConfig);
			return this;
		}

		public Biome.Settings method_8731(ConfiguredSurfaceBuilder<?> configuredSurfaceBuilder) {
			this.field_9353 = configuredSurfaceBuilder;
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

		public Biome.Settings depth(float f) {
			this.depth = f;
			return this;
		}

		public Biome.Settings scale(float f) {
			this.scale = f;
			return this;
		}

		public Biome.Settings temperature(float f) {
			this.temperature = f;
			return this;
		}

		public Biome.Settings downfall(float f) {
			this.downfall = f;
			return this;
		}

		public Biome.Settings waterColor(int i) {
			this.waterColor = i;
			return this;
		}

		public Biome.Settings waterFogColor(int i) {
			this.waterFogColor = i;
			return this;
		}

		public Biome.Settings parent(@Nullable String string) {
			this.parent = string;
			return this;
		}

		public String toString() {
			return "BiomeBuilder{\nsurfaceBuilder="
				+ this.field_9353
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
				+ ",\nwaterColor="
				+ this.waterColor
				+ ",\nwaterFogColor="
				+ this.waterFogColor
				+ ",\nparent='"
				+ this.parent
				+ '\''
				+ "\n"
				+ '}';
		}
	}

	public static class SpawnEntry extends WeightedPicker.Entry {
		public final EntityType<?> type;
		public final int minGroupSize;
		public final int maxGroupSize;

		public SpawnEntry(EntityType<?> entityType, int i, int j, int k) {
			super(i);
			this.type = entityType;
			this.minGroupSize = j;
			this.maxGroupSize = k;
		}

		public String toString() {
			return EntityType.getId(this.type) + "*(" + this.minGroupSize + "-" + this.maxGroupSize + "):" + this.weight;
		}
	}

	public static enum TemperatureGroup {
		field_9379("ocean"),
		field_9377("cold"),
		field_9375("medium"),
		field_9378("warm");

		private static final Map<String, Biome.TemperatureGroup> NAME_MAP = (Map<String, Biome.TemperatureGroup>)Arrays.stream(values())
			.collect(Collectors.toMap(Biome.TemperatureGroup::getName, temperatureGroup -> temperatureGroup));
		private final String name;

		private TemperatureGroup(String string2) {
			this.name = string2;
		}

		public String getName() {
			return this.name;
		}
	}
}
