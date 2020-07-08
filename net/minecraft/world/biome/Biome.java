/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.biome;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.kinds.Applicative;
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
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;
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
import net.minecraft.world.biome.BiomeEffects;
import net.minecraft.world.biome.BiomeParticleConfig;
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
import org.jetbrains.annotations.Nullable;

public class Biome {
    public static final Logger LOGGER = LogManager.getLogger();
    public static final MapCodec<Biome> field_25819 = RecordCodecBuilder.mapCodec(instance -> instance.group(((MapCodec)Precipitation.field_24680.fieldOf("precipitation")).forGetter(biome -> biome.precipitation), ((MapCodec)Category.field_24678.fieldOf("category")).forGetter(biome -> biome.category), ((MapCodec)Codec.FLOAT.fieldOf("depth")).forGetter(biome -> Float.valueOf(biome.depth)), ((MapCodec)Codec.FLOAT.fieldOf("scale")).forGetter(biome -> Float.valueOf(biome.scale)), ((MapCodec)Codec.FLOAT.fieldOf("temperature")).forGetter(biome -> Float.valueOf(biome.temperature)), ((MapCodec)Codec.FLOAT.fieldOf("downfall")).forGetter(biome -> Float.valueOf(biome.downfall)), ((MapCodec)BiomeEffects.CODEC.fieldOf("effects")).forGetter(biome -> biome.effects), ((MapCodec)Codec.INT.fieldOf("sky_color")).forGetter(biome -> biome.skyColor), ((MapCodec)ConfiguredSurfaceBuilder.field_25015.fieldOf("surface_builder")).forGetter(biome -> biome.surfaceBuilder), Codec.simpleMap(GenerationStep.Carver.field_24770, ConfiguredCarver.field_24828.listOf().promotePartial((Consumer)Util.method_29188("Carver: ", LOGGER::error)), StringIdentifiable.method_28142(GenerationStep.Carver.values())).fieldOf("carvers").forGetter(biome -> biome.carvers), ((MapCodec)ConfiguredFeature.CODEC.listOf().promotePartial((Consumer)Util.method_29188("Feature: ", LOGGER::error)).listOf().fieldOf("features")).forGetter(biome -> biome.features), ((MapCodec)ConfiguredStructureFeature.TYPE_CODEC.listOf().promotePartial((Consumer)Util.method_29188("Structure start: ", LOGGER::error)).fieldOf("starts")).forGetter(biome -> biome.structureFeatures), Codec.simpleMap(SpawnGroup.field_24655, SpawnEntry.CODEC.listOf().promotePartial((Consumer)Util.method_29188("Spawn data: ", LOGGER::error)), StringIdentifiable.method_28142(SpawnGroup.values())).fieldOf("spawners").forGetter(biome -> biome.spawns), Codec.STRING.optionalFieldOf("parent").forGetter(biome -> Optional.ofNullable(biome.parent)), Codec.simpleMap(Registry.ENTITY_TYPE, SpawnDensity.field_25820, Registry.ENTITY_TYPE).fieldOf("spawn_costs").forGetter(biome -> biome.spawnDensities)).apply((Applicative<Biome, ?>)instance, Biome::new));
    public static final Codec<Supplier<Biome>> field_24677 = RegistryElementCodec.of(Registry.BIOME_KEY, field_25819);
    public static final Set<Biome> BIOMES = Sets.newHashSet();
    protected static final OctaveSimplexNoiseSampler TEMPERATURE_NOISE = new OctaveSimplexNoiseSampler(new ChunkRandom(1234L), ImmutableList.of(Integer.valueOf(0)));
    public static final OctaveSimplexNoiseSampler FOLIAGE_NOISE = new OctaveSimplexNoiseSampler(new ChunkRandom(2345L), ImmutableList.of(Integer.valueOf(0)));
    private final float depth;
    private final float scale;
    private final float temperature;
    private final float downfall;
    private final int skyColor;
    @Nullable
    protected final String parent;
    private final Supplier<ConfiguredSurfaceBuilder<?>> surfaceBuilder;
    private final Category category;
    private final Precipitation precipitation;
    private final BiomeEffects effects;
    private final Map<GenerationStep.Carver, List<Supplier<ConfiguredCarver<?>>>> carvers;
    private final List<List<Supplier<ConfiguredFeature<?, ?>>>> features;
    private final List<ConfiguredFeature<?, ?>> flowerFeatures;
    private final List<Supplier<ConfiguredStructureFeature<?, ?>>> structureFeatures;
    private final Map<SpawnGroup, List<SpawnEntry>> spawns;
    private final Map<EntityType<?>, SpawnDensity> spawnDensities;
    private final ThreadLocal<Long2FloatLinkedOpenHashMap> temperatureCache = ThreadLocal.withInitial(() -> Util.make(() -> {
        Long2FloatLinkedOpenHashMap long2FloatLinkedOpenHashMap = new Long2FloatLinkedOpenHashMap(1024, 0.25f){

            @Override
            protected void rehash(int i) {
            }
        };
        long2FloatLinkedOpenHashMap.defaultReturnValue(Float.NaN);
        return long2FloatLinkedOpenHashMap;
    }));

