/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.dimension;

import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.nio.file.Path;
import java.util.Optional;
import java.util.OptionalLong;
import net.minecraft.block.Block;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryElementCodec;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.intprovider.IntProvider;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionTypes;

public record DimensionType(OptionalLong fixedTime, boolean hasSkyLight, boolean hasCeiling, boolean ultrawarm, boolean natural, double coordinateScale, boolean bedWorks, boolean respawnAnchorWorks, int minY, int height, int logicalHeight, TagKey<Block> infiniburn, Identifier effects, float ambientLight, MonsterSettings monsterSettings) {
    public static final int SIZE_BITS_Y = BlockPos.SIZE_BITS_Y;
    public static final int field_33411 = 16;
    public static final int MAX_HEIGHT = (1 << SIZE_BITS_Y) - 32;
    public static final int MAX_COLUMN_HEIGHT = (MAX_HEIGHT >> 1) - 1;
    public static final int MIN_HEIGHT = MAX_COLUMN_HEIGHT - MAX_HEIGHT + 1;
    public static final int field_35478 = MAX_COLUMN_HEIGHT << 4;
    public static final int field_35479 = MIN_HEIGHT << 4;
    public static final Codec<DimensionType> CODEC = Codecs.exceptionCatching(RecordCodecBuilder.create(instance -> instance.group(Codecs.optionalLong(Codec.LONG.optionalFieldOf("fixed_time")).forGetter(DimensionType::fixedTime), ((MapCodec)Codec.BOOL.fieldOf("has_skylight")).forGetter(DimensionType::hasSkyLight), ((MapCodec)Codec.BOOL.fieldOf("has_ceiling")).forGetter(DimensionType::hasCeiling), ((MapCodec)Codec.BOOL.fieldOf("ultrawarm")).forGetter(DimensionType::ultrawarm), ((MapCodec)Codec.BOOL.fieldOf("natural")).forGetter(DimensionType::natural), ((MapCodec)Codec.doubleRange(1.0E-5f, 3.0E7).fieldOf("coordinate_scale")).forGetter(DimensionType::coordinateScale), ((MapCodec)Codec.BOOL.fieldOf("bed_works")).forGetter(DimensionType::bedWorks), ((MapCodec)Codec.BOOL.fieldOf("respawn_anchor_works")).forGetter(DimensionType::respawnAnchorWorks), ((MapCodec)Codec.intRange(MIN_HEIGHT, MAX_COLUMN_HEIGHT).fieldOf("min_y")).forGetter(DimensionType::minY), ((MapCodec)Codec.intRange(16, MAX_HEIGHT).fieldOf("height")).forGetter(DimensionType::height), ((MapCodec)Codec.intRange(0, MAX_HEIGHT).fieldOf("logical_height")).forGetter(DimensionType::logicalHeight), ((MapCodec)TagKey.codec(RegistryKeys.BLOCK).fieldOf("infiniburn")).forGetter(DimensionType::infiniburn), ((MapCodec)Identifier.CODEC.fieldOf("effects")).orElse(DimensionTypes.OVERWORLD_ID).forGetter(DimensionType::effects), ((MapCodec)Codec.FLOAT.fieldOf("ambient_light")).forGetter(DimensionType::ambientLight), MonsterSettings.CODEC.forGetter(DimensionType::monsterSettings)).apply((Applicative<DimensionType, ?>)instance, DimensionType::new)));
    private static final int field_31440 = 8;
    public static final float[] MOON_SIZES = new float[]{1.0f, 0.75f, 0.5f, 0.25f, 0.0f, 0.25f, 0.5f, 0.75f};
    public static final Codec<RegistryEntry<DimensionType>> REGISTRY_CODEC = RegistryElementCodec.of(RegistryKeys.DIMENSION_TYPE, CODEC);

