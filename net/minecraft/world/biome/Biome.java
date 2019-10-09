/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.biome;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import it.unimi.dsi.fastutil.longs.Long2FloatLinkedOpenHashMap;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;
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
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.noise.OctaveSimplexNoiseSampler;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.IWorld;
import net.minecraft.world.LightType;
import net.minecraft.world.WorldView;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.ChunkRandom;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.carver.Carver;
import net.minecraft.world.gen.carver.CarverConfig;
import net.minecraft.world.gen.carver.ConfiguredCarver;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.FeatureConfig;
import net.minecraft.world.gen.feature.StructureFeature;
import net.minecraft.world.gen.surfacebuilder.ConfiguredSurfaceBuilder;
import net.minecraft.world.gen.surfacebuilder.SurfaceBuilder;
import net.minecraft.world.gen.surfacebuilder.SurfaceConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

public abstract class Biome {
    public static final Logger LOGGER = LogManager.getLogger();
    public static final Set<Biome> BIOMES = Sets.newHashSet();
    public static final IdList<Biome> PARENT_BIOME_ID_MAP = new IdList();
    protected static final OctaveSimplexNoiseSampler TEMPERATURE_NOISE = new OctaveSimplexNoiseSampler(new ChunkRandom(1234L), 0, 0);
    public static final OctaveSimplexNoiseSampler FOLIAGE_NOISE = new OctaveSimplexNoiseSampler(new ChunkRandom(2345L), 0, 0);
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
    protected final ConfiguredSurfaceBuilder<?> surfaceBuilder;
    protected final Category category;
    protected final Precipitation precipitation;
    protected final Map<GenerationStep.Carver, List<ConfiguredCarver<?>>> carvers = Maps.newHashMap();
    protected final Map<GenerationStep.Feature, List<ConfiguredFeature<?, ?>>> features = Maps.newHashMap();
    protected final List<ConfiguredFeature<?, ?>> flowerFeatures = Lists.newArrayList();
    protected final Map<StructureFeature<?>, FeatureConfig> structureFeatures = Maps.newHashMap();
    private final Map<EntityCategory, List<SpawnEntry>> spawns = Maps.newHashMap();
    private final ThreadLocal<Long2FloatLinkedOpenHashMap> temperatureCache = ThreadLocal.withInitial(() -> SystemUtil.get(() -> {
        Long2FloatLinkedOpenHashMap long2FloatLinkedOpenHashMap = new Long2FloatLinkedOpenHashMap(1024, 0.25f){

            @Override
            protected void rehash(int i) {
            }
        };
        long2FloatLinkedOpenHashMap.defaultReturnValue(Float.NaN);
        return long2FloatLinkedOpenHashMap;
    }));

    @Nullable
    public static Biome getParentBiome(Biome biome) {
        return PARENT_BIOME_ID_MAP.get(Registry.BIOME.getRawId(biome));
    }

    public static <C extends CarverConfig> ConfiguredCarver<C> configureCarver(Carver<C> carver, C carverConfig) {
        return new ConfiguredCarver<C>(carver, carverConfig);
    }

    protected Biome(Settings settings) {
        if (settings.surfaceBuilder == null || settings.precipitation == null || settings.category == null || settings.depth == null || settings.scale == null || settings.temperature == null || settings.downfall == null || settings.waterColor == null || settings.waterFogColor == null) {
            throw new IllegalStateException("You are missing parameters to build a proper biome for " + this.getClass().getSimpleName() + "\n" + settings);
        }
        this.surfaceBuilder = settings.surfaceBuilder;
        this.precipitation = settings.precipitation;
        this.category = settings.category;
        this.depth = settings.depth.floatValue();
        this.scale = settings.scale.floatValue();
        this.temperature = settings.temperature.floatValue();
        this.downfall = settings.downfall.floatValue();
        this.waterColor = settings.waterColor;
        this.waterFogColor = settings.waterFogColor;
        this.parent = settings.parent;
        for (GenerationStep.Feature feature : GenerationStep.Feature.values()) {
            this.features.put(feature, Lists.newArrayList());
        }
        for (Enum enum_ : EntityCategory.values()) {
            this.spawns.put((EntityCategory)enum_, Lists.newArrayList());
        }
    }