    protected Biome(Settings settings) {
        if (settings.surfaceBuilder == null || settings.precipitation == null || settings.category == null || settings.depth == null || settings.scale == null || settings.temperature == null || settings.downfall == null || settings.specialEffects == null) {
            throw new IllegalStateException("You are missing parameters to build a proper biome for " + this.getClass().getSimpleName() + "\n" + settings);
        }
        this.surfaceBuilder = settings.surfaceBuilder;
        this.precipitation = settings.precipitation;
        this.category = settings.category;
        this.depth = settings.depth.floatValue();
        this.scale = settings.scale.floatValue();
        this.temperature = settings.temperature.floatValue();
        this.downfall = settings.downfall.floatValue();
        this.skyColor = this.calculateSkyColor();
        this.parent = settings.parent;
        this.effects = settings.specialEffects;
        this.carvers = Maps.newHashMap();
        this.structureFeatures = Lists.newArrayList();
        this.features = Lists.newArrayList();
        this.spawns = Maps.newHashMap();
        for (SpawnGroup spawnGroup : SpawnGroup.values()) {
            this.spawns.put(spawnGroup, Lists.newArrayList());
        }
        this.spawnDensities = Maps.newHashMap();
        this.flowerFeatures = Lists.newArrayList();
    }

    private Biome(Precipitation precipitation, Category category, float f, float g, float h, float i, BiomeEffects biomeEffects, int j, Supplier<ConfiguredSurfaceBuilder<?>> supplier, Map<GenerationStep.Carver, List<Supplier<ConfiguredCarver<?>>>> map, List<List<Supplier<ConfiguredFeature<?, ?>>>> list, List<Supplier<ConfiguredStructureFeature<?, ?>>> list2, Map<SpawnGroup, List<SpawnEntry>> map2, Optional<String> optional, Map<EntityType<?>, SpawnDensity> map3) {
        this.precipitation = precipitation;
        this.category = category;
        this.depth = f;
        this.scale = g;
        this.temperature = h;
        this.downfall = i;
        this.effects = biomeEffects;
        this.skyColor = j;
        this.surfaceBuilder = supplier;
        this.carvers = map;
        this.features = list;
        this.structureFeatures = list2;
        this.spawns = map2;
        this.parent = optional.orElse(null);
        this.spawnDensities = map3;
        this.flowerFeatures = list.stream().flatMap(Collection::stream).map(Supplier::get).filter(configuredFeature -> configuredFeature.feature == Feature.DECORATED_FLOWER).collect(Collectors.toList());
    }

    public boolean hasParent() {
        return this.parent != null;
    }

    private int calculateSkyColor() {
        float f = this.temperature;
        f /= 3.0f;
        f = MathHelper.clamp(f, -1.0f, 1.0f);
        return MathHelper.hsvToRgb(0.62222224f - f * 0.05f, 0.5f + f * 0.1f, 1.0f);
    }

    @Environment(value=EnvType.CLIENT)
    public int getSkyColor() {
        return this.skyColor;
    }

    public void addSpawn(SpawnGroup group, SpawnEntry spawnEntry) {
        this.spawns.get(group).add(spawnEntry);
    }

    protected void addSpawnDensity(EntityType<?> type, double maxMass, double mass) {
        this.spawnDensities.put(type, new SpawnDensity(mass, maxMass));
    }

    public List<SpawnEntry> getEntitySpawnList(SpawnGroup group) {
        return this.spawns.get(group);
    }