    public DimensionType {
        if (j < 16) {
            throw new IllegalStateException("height has to be at least 16");
        }
        if (i + j > MAX_COLUMN_HEIGHT + 1) {
            throw new IllegalStateException("min_y + height cannot be higher than: " + (MAX_COLUMN_HEIGHT + 1));
        }
        if (k > j) {
            throw new IllegalStateException("logical_height cannot be higher than height");
        }
        if (j % 16 != 0) {
            throw new IllegalStateException("height has to be multiple of 16");
        }
        if (i % 16 != 0) {
            throw new IllegalStateException("min_y has to be a multiple of 16");
        }
    }

    @Deprecated
    public static DataResult<RegistryKey<World>> worldFromDimensionNbt(Dynamic<?> nbt) {
        Optional<Number> optional = nbt.asNumber().result();
        if (optional.isPresent()) {
            int i = optional.get().intValue();
            if (i == -1) {
                return DataResult.success(World.NETHER);
            }
            if (i == 0) {
                return DataResult.success(World.OVERWORLD);
            }
            if (i == 1) {
                return DataResult.success(World.END);
            }
        }
        return World.CODEC.parse(nbt);
    }

    public static double getCoordinateScaleFactor(DimensionType fromDimension, DimensionType toDimension) {
        double d = fromDimension.coordinateScale();
        double e = toDimension.coordinateScale();
        return d / e;
    }

    public static Path getSaveDirectory(RegistryKey<World> worldRef, Path worldDirectory) {
        if (worldRef == World.OVERWORLD) {
            return worldDirectory;
        }
        if (worldRef == World.END) {
            return worldDirectory.resolve("DIM1");
        }
        if (worldRef == World.NETHER) {
            return worldDirectory.resolve("DIM-1");
        }
        return worldDirectory.resolve("dimensions").resolve(worldRef.getValue().getNamespace()).resolve(worldRef.getValue().getPath());
    }

    public boolean hasFixedTime() {
        return this.fixedTime.isPresent();
    }

    public float getSkyAngle(long time) {
        double d = MathHelper.fractionalPart((double)this.fixedTime.orElse(time) / 24000.0 - 0.25);
        double e = 0.5 - Math.cos(d * Math.PI) / 2.0;
        return (float)(d * 2.0 + e) / 3.0f;
    }

    /**
     * Gets the moon phase index of Minecraft's moon.
     * 
     * <p>This is typically used to determine the size of the moon that should be rendered.
     * 
     * @param time the time to calculate the index from
     */
    public int getMoonPhase(long time) {
        return (int)(time / 24000L % 8L + 8L) % 8;
    }

    public boolean piglinSafe() {
        return this.monsterSettings.piglinSafe();
    }

    public boolean hasRaids() {
        return this.monsterSettings.hasRaids();
    }

    public IntProvider monsterSpawnLightTest() {
        return this.monsterSettings.monsterSpawnLightTest();
    }

    public int monsterSpawnBlockLightLimit() {
        return this.monsterSettings.monsterSpawnBlockLightLimit();
    }

    public record MonsterSettings(boolean piglinSafe, boolean hasRaids, IntProvider monsterSpawnLightTest, int monsterSpawnBlockLightLimit) {
        public static final MapCodec<MonsterSettings> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(((MapCodec)Codec.BOOL.fieldOf("piglin_safe")).forGetter(MonsterSettings::piglinSafe), ((MapCodec)Codec.BOOL.fieldOf("has_raids")).forGetter(MonsterSettings::hasRaids), ((MapCodec)IntProvider.createValidatingCodec(0, 15).fieldOf("monster_spawn_light_level")).forGetter(MonsterSettings::monsterSpawnLightTest), ((MapCodec)Codec.intRange(0, 15).fieldOf("monster_spawn_block_light_limit")).forGetter(MonsterSettings::monsterSpawnBlockLightLimit)).apply((Applicative<MonsterSettings, ?>)instance, MonsterSettings::new));
    }
}