    public boolean hasParent() {
        return this.parent != null;
    }

    @Environment(value=EnvType.CLIENT)
    public int getSkyColor(float f) {
        f /= 3.0f;
        f = MathHelper.clamp(f, -1.0f, 1.0f);
        return MathHelper.hsvToRgb(0.62222224f - f * 0.05f, 0.5f + f * 0.1f, 1.0f);
    }

    protected void addSpawn(EntityCategory entityCategory, SpawnEntry spawnEntry) {
        this.spawns.get((Object)entityCategory).add(spawnEntry);
    }

    public List<SpawnEntry> getEntitySpawnList(EntityCategory entityCategory) {
        return this.spawns.get((Object)entityCategory);
    }

    public Precipitation getPrecipitation() {
        return this.precipitation;
    }

    public boolean hasHighHumidity() {
        return this.getRainfall() > 0.85f;
    }

    public float getMaxSpawnLimit() {
        return 0.1f;
    }

    protected float computeTemperature(BlockPos blockPos) {
        if (blockPos.getY() > 64) {
            float f = (float)(TEMPERATURE_NOISE.sample((float)blockPos.getX() / 8.0f, (float)blockPos.getZ() / 8.0f, false) * 4.0);
            return this.getTemperature() - (f + (float)blockPos.getY() - 64.0f) * 0.05f / 30.0f;
        }
        return this.getTemperature();
    }

    public final float getTemperature(BlockPos blockPos) {
        long l = blockPos.asLong();
        Long2FloatLinkedOpenHashMap long2FloatLinkedOpenHashMap = this.temperatureCache.get();
        float f = long2FloatLinkedOpenHashMap.get(l);
        if (!Float.isNaN(f)) {
            return f;
        }
        float g = this.computeTemperature(blockPos);
        if (long2FloatLinkedOpenHashMap.size() == 1024) {
            long2FloatLinkedOpenHashMap.removeFirstFloat();
        }
        long2FloatLinkedOpenHashMap.put(l, g);
        return g;
    }

    public boolean canSetSnow(WorldView worldView, BlockPos blockPos) {
        return this.canSetSnow(worldView, blockPos, true);
    }

