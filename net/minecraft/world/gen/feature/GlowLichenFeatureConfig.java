/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.feature;

import com.google.common.collect.Lists;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.Direction;
import net.minecraft.world.gen.feature.FeatureConfig;

public class GlowLichenFeatureConfig
implements FeatureConfig {
    public static final Codec<GlowLichenFeatureConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(((MapCodec)Codec.intRange(1, 64).fieldOf("search_range")).orElse(10).forGetter(glowLichenFeatureConfig -> glowLichenFeatureConfig.searchRange), ((MapCodec)Codec.BOOL.fieldOf("can_place_on_floor")).orElse(false).forGetter(glowLichenFeatureConfig -> glowLichenFeatureConfig.placeOnFloor), ((MapCodec)Codec.BOOL.fieldOf("can_place_on_ceiling")).orElse(false).forGetter(glowLichenFeatureConfig -> glowLichenFeatureConfig.placeOnCeiling), ((MapCodec)Codec.BOOL.fieldOf("can_place_on_wall")).orElse(false).forGetter(glowLichenFeatureConfig -> glowLichenFeatureConfig.placeOnWalls), ((MapCodec)Codec.floatRange(0.0f, 1.0f).fieldOf("chance_of_spreading")).orElse(Float.valueOf(0.5f)).forGetter(glowLichenFeatureConfig -> Float.valueOf(glowLichenFeatureConfig.spreadChance)), ((MapCodec)BlockState.CODEC.listOf().fieldOf("can_be_placed_on")).forGetter(glowLichenFeatureConfig -> new ArrayList<BlockState>(glowLichenFeatureConfig.canPlaceOn))).apply((Applicative<GlowLichenFeatureConfig, ?>)instance, GlowLichenFeatureConfig::new));
    public final int searchRange;
    public final boolean placeOnFloor;
    public final boolean placeOnCeiling;
    public final boolean placeOnWalls;
    public final float spreadChance;
    public final List<BlockState> canPlaceOn;
    public final List<Direction> directions;

    public GlowLichenFeatureConfig(int searchRange, boolean placeOnFloor, boolean placeOnCeiling, boolean placeOnWalls, float spreadChance, List<BlockState> canPlaceOn) {
        this.searchRange = searchRange;
        this.placeOnFloor = placeOnFloor;
        this.placeOnCeiling = placeOnCeiling;
        this.placeOnWalls = placeOnWalls;
        this.spreadChance = spreadChance;
        this.canPlaceOn = canPlaceOn;
        ArrayList<Direction> list = Lists.newArrayList();
        if (placeOnCeiling) {
            list.add(Direction.UP);
        }
        if (placeOnFloor) {
            list.add(Direction.DOWN);
        }
        if (placeOnWalls) {
            Direction.Type.HORIZONTAL.forEach(list::add);
        }
        this.directions = Collections.unmodifiableList(list);
    }

    public boolean canGrowOn(Block block) {
        return this.canPlaceOn.stream().anyMatch(state -> state.isOf(block));
    }
}

