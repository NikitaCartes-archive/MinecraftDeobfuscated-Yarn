/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.dimension;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.io.File;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalLong;
import java.util.function.Supplier;
import net.minecraft.util.Identifier;
import net.minecraft.util.dynamic.RegistryElementCodec;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.util.registry.RegistryTracker;
import net.minecraft.util.registry.SimpleRegistry;
import net.minecraft.world.World;
import net.minecraft.world.biome.source.BiomeAccessType;
import net.minecraft.world.biome.source.HorizontalVoronoiBiomeAccessType;
import net.minecraft.world.biome.source.MultiNoiseBiomeSource;
import net.minecraft.world.biome.source.TheEndBiomeSource;
import net.minecraft.world.biome.source.VoronoiBiomeAccessType;
import net.minecraft.world.dimension.DimensionOptions;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorType;
import net.minecraft.world.gen.chunk.SurfaceChunkGenerator;

public class DimensionType {
    private static final Codec<RegistryKey<DimensionType>> REGISTRY_KEY_CODEC = Identifier.CODEC.xmap(RegistryKey.createKeyFactory(Registry.DIMENSION_TYPE_KEY), RegistryKey::getValue);
    public static final Codec<DimensionType> CODEC = RecordCodecBuilder.create(instance -> instance.group(Codec.LONG.optionalFieldOf("fixed_time").xmap(optional -> optional.map(OptionalLong::of).orElseGet(OptionalLong::empty), optionalLong -> optionalLong.isPresent() ? Optional.of(optionalLong.getAsLong()) : Optional.empty()).forGetter(dimensionType -> dimensionType.fixedTime), ((MapCodec)Codec.BOOL.fieldOf("has_skylight")).forGetter(DimensionType::hasSkyLight), ((MapCodec)Codec.BOOL.fieldOf("has_ceiling")).forGetter(DimensionType::hasCeiling), ((MapCodec)Codec.BOOL.fieldOf("ultrawarm")).forGetter(DimensionType::isUltrawarm), ((MapCodec)Codec.BOOL.fieldOf("natural")).forGetter(DimensionType::isNatural), ((MapCodec)Codec.BOOL.fieldOf("shrunk")).forGetter(DimensionType::isShrunk), ((MapCodec)Codec.FLOAT.fieldOf("ambient_light")).forGetter(dimensionType -> Float.valueOf(dimensionType.ambientLight))).apply((Applicative<DimensionType, ?>)instance, DimensionType::new));
    public static final float[] field_24752 = new float[]{1.0f, 0.75f, 0.5f, 0.25f, 0.0f, 0.25f, 0.5f, 0.75f};
    public static final RegistryKey<DimensionType> OVERWORLD_REGISTRY_KEY = RegistryKey.of(Registry.DIMENSION_TYPE_KEY, new Identifier("overworld"));
    public static final RegistryKey<DimensionType> THE_NETHER_REGISTRY_KEY = RegistryKey.of(Registry.DIMENSION_TYPE_KEY, new Identifier("the_nether"));
    public static final RegistryKey<DimensionType> THE_END_REGISTRY_KEY = RegistryKey.of(Registry.DIMENSION_TYPE_KEY, new Identifier("the_end"));
    private static final DimensionType OVERWORLD = new DimensionType("", OptionalLong.empty(), true, false, false, true, false, false, HorizontalVoronoiBiomeAccessType.INSTANCE, Optional.of(OVERWORLD_REGISTRY_KEY), 0.0f);
    private static final DimensionType THE_NETHER = new DimensionType("_nether", OptionalLong.of(18000L), false, true, true, false, true, false, VoronoiBiomeAccessType.INSTANCE, Optional.of(THE_NETHER_REGISTRY_KEY), 0.1f);
    private static final DimensionType THE_END = new DimensionType("_end", OptionalLong.of(6000L), false, false, false, false, false, true, VoronoiBiomeAccessType.INSTANCE, Optional.of(THE_END_REGISTRY_KEY), 0.0f);
    private static final Map<RegistryKey<DimensionType>, DimensionType> field_24759 = ImmutableMap.of(OVERWORLD_REGISTRY_KEY, DimensionType.getOverworldDimensionType(), THE_NETHER_REGISTRY_KEY, THE_NETHER, THE_END_REGISTRY_KEY, THE_END);
    private static final Codec<DimensionType> field_24760 = REGISTRY_KEY_CODEC.flatXmap(registryKey -> Optional.ofNullable(field_24759.get(registryKey)).map(DataResult::success).orElseGet(() -> DataResult.error("Unknown builtin dimension: " + registryKey)), dimensionType -> dimensionType.field_24765.map(DataResult::success).orElseGet(() -> DataResult.error("Unknown builtin dimension: " + dimensionType))).stable();
    private static final Codec<DimensionType> field_25410 = Codec.either(field_24760, CODEC).flatXmap(either -> either.map(dimensionType -> DataResult.success(dimensionType, Lifecycle.stable()), DataResult::success), dimensionType -> dimensionType.field_24765.isPresent() ? DataResult.success(Either.left(dimensionType), Lifecycle.stable()) : DataResult.success(Either.right(dimensionType)));
    public static final Codec<Supplier<DimensionType>> field_24756 = RegistryElementCodec.of(Registry.DIMENSION_TYPE_KEY, field_25410);
    private final String suffix;
    private final OptionalLong fixedTime;
    private final boolean hasSkyLight;
    private final boolean hasCeiling;
    private final boolean ultrawarm;
    private final boolean natural;
    private final boolean shrunk;
    private final boolean hasEnderDragonFight;
    private final BiomeAccessType biomeAccessType;
    private final Optional<RegistryKey<DimensionType>> field_24765;
    private final float ambientLight;
    private final transient float[] field_24767;