    @Nullable
    public SpawnDensity getSpawnDensity(EntityType<?> type) {
        return this.spawnDensities.get(type);
    }

    public Precipitation getPrecipitation() {
        return this.precipitation;
    }

    public boolean hasHighHumidity() {
        return this.getRainfall() > 0.85f;
    }

    public float getMaxSpawnChance() {
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

    public boolean canSetIce(WorldView world, BlockPos blockPos) {
        return this.canSetIce(world, blockPos, true);
    }

    public boolean canSetIce(WorldView world, BlockPos pos, boolean doWaterCheck) {
        if (this.getTemperature(pos) >= 0.15f) {
            return false;
        }
        if (pos.getY() >= 0 && pos.getY() < 256 && world.getLightLevel(LightType.BLOCK, pos) < 10) {
            BlockState blockState = world.getBlockState(pos);
            FluidState fluidState = world.getFluidState(pos);
            if (fluidState.getFluid() == Fluids.WATER && blockState.getBlock() instanceof FluidBlock) {
                boolean bl;
                if (!doWaterCheck) {
                    return true;
                }
                boolean bl2 = bl = world.isWater(pos.west()) && world.isWater(pos.east()) && world.isWater(pos.north()) && world.isWater(pos.south());
                if (!bl) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean canSetSnow(WorldView world, BlockPos blockPos) {
        BlockState blockState;
        if (this.getTemperature(blockPos) >= 0.15f) {
            return false;
        }
        return blockPos.getY() >= 0 && blockPos.getY() < 256 && world.getLightLevel(LightType.BLOCK, blockPos) < 10 && (blockState = world.getBlockState(blockPos)).isAir() && Blocks.SNOW.getDefaultState().canPlaceAt(world, blockPos);
    }

    public void addFeature(GenerationStep.Feature step, ConfiguredFeature<?, ?> configuredFeature) {
        this.addFeature(step.ordinal(), () -> configuredFeature);
    }

    public void addFeature(int stepIndex, Supplier<ConfiguredFeature<?, ?>> supplier) {
        if (supplier.get().feature == Feature.DECORATED_FLOWER) {
            this.flowerFeatures.add(supplier.get());
        }
        while (this.features.size() <= stepIndex) {
            this.features.add(Lists.newArrayList());
        }
        this.features.get(stepIndex).add(supplier);
    }

    public <C extends CarverConfig> void addCarver(GenerationStep.Carver step, ConfiguredCarver<C> configuredCarver) {
        this.carvers.computeIfAbsent(step, carver -> Lists.newArrayList()).add(() -> configuredCarver);
    }

    public List<Supplier<ConfiguredCarver<?>>> getCarversForStep(GenerationStep.Carver carver) {
        return this.carvers.getOrDefault(carver, ImmutableList.of());
    }

    public void addStructureFeature(ConfiguredStructureFeature<?, ?> configuredStructureFeature) {
        this.structureFeatures.add(() -> configuredStructureFeature);
    }

    public boolean hasStructureFeature(StructureFeature<?> structureFeature) {
        return this.structureFeatures.stream().anyMatch(supplier -> ((ConfiguredStructureFeature)supplier.get()).feature == structureFeature);
    }

    public Iterable<Supplier<ConfiguredStructureFeature<?, ?>>> getStructureFeatures() {
        return this.structureFeatures;
    }

    public ConfiguredStructureFeature<?, ?> method_28405(ConfiguredStructureFeature<?, ?> configuredStructureFeature) {
        return DataFixUtils.orElse(this.structureFeatures.stream().map(Supplier::get).filter(configuredStructureFeature2 -> configuredStructureFeature2.feature == configuredStructureFeature.feature).findAny(), configuredStructureFeature);
    }

    public List<ConfiguredFeature<?, ?>> getFlowerFeatures() {
        return this.flowerFeatures;
    }

    /**
     * @return The lists of features configured for each {@link net.minecraft.world.gen.GenerationStep.Feature feature generation step}, up to the highest step that has a configured feature.
     * Entries are guaranteed to not be null, but may be empty lists if an earlier step has no features, but a later step does.
     */
    public List<List<Supplier<ConfiguredFeature<?, ?>>>> getFeatures() {
        return this.features;
    }

    public void generateFeatureStep(StructureAccessor structureAccessor, ChunkGenerator chunkGenerator, ChunkRegion chunkRegion, long populationSeed, ChunkRandom chunkRandom, BlockPos blockPos) {
        for (int i = 0; i < this.features.size(); ++i) {
            int j = 0;
            if (structureAccessor.shouldGenerateStructures()) {
                for (StructureFeature structureFeature : Registry.STRUCTURE_FEATURE) {
                    if (structureFeature.getGenerationStep().ordinal() != i) continue;
                    chunkRandom.setDecoratorSeed(populationSeed, j, i);
                    int k = blockPos.getX() >> 4;
                    int l = blockPos.getZ() >> 4;
                    int m = k << 4;
                    int n = l << 4;
                    try {
                        structureAccessor.getStructuresWithChildren(ChunkSectionPos.from(blockPos), structureFeature).forEach(structureStart -> structureStart.generateStructure(chunkRegion, structureAccessor, chunkGenerator, chunkRandom, new BlockBox(m, n, m + 15, n + 15), new ChunkPos(k, l)));
                    } catch (Exception exception) {
                        CrashReport crashReport = CrashReport.create(exception, "Feature placement");
                        crashReport.addElement("Feature").add("Id", Registry.STRUCTURE_FEATURE.getId(structureFeature)).add("Description", () -> structureFeature.toString());
                        throw new CrashException(crashReport);
                    }
                    ++j;
                }
            }
            for (Supplier supplier : this.features.get(i)) {
                ConfiguredFeature configuredFeature = (ConfiguredFeature)supplier.get();
                chunkRandom.setDecoratorSeed(populationSeed, j, i);
                try {
                    configuredFeature.generate(chunkRegion, chunkGenerator, chunkRandom, blockPos);
                } catch (Exception exception2) {
                    CrashReport crashReport2 = CrashReport.create(exception2, "Feature placement");
                    crashReport2.addElement("Feature").add("Id", Registry.FEATURE.getId((Feature<?>)configuredFeature.feature)).add("Config", configuredFeature.config).add("Description", () -> configuredFeature.feature.toString());
                    throw new CrashException(crashReport2);
                }
                ++j;
            }
        }
    }

    @Environment(value=EnvType.CLIENT)
    public int getFogColor() {
        return this.effects.getFogColor();
    }

    @Environment(value=EnvType.CLIENT)
    public int getGrassColorAt(double x, double z) {
        double d = MathHelper.clamp(this.getTemperature(), 0.0f, 1.0f);
        double e = MathHelper.clamp(this.getRainfall(), 0.0f, 1.0f);
        return GrassColors.getColor(d, e);
    }

    @Environment(value=EnvType.CLIENT)
    public int getFoliageColor() {
        double d = MathHelper.clamp(this.getTemperature(), 0.0f, 1.0f);
        double e = MathHelper.clamp(this.getRainfall(), 0.0f, 1.0f);
        return FoliageColors.getColor(d, e);
    }

    public void buildSurface(Random random, Chunk chunk, int x, int z, int worldHeight, double noise, BlockState defaultBlock, BlockState defaultFluid, int seaLevel, long seed) {
        ConfiguredSurfaceBuilder<?> configuredSurfaceBuilder = this.surfaceBuilder.get();
        configuredSurfaceBuilder.initSeed(seed);
        configuredSurfaceBuilder.generate(random, chunk, this, x, z, worldHeight, noise, defaultBlock, defaultFluid, seaLevel, seed);
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

    public final float getScale() {
        return this.scale;
    }

    public final float getTemperature() {
        return this.temperature;
    }

    public BiomeEffects getEffects() {
        return this.effects;
    }

    @Environment(value=EnvType.CLIENT)
    public final int getWaterColor() {
        return this.effects.getWaterColor();
    }

    @Environment(value=EnvType.CLIENT)
    public final int getWaterFogColor() {
        return this.effects.getWaterFogColor();
    }

    @Environment(value=EnvType.CLIENT)
    public Optional<BiomeParticleConfig> getParticleConfig() {
        return this.effects.getParticleConfig();
    }

    @Environment(value=EnvType.CLIENT)
    public Optional<SoundEvent> getLoopSound() {
        return this.effects.getLoopSound();
    }

    @Environment(value=EnvType.CLIENT)
    public Optional<BiomeMoodSound> getMoodSound() {
        return this.effects.getMoodSound();
    }

    @Environment(value=EnvType.CLIENT)
    public Optional<BiomeAdditionsSound> getAdditionsSound() {
        return this.effects.getAdditionsSound();
    }

    @Environment(value=EnvType.CLIENT)
    public Optional<MusicSound> method_27343() {
        return this.effects.method_27345();
    }

    public final Category getCategory() {
        return this.category;
    }

    public Supplier<ConfiguredSurfaceBuilder<?>> getSurfaceBuilder() {
        return this.surfaceBuilder;
    }

    public SurfaceConfig getSurfaceConfig() {
        return this.surfaceBuilder.get().getConfig();
    }

    @Nullable
    public String getParent() {
        return this.parent;
    }

    public String toString() {
        Identifier identifier = BuiltinRegistries.BIOME.getId(this);
        return identifier == null ? super.toString() : identifier.toString();
    }

    public static class MixedNoisePoint {
        public static final Codec<MixedNoisePoint> CODEC = RecordCodecBuilder.create(instance -> instance.group(((MapCodec)Codec.floatRange(-2.0f, 2.0f).fieldOf("temperature")).forGetter(mixedNoisePoint -> Float.valueOf(mixedNoisePoint.temperature)), ((MapCodec)Codec.floatRange(-2.0f, 2.0f).fieldOf("humidity")).forGetter(mixedNoisePoint -> Float.valueOf(mixedNoisePoint.humidity)), ((MapCodec)Codec.floatRange(-2.0f, 2.0f).fieldOf("altitude")).forGetter(mixedNoisePoint -> Float.valueOf(mixedNoisePoint.altitude)), ((MapCodec)Codec.floatRange(-2.0f, 2.0f).fieldOf("weirdness")).forGetter(mixedNoisePoint -> Float.valueOf(mixedNoisePoint.weirdness)), ((MapCodec)Codec.floatRange(0.0f, 1.0f).fieldOf("offset")).forGetter(mixedNoisePoint -> Float.valueOf(mixedNoisePoint.weight))).apply((Applicative<MixedNoisePoint, ?>)instance, MixedNoisePoint::new));
        private final float temperature;
        private final float humidity;
        private final float altitude;
        private final float weirdness;
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
            }
            if (object == null || this.getClass() != object.getClass()) {
                return false;
            }
            MixedNoisePoint mixedNoisePoint = (MixedNoisePoint)object;
            if (Float.compare(mixedNoisePoint.temperature, this.temperature) != 0) {
                return false;
            }
            if (Float.compare(mixedNoisePoint.humidity, this.humidity) != 0) {
                return false;
            }
            if (Float.compare(mixedNoisePoint.altitude, this.altitude) != 0) {
                return false;
            }
            return Float.compare(mixedNoisePoint.weirdness, this.weirdness) == 0;
        }

        public int hashCode() {
            int i = this.temperature != 0.0f ? Float.floatToIntBits(this.temperature) : 0;
            i = 31 * i + (this.humidity != 0.0f ? Float.floatToIntBits(this.humidity) : 0);
            i = 31 * i + (this.altitude != 0.0f ? Float.floatToIntBits(this.altitude) : 0);
            i = 31 * i + (this.weirdness != 0.0f ? Float.floatToIntBits(this.weirdness) : 0);
            return i;
        }

        public float calculateDistanceTo(MixedNoisePoint other) {
            return (this.temperature - other.temperature) * (this.temperature - other.temperature) + (this.humidity - other.humidity) * (this.humidity - other.humidity) + (this.altitude - other.altitude) * (this.altitude - other.altitude) + (this.weirdness - other.weirdness) * (this.weirdness - other.weirdness) + (this.weight - other.weight) * (this.weight - other.weight);
        }
    }

    public static class Settings {
        @Nullable
        private Supplier<ConfiguredSurfaceBuilder<?>> surfaceBuilder;
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
        private String parent;
        @Nullable
        private BiomeEffects specialEffects;

        public Settings configureSurfaceBuilder(ConfiguredSurfaceBuilder<?> configuredSurfaceBuilder) {
            return this.surfaceBuilder(() -> configuredSurfaceBuilder);
        }

        public Settings surfaceBuilder(Supplier<ConfiguredSurfaceBuilder<?>> supplier) {
            this.surfaceBuilder = supplier;
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

        public Settings depth(float depth) {
            this.depth = Float.valueOf(depth);
            return this;
        }

        public Settings scale(float scale) {
            this.scale = Float.valueOf(scale);
            return this;
        }

        public Settings temperature(float temperature) {
            this.temperature = Float.valueOf(temperature);
            return this;
        }

        public Settings downfall(float downfall) {
            this.downfall = Float.valueOf(downfall);
            return this;
        }

        public Settings parent(@Nullable String parent) {
            this.parent = parent;
            return this;
        }

        public Settings effects(BiomeEffects effects) {
            this.specialEffects = effects;
            return this;
        }

        public String toString() {
            return "BiomeBuilder{\nsurfaceBuilder=" + this.surfaceBuilder + ",\nprecipitation=" + this.precipitation + ",\nbiomeCategory=" + this.category + ",\ndepth=" + this.depth + ",\nscale=" + this.scale + ",\ntemperature=" + this.temperature + ",\ndownfall=" + this.downfall + ",\nspecialEffects=" + this.specialEffects + ",\nparent='" + this.parent + '\'' + "\n" + '}';
        }
    }

    public static class SpawnEntry
    extends WeightedPicker.Entry {
        public static final Codec<SpawnEntry> CODEC = RecordCodecBuilder.create(instance -> instance.group(((MapCodec)Registry.ENTITY_TYPE.fieldOf("type")).forGetter(spawnEntry -> spawnEntry.type), ((MapCodec)Codec.INT.fieldOf("weight")).forGetter(spawnEntry -> spawnEntry.weight), ((MapCodec)Codec.INT.fieldOf("minCount")).forGetter(spawnEntry -> spawnEntry.minGroupSize), ((MapCodec)Codec.INT.fieldOf("maxCount")).forGetter(spawnEntry -> spawnEntry.maxGroupSize)).apply((Applicative<SpawnEntry, ?>)instance, SpawnEntry::new));
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

    public static class SpawnDensity {
        public static final Codec<SpawnDensity> field_25820 = RecordCodecBuilder.create(instance -> instance.group(((MapCodec)Codec.DOUBLE.fieldOf("energy_budget")).forGetter(SpawnDensity::getGravityLimit), ((MapCodec)Codec.DOUBLE.fieldOf("charge")).forGetter(SpawnDensity::getMass)).apply((Applicative<SpawnDensity, ?>)instance, SpawnDensity::new));
        private final double gravityLimit;
        private final double mass;

        public SpawnDensity(double gravityLimit, double mass) {
            this.gravityLimit = gravityLimit;
            this.mass = mass;
        }

        public double getGravityLimit() {
            return this.gravityLimit;
        }

        public double getMass() {
            return this.mass;
        }
    }

    public static enum Precipitation implements StringIdentifiable
    {
        NONE("none"),
        RAIN("rain"),
        SNOW("snow");

        public static final Codec<Precipitation> field_24680;
        private static final Map<String, Precipitation> NAME_MAP;
        private final String name;

        private Precipitation(String name) {
            this.name = name;
        }

        public String getName() {
            return this.name;
        }

        public static Precipitation method_28431(String string) {
            return NAME_MAP.get(string);
        }

        @Override
        public String asString() {
            return this.name;
        }

        static {
            field_24680 = StringIdentifiable.createCodec(Precipitation::values, Precipitation::method_28431);
            NAME_MAP = Arrays.stream(Precipitation.values()).collect(Collectors.toMap(Precipitation::getName, precipitation -> precipitation));
        }
    }

    public static enum Category implements StringIdentifiable
    {
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

        public static final Codec<Category> field_24678;
        private static final Map<String, Category> NAME_MAP;
        private final String name;

        private Category(String name) {
            this.name = name;
        }

        public String getName() {
            return this.name;
        }

        public static Category method_28424(String string) {
            return NAME_MAP.get(string);
        }

        @Override
        public String asString() {
            return this.name;
        }

        static {
            field_24678 = StringIdentifiable.createCodec(Category::values, Category::method_28424);
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

        private TemperatureGroup(String name) {
            this.name = name;
        }

        public String getName() {
            return this.name;
        }

        static {
            NAME_MAP = Arrays.stream(TemperatureGroup.values()).collect(Collectors.toMap(TemperatureGroup::getName, temperatureGroup -> temperatureGroup));
        }
    }
}

