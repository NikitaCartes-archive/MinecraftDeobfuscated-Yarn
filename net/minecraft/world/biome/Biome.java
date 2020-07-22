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

public final class Biome {
    public static final Logger LOGGER = LogManager.getLogger();
    public static final MapCodec<Biome> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(Weather.CODEC.forGetter(biome -> biome.weather), ((MapCodec)Category.CODEC.fieldOf("category")).forGetter(biome -> biome.category), ((MapCodec)Codec.FLOAT.fieldOf("depth")).forGetter(biome -> Float.valueOf(biome.depth)), ((MapCodec)Codec.FLOAT.fieldOf("scale")).forGetter(biome -> Float.valueOf(biome.scale)), ((MapCodec)BiomeEffects.CODEC.fieldOf("effects")).forGetter(biome -> biome.effects), GenerationSettings.CODEC.forGetter(biome -> biome.generationSettings), SpawnSettings.CODEC.forGetter(biome -> biome.spawnSettings), Codec.STRING.optionalFieldOf("parent").forGetter(biome -> Optional.ofNullable(biome.parent))).apply((Applicative<Biome, ?>)instance, Biome::new));
    public static final Codec<Supplier<Biome>> REGISTRY_CODEC = RegistryElementCodec.of(Registry.BIOME_KEY, CODEC);
    public static final Set<Biome> BIOMES = Sets.newHashSet();
    private static final OctaveSimplexNoiseSampler TEMPERATURE_NOISE = new OctaveSimplexNoiseSampler(new ChunkRandom(1234L), ImmutableList.of(Integer.valueOf(0)));
    private static final OctaveSimplexNoiseSampler field_26392 = new OctaveSimplexNoiseSampler(new ChunkRandom(3456L), ImmutableList.of(Integer.valueOf(-2), Integer.valueOf(-1), Integer.valueOf(0)));
    public static final OctaveSimplexNoiseSampler FOLIAGE_NOISE = new OctaveSimplexNoiseSampler(new ChunkRandom(2345L), ImmutableList.of(Integer.valueOf(0)));
    private final Weather weather;
    private final GenerationSettings generationSettings;
    private final SpawnSettings spawnSettings;
    private final float depth;
    private final float scale;
    @Nullable
    protected final String parent;
    private final Category category;
    private final BiomeEffects effects;
    private final List<ConfiguredFeature<?, ?>> flowerFeatures;
    private final ThreadLocal<Long2FloatLinkedOpenHashMap> temperatureCache = ThreadLocal.withInitial(() -> Util.make(() -> {
        Long2FloatLinkedOpenHashMap long2FloatLinkedOpenHashMap = new Long2FloatLinkedOpenHashMap(1024, 0.25f){

            @Override
            protected void rehash(int i) {
            }
        };
        long2FloatLinkedOpenHashMap.defaultReturnValue(Float.NaN);
        return long2FloatLinkedOpenHashMap;
    }));

    public Biome(Settings settings) {
        if (settings.surfaceBuilder == null || settings.precipitation == null || settings.category == null || settings.depth == null || settings.scale == null || settings.temperature == null || settings.downfall == null || settings.specialEffects == null) {
            throw new IllegalStateException("You are missing parameters to build a proper biome for " + this.getClass().getSimpleName() + "\n" + settings);
        }
        this.weather = new Weather(settings.precipitation, settings.temperature.floatValue(), settings.temperatureModifier, settings.downfall.floatValue());
        this.category = settings.category;
        this.depth = settings.depth.floatValue();
        this.scale = settings.scale.floatValue();
        this.parent = settings.parent;
        this.effects = settings.specialEffects;
        this.spawnSettings = new SpawnSettings(settings.creatureGenerationProbability);
        this.generationSettings = new GenerationSettings(settings.surfaceBuilder);
        this.flowerFeatures = Lists.newArrayList();
    }

    private Biome(Weather weather, Category category, float depth, float scale, BiomeEffects effects, GenerationSettings generationSettings, SpawnSettings spawnSettings, Optional<String> parent) {
        this.weather = weather;
        this.generationSettings = generationSettings;
        this.spawnSettings = spawnSettings;
        this.category = category;
        this.depth = depth;
        this.scale = scale;
        this.effects = effects;
        this.parent = parent.orElse(null);
        this.flowerFeatures = generationSettings.features.stream().flatMap(Collection::stream).map(Supplier::get).flatMap(ConfiguredFeature::method_30648).filter(configuredFeature -> configuredFeature.feature == Feature.FLOWER).collect(Collectors.toList());
    }

    public boolean hasParent() {
        return this.parent != null;
    }

    @Environment(value=EnvType.CLIENT)
    public int getSkyColor() {
        return this.effects.getSkyColor();
    }

    public void addSpawn(SpawnGroup group, SpawnEntry spawnEntry) {
        ((List)this.spawnSettings.spawners.get(group)).add(spawnEntry);
    }

    public void addSpawnDensity(EntityType<?> type, double maxMass, double mass) {
        this.spawnSettings.spawnCosts.put(type, new SpawnDensity(mass, maxMass));
    }

    public List<SpawnEntry> getEntitySpawnList(SpawnGroup group) {
        return (List)this.spawnSettings.spawners.get(group);
    }

    @Nullable
    public SpawnDensity getSpawnDensity(EntityType<?> type) {
        return (SpawnDensity)this.spawnSettings.spawnCosts.get(type);
    }

    public Precipitation getPrecipitation() {
        return this.weather.precipitation;
    }

    public boolean hasHighHumidity() {
        return this.getDownfall() > 0.85f;
    }

    public float getMaxSpawnChance() {
        return this.spawnSettings.creatureSpawnProbability;
    }

    private float computeTemperature(BlockPos pos) {
        float f = this.weather.temperatureModifier.getModifiedTemperature(pos, this.getTemperature());
        if (pos.getY() > 64) {
            float g = (float)(TEMPERATURE_NOISE.sample((float)pos.getX() / 8.0f, (float)pos.getZ() / 8.0f, false) * 4.0);
            return f - (g + (float)pos.getY() - 64.0f) * 0.05f / 30.0f;
        }
        return f;
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
        supplier.get().method_30648().filter(configuredFeature -> configuredFeature.feature == Feature.FLOWER).forEach(this.flowerFeatures::add);
        this.method_30775(stepIndex);
        ((List)this.generationSettings.features.get(stepIndex)).add(supplier);
    }

    public <C extends CarverConfig> void addCarver(GenerationStep.Carver step, ConfiguredCarver<C> configuredCarver) {
        this.generationSettings.carvers.computeIfAbsent(step, carver -> Lists.newArrayList()).add(() -> configuredCarver);
    }

    public List<Supplier<ConfiguredCarver<?>>> getCarversForStep(GenerationStep.Carver carver) {
        return this.generationSettings.carvers.getOrDefault(carver, ImmutableList.of());
    }

    public void addStructureFeature(ConfiguredStructureFeature<?, ?> configuredStructureFeature) {
        this.generationSettings.starts.add(() -> configuredStructureFeature);
        this.method_30775(((StructureFeature)configuredStructureFeature.feature).getGenerationStep().ordinal());
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
        return DataFixUtils.orElse(this.generationSettings.starts.stream().map(Supplier::get).filter(configuredStructureFeature2 -> configuredStructureFeature2.feature == configuredStructureFeature.feature).findAny(), configuredStructureFeature);
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

    public void generateFeatureStep(StructureAccessor structureAccessor, ChunkGenerator chunkGenerator, ChunkRegion region, long populationSeed, ChunkRandom random, BlockPos pos) {
        for (int i = 0; i < this.generationSettings.features.size(); ++i) {
            int j = 0;
            if (structureAccessor.shouldGenerateStructures()) {
                for (StructureFeature structureFeature : Registry.STRUCTURE_FEATURE) {
                    if (structureFeature.getGenerationStep().ordinal() != i) continue;
                    random.setDecoratorSeed(populationSeed, j, i);
                    int k = pos.getX() >> 4;
                    int l = pos.getZ() >> 4;
                    int m = k << 4;
                    int n = l << 4;
                    try {
                        structureAccessor.getStructuresWithChildren(ChunkSectionPos.from(pos), structureFeature).forEach(structureStart -> structureStart.generateStructure(region, structureAccessor, chunkGenerator, random, new BlockBox(m, n, m + 15, n + 15), new ChunkPos(k, l)));
                    } catch (Exception exception) {
                        CrashReport crashReport = CrashReport.create(exception, "Feature placement");
                        crashReport.addElement("Feature").add("Id", Registry.STRUCTURE_FEATURE.getId(structureFeature)).add("Description", () -> structureFeature.toString());
                        throw new CrashException(crashReport);
                    }
                    ++j;
                }
            }
            for (Supplier supplier : (List)this.generationSettings.features.get(i)) {
                ConfiguredFeature configuredFeature = (ConfiguredFeature)supplier.get();
                random.setDecoratorSeed(populationSeed, j, i);
                try {
                    configuredFeature.generate(region, chunkGenerator, random, pos);
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
        int i = this.effects.getGrassColor().orElseGet(this::getDefaultGrassColor);
        return this.effects.getGrassColorModifier().getModifiedGrassColor(x, z, i);
    }

    @Environment(value=EnvType.CLIENT)
    private int getDefaultGrassColor() {
        double d = MathHelper.clamp(this.weather.temperature, 0.0f, 1.0f);
        double e = MathHelper.clamp(this.weather.downfall, 0.0f, 1.0f);
        return GrassColors.getColor(d, e);
    }

    @Environment(value=EnvType.CLIENT)
    public int getFoliageColor() {
        return this.effects.getFoliageColor().orElseGet(this::getDefaultFoliageColor);
    }

    @Environment(value=EnvType.CLIENT)
    private int getDefaultFoliageColor() {
        double d = MathHelper.clamp(this.weather.temperature, 0.0f, 1.0f);
        double e = MathHelper.clamp(this.weather.downfall, 0.0f, 1.0f);
        return FoliageColors.getColor(d, e);
    }

    public void buildSurface(Random random, Chunk chunk, int x, int z, int worldHeight, double noise, BlockState defaultBlock, BlockState defaultFluid, int seaLevel, long seed) {
        ConfiguredSurfaceBuilder configuredSurfaceBuilder = (ConfiguredSurfaceBuilder)this.generationSettings.surfaceBuilder.get();
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
    public Optional<MusicSound> getMusic() {
        return this.effects.getMusic();
    }

    public final Category getCategory() {
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

    static class Weather {
        public static final MapCodec<Weather> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(((MapCodec)Precipitation.CODEC.fieldOf("precipitation")).forGetter(weather -> weather.precipitation), ((MapCodec)Codec.FLOAT.fieldOf("temperature")).forGetter(weather -> Float.valueOf(weather.temperature)), TemperatureModifier.CODEC.optionalFieldOf("temperature_modifier", TemperatureModifier.NONE).forGetter(weather -> weather.temperatureModifier), ((MapCodec)Codec.FLOAT.fieldOf("downfall")).forGetter(weather -> Float.valueOf(weather.downfall))).apply((Applicative<Weather, ?>)instance, Weather::new));
        private final Precipitation precipitation;
        private final float temperature;
        private final TemperatureModifier temperatureModifier;
        private final float downfall;

        private Weather(Precipitation precipitation, float temperature, TemperatureModifier temperatureModifier, float downfall) {
            this.precipitation = precipitation;
            this.temperature = temperature;
            this.temperatureModifier = temperatureModifier;
            this.downfall = downfall;
        }
    }

    static class GenerationSettings {
        public static final MapCodec<GenerationSettings> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(((MapCodec)ConfiguredSurfaceBuilder.field_25015.fieldOf("surface_builder")).forGetter(generationSettings -> generationSettings.surfaceBuilder), Codec.simpleMap(GenerationStep.Carver.field_24770, ConfiguredCarver.field_24828.listOf().promotePartial((Consumer)Util.method_29188("Carver: ", LOGGER::error)), StringIdentifiable.method_28142(GenerationStep.Carver.values())).fieldOf("carvers").forGetter(generationSettings -> generationSettings.carvers), ((MapCodec)ConfiguredFeature.CODEC.listOf().promotePartial((Consumer)Util.method_29188("Feature: ", LOGGER::error)).listOf().fieldOf("features")).forGetter(generationSettings -> generationSettings.features), ((MapCodec)ConfiguredStructureFeature.REGISTRY_CODEC.listOf().promotePartial((Consumer)Util.method_29188("Structure start: ", LOGGER::error)).fieldOf("starts")).forGetter(generationSettings -> generationSettings.starts)).apply((Applicative<GenerationSettings, ?>)instance, GenerationSettings::new));
        private final Supplier<ConfiguredSurfaceBuilder<?>> surfaceBuilder;
        private final Map<GenerationStep.Carver, List<Supplier<ConfiguredCarver<?>>>> carvers;
        private final List<List<Supplier<ConfiguredFeature<?, ?>>>> features;
        private final List<Supplier<ConfiguredStructureFeature<?, ?>>> starts;

        private GenerationSettings(Supplier<ConfiguredSurfaceBuilder<?>> surfaceBuilder, Map<GenerationStep.Carver, List<Supplier<ConfiguredCarver<?>>>> carvers, List<List<Supplier<ConfiguredFeature<?, ?>>>> features, List<Supplier<ConfiguredStructureFeature<?, ?>>> starts) {
            this.surfaceBuilder = surfaceBuilder;
            this.carvers = carvers;
            this.features = features;
            this.starts = starts;
        }

        private GenerationSettings(Supplier<ConfiguredSurfaceBuilder<?>> surfaceBuilder) {
            this(surfaceBuilder, Maps.newLinkedHashMap(), Lists.newArrayList(), Lists.newArrayList());
        }
    }

    static class SpawnSettings {
        public static final MapCodec<SpawnSettings> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(Codec.FLOAT.optionalFieldOf("creature_spawn_probability", Float.valueOf(0.1f)).forGetter(spawnSettings -> Float.valueOf(spawnSettings.creatureSpawnProbability)), Codec.simpleMap(SpawnGroup.field_24655, SpawnEntry.CODEC.listOf().promotePartial((Consumer)Util.method_29188("Spawn data: ", LOGGER::error)), StringIdentifiable.method_28142(SpawnGroup.values())).fieldOf("spawners").forGetter(spawnSettings -> spawnSettings.spawners), Codec.simpleMap(Registry.ENTITY_TYPE, SpawnDensity.CODEC, Registry.ENTITY_TYPE).fieldOf("spawn_costs").forGetter(spawnSettings -> spawnSettings.spawnCosts)).apply((Applicative<SpawnSettings, ?>)instance, SpawnSettings::new));
        private final float creatureSpawnProbability;
        private final Map<SpawnGroup, List<SpawnEntry>> spawners;
        private final Map<EntityType<?>, SpawnDensity> spawnCosts;

        private SpawnSettings(float creatureSpawnProbability, Map<SpawnGroup, List<SpawnEntry>> spawners, Map<EntityType<?>, SpawnDensity> spawnCosts) {
            this.creatureSpawnProbability = creatureSpawnProbability;
            this.spawners = spawners;
            this.spawnCosts = spawnCosts;
        }

        private SpawnSettings(float creatureSpawnProbability) {
            this(creatureSpawnProbability, Maps.newLinkedHashMap(), Maps.newLinkedHashMap());
            for (SpawnGroup spawnGroup : SpawnGroup.values()) {
                this.spawners.put(spawnGroup, Lists.newArrayList());
            }
        }
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
        private TemperatureModifier temperatureModifier = TemperatureModifier.NONE;
        @Nullable
        private Float downfall;
        @Nullable
        private String parent;
        @Nullable
        private BiomeEffects specialEffects;
        private float creatureGenerationProbability = 0.1f;

        public Settings surfaceBuilder(ConfiguredSurfaceBuilder<?> surfaceBuilder) {
            return this.surfaceBuilder(() -> surfaceBuilder);
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

        public Settings creatureGenerationProbability(float probability) {
            this.creatureGenerationProbability = probability;
            return this;
        }

        public Settings temperatureModifier(TemperatureModifier temperatureModifier) {
            this.temperatureModifier = temperatureModifier;
            return this;
        }

        public String toString() {
            return "BiomeBuilder{\nsurfaceBuilder=" + this.surfaceBuilder + ",\nprecipitation=" + this.precipitation + ",\nbiomeCategory=" + this.category + ",\ndepth=" + this.depth + ",\nscale=" + this.scale + ",\ntemperature=" + this.temperature + ",\ntemperatureModifier=" + this.temperatureModifier + ",\ndownfall=" + this.downfall + ",\nspecialEffects=" + this.specialEffects + ",\ncreatureGenerationProbability=" + this.creatureGenerationProbability + ",\nparent='" + this.parent + '\'' + "\n" + '}';
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

    public static enum TemperatureModifier implements StringIdentifiable
    {
        NONE("none"){

            @Override
            public float getModifiedTemperature(BlockPos pos, float temperature) {
                return temperature;
            }
        }
        ,
        FROZEN("frozen"){

            @Override
            public float getModifiedTemperature(BlockPos pos, float temperature) {
                double g;
                double e;
                double d = field_26392.sample((double)pos.getX() * 0.05, (double)pos.getZ() * 0.05, false) * 7.0;
                double f = d + (e = FOLIAGE_NOISE.sample((double)pos.getX() * 0.2, (double)pos.getZ() * 0.2, false));
                if (f < 0.3 && (g = FOLIAGE_NOISE.sample((double)pos.getX() * 0.09, (double)pos.getZ() * 0.09, false)) < 0.8) {
                    return 0.2f;
                }
                return temperature;
            }
        };

        private final String name;
        public static final Codec<TemperatureModifier> CODEC;
        private static final Map<String, TemperatureModifier> BY_NAME;

        public abstract float getModifiedTemperature(BlockPos var1, float var2);

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

        public static TemperatureModifier byName(String name) {
            return BY_NAME.get(name);
        }

        static {
            CODEC = StringIdentifiable.createCodec(TemperatureModifier::values, TemperatureModifier::byName);
            BY_NAME = Arrays.stream(TemperatureModifier.values()).collect(Collectors.toMap(TemperatureModifier::getName, temperatureModifier -> temperatureModifier));
        }
    }

    public static class SpawnDensity {
        public static final Codec<SpawnDensity> CODEC = RecordCodecBuilder.create(instance -> instance.group(((MapCodec)Codec.DOUBLE.fieldOf("energy_budget")).forGetter(SpawnDensity::getGravityLimit), ((MapCodec)Codec.DOUBLE.fieldOf("charge")).forGetter(SpawnDensity::getMass)).apply((Applicative<SpawnDensity, ?>)instance, SpawnDensity::new));
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

        public static final Codec<Precipitation> CODEC;
        private static final Map<String, Precipitation> BY_NAME;
        private final String name;

        private Precipitation(String name) {
            this.name = name;
        }

        public String getName() {
            return this.name;
        }

        public static Precipitation byName(String name) {
            return BY_NAME.get(name);
        }

        @Override
        public String asString() {
            return this.name;
        }

        static {
            CODEC = StringIdentifiable.createCodec(Precipitation::values, Precipitation::byName);
            BY_NAME = Arrays.stream(Precipitation.values()).collect(Collectors.toMap(Precipitation::getName, precipitation -> precipitation));
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

        public static final Codec<Category> CODEC;
        private static final Map<String, Category> BY_NAME;
        private final String name;

        private Category(String name) {
            this.name = name;
        }

        public String getName() {
            return this.name;
        }

        public static Category byName(String name) {
            return BY_NAME.get(name);
        }

        @Override
        public String asString() {
            return this.name;
        }

        static {
            CODEC = StringIdentifiable.createCodec(Category::values, Category::byName);
            BY_NAME = Arrays.stream(Category.values()).collect(Collectors.toMap(Category::getName, category -> category));
        }
    }

    public static enum TemperatureGroup {
        OCEAN("ocean"),
        COLD("cold"),
        MEDIUM("medium"),
        WARM("warm");

        private static final Map<String, TemperatureGroup> BY_NAME;
        private final String name;

        private TemperatureGroup(String name) {
            this.name = name;
        }

        public String getName() {
            return this.name;
        }

        static {
            BY_NAME = Arrays.stream(TemperatureGroup.values()).collect(Collectors.toMap(TemperatureGroup::getName, temperatureGroup -> temperatureGroup));
        }
    }
}

