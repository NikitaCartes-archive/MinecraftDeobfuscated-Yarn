/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.feature;

import com.google.common.collect.Lists;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.minecraft.block.AbstractLichenBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.Direction;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryCodecs;
import net.minecraft.util.registry.RegistryEntryList;
import net.minecraft.world.gen.feature.FeatureConfig;

public class GlowLichenFeatureConfig
implements FeatureConfig {
    public static final Codec<GlowLichenFeatureConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(((MapCodec)Registry.BLOCK.getCodec().fieldOf("block")).flatXmap(GlowLichenFeatureConfig::method_41573, DataResult::success).orElse((AbstractLichenBlock)Blocks.GLOW_LICHEN).forGetter(glowLichenFeatureConfig -> glowLichenFeatureConfig.field_37709), ((MapCodec)Codec.intRange(1, 64).fieldOf("search_range")).orElse(10).forGetter(config -> config.searchRange), ((MapCodec)Codec.BOOL.fieldOf("can_place_on_floor")).orElse(false).forGetter(config -> config.placeOnFloor), ((MapCodec)Codec.BOOL.fieldOf("can_place_on_ceiling")).orElse(false).forGetter(config -> config.placeOnCeiling), ((MapCodec)Codec.BOOL.fieldOf("can_place_on_wall")).orElse(false).forGetter(config -> config.placeOnWalls), ((MapCodec)Codec.floatRange(0.0f, 1.0f).fieldOf("chance_of_spreading")).orElse(Float.valueOf(0.5f)).forGetter(config -> Float.valueOf(config.spreadChance)), ((MapCodec)RegistryCodecs.entryList(Registry.BLOCK_KEY).fieldOf("can_be_placed_on")).forGetter(config -> config.canPlaceOn)).apply((Applicative<GlowLichenFeatureConfig, ?>)instance, GlowLichenFeatureConfig::new));
    public final AbstractLichenBlock field_37709;
    public final int searchRange;
    public final boolean placeOnFloor;
    public final boolean placeOnCeiling;
    public final boolean placeOnWalls;
    public final float spreadChance;
    public final RegistryEntryList<Block> canPlaceOn;
    public final List<Direction> directions;

    private static DataResult<AbstractLichenBlock> method_41573(Block block) {
        DataResult<AbstractLichenBlock> dataResult;
        if (block instanceof AbstractLichenBlock) {
            AbstractLichenBlock abstractLichenBlock = (AbstractLichenBlock)block;
            dataResult = DataResult.success(abstractLichenBlock);
        } else {
            dataResult = DataResult.error("Growth block should be a multiface block");
        }
        return dataResult;
    }

    public GlowLichenFeatureConfig(AbstractLichenBlock abstractLichenBlock, int i, boolean bl, boolean bl2, boolean bl3, float f, RegistryEntryList<Block> registryEntryList) {
        this.field_37709 = abstractLichenBlock;
        this.searchRange = i;
        this.placeOnFloor = bl;
        this.placeOnCeiling = bl2;
        this.placeOnWalls = bl3;
        this.spreadChance = f;
        this.canPlaceOn = registryEntryList;
        ArrayList<Direction> list = Lists.newArrayList();
        if (bl2) {
            list.add(Direction.UP);
        }
        if (bl) {
            list.add(Direction.DOWN);
        }
        if (bl3) {
            Direction.Type.HORIZONTAL.forEach(list::add);
        }
        this.directions = Collections.unmodifiableList(list);
    }
}