    public boolean canSetSnow(WorldView worldView, BlockPos blockPos, boolean bl) {
        if (this.getTemperature(blockPos) >= 0.15f) {
            return false;
        }
        if (blockPos.getY() >= 0 && blockPos.getY() < 256 && worldView.getLightLevel(LightType.BLOCK, blockPos) < 10) {
            BlockState blockState = worldView.getBlockState(blockPos);
            FluidState fluidState = worldView.getFluidState(blockPos);
            if (fluidState.getFluid() == Fluids.WATER && blockState.getBlock() instanceof FluidBlock) {
                boolean bl2;
                if (!bl) {
                    return true;
                }
                boolean bl3 = bl2 = worldView.isWater(blockPos.west()) && worldView.isWater(blockPos.east()) && worldView.isWater(blockPos.north()) && worldView.isWater(blockPos.south());
                if (!bl2) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean canSetIce(WorldView worldView, BlockPos blockPos) {
        BlockState blockState;
        if (this.getTemperature(blockPos) >= 0.15f) {
            return false;
        }
        return blockPos.getY() >= 0 && blockPos.getY() < 256 && worldView.getLightLevel(LightType.BLOCK, blockPos) < 10 && (blockState = worldView.getBlockState(blockPos)).isAir() && Blocks.SNOW.getDefaultState().canPlaceAt(worldView, blockPos);
    }

    public void addFeature(GenerationStep.Feature feature, ConfiguredFeature<?, ?> configuredFeature) {
        if (configuredFeature.feature == Feature.DECORATED_FLOWER) {
            this.flowerFeatures.add(configuredFeature);
        }
        this.features.get((Object)feature).add(configuredFeature);
    }

    public <C extends CarverConfig> void addCarver(GenerationStep.Carver carver2, ConfiguredCarver<C> configuredCarver) {
        this.carvers.computeIfAbsent(carver2, carver -> Lists.newArrayList()).add(configuredCarver);
    }

    public List<ConfiguredCarver<?>> getCarversForStep(GenerationStep.Carver carver2) {
        return this.carvers.computeIfAbsent(carver2, carver -> Lists.newArrayList());
    }

    public <C extends FeatureConfig> void addStructureFeature(ConfiguredFeature<C, ? extends StructureFeature<C>> configuredFeature) {
        this.structureFeatures.put((StructureFeature<?>)configuredFeature.feature, (FeatureConfig)configuredFeature.config);
    }

    public <C extends FeatureConfig> boolean hasStructureFeature(StructureFeature<C> structureFeature) {
        return this.structureFeatures.containsKey(structureFeature);
    }

    @Nullable
    public <C extends FeatureConfig> C getStructureFeatureConfig(StructureFeature<C> structureFeature) {
        return (C)this.structureFeatures.get(structureFeature);
    }

    public List<ConfiguredFeature<?, ?>> getFlowerFeatures() {
        return this.flowerFeatures;
    }

    public List<ConfiguredFeature<?, ?>> getFeaturesForStep(GenerationStep.Feature feature) {
        return this.features.get((Object)feature);
    }

    public void generateFeatureStep(GenerationStep.Feature feature, ChunkGenerator<? extends ChunkGeneratorConfig> chunkGenerator, IWorld iWorld, long l, ChunkRandom chunkRandom, BlockPos blockPos) {
        int i = 0;
        for (ConfiguredFeature<?, ?> configuredFeature : this.features.get((Object)feature)) {
            chunkRandom.setFeatureSeed(l, i, feature.ordinal());
            try {
                configuredFeature.generate(iWorld, chunkGenerator, chunkRandom, blockPos);
            } catch (Exception exception) {
                CrashReport crashReport = CrashReport.create(exception, "Feature placement");
                crashReport.addElement("Feature").add("Id", Registry.FEATURE.getId((Feature<?>)configuredFeature.feature)).add("Description", () -> configuredFeature.feature.toString());
                throw new CrashException(crashReport);
            }
            ++i;
        }
    }

    @Environment(value=EnvType.CLIENT)
    public int getGrassColorAt(BlockPos blockPos) {
        double d = MathHelper.clamp(this.getTemperature(blockPos), 0.0f, 1.0f);
        double e = MathHelper.clamp(this.getRainfall(), 0.0f, 1.0f);
        return GrassColors.getColor(d, e);
    }

    @Environment(value=EnvType.CLIENT)
    public int getFoliageColorAt(BlockPos blockPos) {
        double d = MathHelper.clamp(this.getTemperature(blockPos), 0.0f, 1.0f);
        double e = MathHelper.clamp(this.getRainfall(), 0.0f, 1.0f);
        return FoliageColors.getColor(d, e);
    }

    public void buildSurface(Random random, Chunk chunk, int i, int j, int k, double d, BlockState blockState, BlockState blockState2, int l, long m) {
        this.surfaceBuilder.initSeed(m);
        this.surfaceBuilder.generate(random, chunk, this, i, j, k, d, blockState, blockState2, l, m);
    }

    public TemperatureGroup getTemperatureGroup() {
        if (this.category == Category.OCEAN) {
            return TemperatureGroup.OCEAN;
        }
        if ((double)this.getTemperature() < 0.2) {
            return TemperatureGroup.COLD;
        }
        if ((double)this.getTemperature() < 1.0) {
            return TemperatureGroup.MEDIUM;
        }
        return TemperatureGroup.WARM;
    }

    public final float getDepth() {
        return this.depth;
    }

    public final float getRainfall() {
        return this.downfall;
    }

    @Environment(value=EnvType.CLIENT)
    public Text getName() {
        return new TranslatableText(this.getTranslationKey(), new Object[0]);
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

    public final Category getCategory() {
        return this.category;
    }

    public ConfiguredSurfaceBuilder<?> getSurfaceBuilder() {
        return this.surfaceBuilder;
    }

    public SurfaceConfig getSurfaceConfig() {
        return this.surfaceBuilder.getConfig();
    }

    @Nullable
    public String getParent() {
        return this.parent;
    }

    public static class Settings {
        @Nullable
        private ConfiguredSurfaceBuilder<?> surfaceBuilder;
        @Nullable
        private Precipitation precipitation;
        @Nullable
        private Category category;
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

        public <SC extends SurfaceConfig> Settings configureSurfaceBuilder(SurfaceBuilder<SC> surfaceBuilder, SC surfaceConfig) {
            this.surfaceBuilder = new ConfiguredSurfaceBuilder<SC>(surfaceBuilder, surfaceConfig);
            return this;
        }

        public Settings surfaceBuilder(ConfiguredSurfaceBuilder<?> configuredSurfaceBuilder) {
            this.surfaceBuilder = configuredSurfaceBuilder;
            return this;
        }

        public Settings precipitation(Precipitation precipitation) {
            this.precipitation = precipitation;
            return this;
        }

        public Settings category(Category category) {
            this.category = category;
            return this;
        }

        public Settings depth(float f) {
            this.depth = Float.valueOf(f);
            return this;
        }

        public Settings scale(float f) {
            this.scale = Float.valueOf(f);
            return this;
        }

        public Settings temperature(float f) {
            this.temperature = Float.valueOf(f);
            return this;
        }

        public Settings downfall(float f) {
            this.downfall = Float.valueOf(f);
            return this;
        }

        public Settings waterColor(int i) {
            this.waterColor = i;
            return this;
        }

        public Settings waterFogColor(int i) {
            this.waterFogColor = i;
            return this;
        }

        public Settings parent(@Nullable String string) {
            this.parent = string;
            return this;
        }

        public String toString() {
            return "BiomeBuilder{\nsurfaceBuilder=" + this.surfaceBuilder + ",\nprecipitation=" + (Object)((Object)this.precipitation) + ",\nbiomeCategory=" + (Object)((Object)this.category) + ",\ndepth=" + this.depth + ",\nscale=" + this.scale + ",\ntemperature=" + this.temperature + ",\ndownfall=" + this.downfall + ",\nwaterColor=" + this.waterColor + ",\nwaterFogColor=" + this.waterFogColor + ",\nparent='" + this.parent + '\'' + "\n" + '}';
        }
    }

    public static class SpawnEntry
    extends WeightedPicker.Entry {
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

    public static enum Precipitation {
        NONE("none"),
        RAIN("rain"),
        SNOW("snow");

        private static final Map<String, Precipitation> NAME_MAP;
        private final String name;

        private Precipitation(String string2) {
            this.name = string2;
        }

        public String getName() {
            return this.name;
        }

        static {
            NAME_MAP = Arrays.stream(Precipitation.values()).collect(Collectors.toMap(Precipitation::getName, precipitation -> precipitation));
        }
    }

    public static enum Category {
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

        private static final Map<String, Category> NAME_MAP;
        private final String name;

        private Category(String string2) {
            this.name = string2;
        }

        public String getName() {
            return this.name;
        }

        static {
            NAME_MAP = Arrays.stream(Category.values()).collect(Collectors.toMap(Category::getName, category -> category));
        }
    }

    public static enum TemperatureGroup {
        OCEAN("ocean"),
        COLD("cold"),
        MEDIUM("medium"),
        WARM("warm");

        private static final Map<String, TemperatureGroup> NAME_MAP;
        private final String name;

        private TemperatureGroup(String string2) {
            this.name = string2;
        }

        public String getName() {
            return this.name;
        }

        static {
            NAME_MAP = Arrays.stream(TemperatureGroup.values()).collect(Collectors.toMap(TemperatureGroup::getName, temperatureGroup -> temperatureGroup));
        }
    }
}