    public static DimensionType getOverworldDimensionType() {
        return OVERWORLD;
    }

    protected DimensionType(OptionalLong fixedTime, boolean hasSkylight, boolean hasCeiling, boolean ultrawarm, boolean natural, boolean shrunk, float ambientLight) {
        this("", fixedTime, hasSkylight, hasCeiling, ultrawarm, natural, shrunk, false, VoronoiBiomeAccessType.INSTANCE, Optional.empty(), ambientLight);
    }

    protected DimensionType(String suffix, OptionalLong fixedTime, boolean hasSkylight, boolean hasCeiling, boolean ultrawarm, boolean natural, boolean shrunk, boolean hasEnderDragonFight, BiomeAccessType biomeAccessType, Optional<RegistryKey<DimensionType>> optional, float ambientLight) {
        this.suffix = suffix;
        this.fixedTime = fixedTime;
        this.hasSkyLight = hasSkylight;
        this.hasCeiling = hasCeiling;
        this.ultrawarm = ultrawarm;
        this.natural = natural;
        this.shrunk = shrunk;
        this.hasEnderDragonFight = hasEnderDragonFight;
        this.biomeAccessType = biomeAccessType;
        this.field_24765 = optional;
        this.ambientLight = ambientLight;
        this.field_24767 = DimensionType.method_28515(ambientLight);
    }

    private static float[] method_28515(float f) {
        float[] fs = new float[16];
        for (int i = 0; i <= 15; ++i) {
            float g = (float)i / 15.0f;
            float h = g / (4.0f - 3.0f * g);
            fs[i] = MathHelper.lerp(f, h, 1.0f);
        }
        return fs;
    }

    @Deprecated
    public static DataResult<RegistryKey<World>> method_28521(Dynamic<?> dynamic) {
        DataResult<Number> dataResult = dynamic.asNumber();
        if (dataResult.result().equals(Optional.of(-1))) {
            return DataResult.success(World.NETHER);
        }
        if (dataResult.result().equals(Optional.of(0))) {
            return DataResult.success(World.OVERWORLD);
        }
        if (dataResult.result().equals(Optional.of(1))) {
            return DataResult.success(World.END);
        }
        return World.CODEC.parse(dynamic);
    }

