/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.feature;

import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.MultifaceGrowthBlock;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryCodecs;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.util.Util;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.gen.feature.FeatureConfig;

public class MultifaceGrowthFeatureConfig
implements FeatureConfig {
    public static final Codec<MultifaceGrowthFeatureConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(((MapCodec)Registries.BLOCK.getCodec().fieldOf("block")).flatXmap(MultifaceGrowthFeatureConfig::validateBlock, DataResult::success).orElse((MultifaceGrowthBlock)Blocks.GLOW_LICHEN).forGetter(config -> config.lichen), ((MapCodec)Codec.intRange(1, 64).fieldOf("search_range")).orElse(10).forGetter(config -> config.searchRange), ((MapCodec)Codec.BOOL.fieldOf("can_place_on_floor")).orElse(false).forGetter(config -> config.placeOnFloor), ((MapCodec)Codec.BOOL.fieldOf("can_place_on_ceiling")).orElse(false).forGetter(config -> config.placeOnCeiling), ((MapCodec)Codec.BOOL.fieldOf("can_place_on_wall")).orElse(false).forGetter(config -> config.placeOnWalls), ((MapCodec)Codec.floatRange(0.0f, 1.0f).fieldOf("chance_of_spreading")).orElse(Float.valueOf(0.5f)).forGetter(config -> Float.valueOf(config.spreadChance)), ((MapCodec)RegistryCodecs.entryList(RegistryKeys.BLOCK).fieldOf("can_be_placed_on")).forGetter(config -> config.canPlaceOn)).apply((Applicative<MultifaceGrowthFeatureConfig, ?>)instance, MultifaceGrowthFeatureConfig::new));
    public final MultifaceGrowthBlock lichen;
    public final int searchRange;
    public final boolean placeOnFloor;
    public final boolean placeOnCeiling;
    public final boolean placeOnWalls;
    public final float spreadChance;
    public final RegistryEntryList<Block> canPlaceOn;
    private final ObjectArrayList<Direction> directions;

    private static DataResult<MultifaceGrowthBlock> validateBlock(Block block) {
        DataResult<MultifaceGrowthBlock> dataResult;
        if (block instanceof MultifaceGrowthBlock) {
            MultifaceGrowthBlock multifaceGrowthBlock = (MultifaceGrowthBlock)block;
            dataResult = DataResult.success(multifaceGrowthBlock);
        } else {
            dataResult = DataResult.error(() -> "Growth block should be a multiface block");
        }
        return dataResult;
    }

    public MultifaceGrowthFeatureConfig(MultifaceGrowthBlock lichen, int searchRange, boolean placeOnFloor, boolean placeOnCeiling, boolean placeOnWalls, float spreadChance, RegistryEntryList<Block> canPlaceOn) {
        this.lichen = lichen;
        this.searchRange = searchRange;
        this.placeOnFloor = placeOnFloor;
        this.placeOnCeiling = placeOnCeiling;
        this.placeOnWalls = placeOnWalls;
        this.spreadChance = spreadChance;
        this.canPlaceOn = canPlaceOn;
        this.directions = new ObjectArrayList(6);
        if (placeOnCeiling) {
            this.directions.add(Direction.UP);
        }
        if (placeOnFloor) {
            this.directions.add(Direction.DOWN);
        }
        if (placeOnWalls) {
            Direction.Type.HORIZONTAL.forEach(this.directions::add);
        }
    }

    public List<Direction> shuffleDirections(Random random, Direction excluded) {
        return Util.copyShuffled(this.directions.stream().filter(direction -> direction != excluded), random);
    }

    public List<Direction> shuffleDirections(Random random) {
        return Util.copyShuffled(this.directions, random);
    }
}