    public static RegistryTracker.Modifiable addRegistryDefaults(RegistryTracker.Modifiable registryTracker) {
        registryTracker.addDimensionType(OVERWORLD_REGISTRY_KEY, DimensionType.getOverworldDimensionType());
        registryTracker.addDimensionType(THE_NETHER_REGISTRY_KEY, THE_NETHER);
        registryTracker.addDimensionType(THE_END_REGISTRY_KEY, THE_END);
        return registryTracker;
    }

    private static ChunkGenerator createEndGenerator(long seed) {
        return new SurfaceChunkGenerator(new TheEndBiomeSource(seed), seed, ChunkGeneratorType.Preset.END.getChunkGeneratorType());
    }

    private static ChunkGenerator createNetherGenerator(long seed) {
        return new SurfaceChunkGenerator(MultiNoiseBiomeSource.Preset.NETHER.getBiomeSource(seed), seed, ChunkGeneratorType.Preset.NETHER.getChunkGeneratorType());
    }

    public static SimpleRegistry<DimensionOptions> method_28517(long seed) {
        SimpleRegistry<DimensionOptions> simpleRegistry = new SimpleRegistry<DimensionOptions>(Registry.DIMENSION_OPTIONS, Lifecycle.experimental());
        simpleRegistry.add(DimensionOptions.NETHER, new DimensionOptions(() -> THE_NETHER, DimensionType.createNetherGenerator(seed)));
        simpleRegistry.add(DimensionOptions.END, new DimensionOptions(() -> THE_END, DimensionType.createEndGenerator(seed)));
        simpleRegistry.markLoaded(DimensionOptions.NETHER);
        simpleRegistry.markLoaded(DimensionOptions.END);
        return simpleRegistry;
    }

    public String getSuffix() {
        return this.suffix;
    }

    public static File getSaveDirectory(RegistryKey<World> worldRef, File root) {
        if (worldRef == World.OVERWORLD) {
            return root;
        }
        if (worldRef == World.END) {
            return new File(root, "DIM1");
        }
        if (worldRef == World.NETHER) {
            return new File(root, "DIM-1");
        }
        return new File(root, "dimensions/" + worldRef.getValue().getNamespace() + "/" + worldRef.getValue().getPath());
    }

    public boolean hasSkyLight() {
        return this.hasSkyLight;
    }

    public boolean hasCeiling() {
        return this.hasCeiling;
    }

    public boolean isUltrawarm() {
        return this.ultrawarm;
    }

    public boolean isNatural() {
        return this.natural;
    }

    public boolean isShrunk() {
        return this.shrunk;
    }

    public boolean hasEnderDragonFight() {
        return this.hasEnderDragonFight;
    }

    public BiomeAccessType getBiomeAccessType() {
        return this.biomeAccessType;
    }

    public float method_28528(long l) {
        double d = MathHelper.fractionalPart((double)this.fixedTime.orElse(l) / 24000.0 - 0.25);
        double e = 0.5 - Math.cos(d * Math.PI) / 2.0;
        return (float)(d * 2.0 + e) / 3.0f;
    }

    public int method_28531(long l) {
        return (int)(l / 24000L % 8L + 8L) % 8;
    }

    public float method_28516(int i) {
        return this.field_24767[i];
    }

    public boolean isOverworld() {
        return this.field_24765.equals(Optional.of(OVERWORLD_REGISTRY_KEY));
    }

    public boolean isNether() {
        return this.field_24765.equals(Optional.of(THE_NETHER_REGISTRY_KEY));
    }

    public boolean isEnd() {
        return this.field_24765.equals(Optional.of(THE_END_REGISTRY_KEY));
    }
}

