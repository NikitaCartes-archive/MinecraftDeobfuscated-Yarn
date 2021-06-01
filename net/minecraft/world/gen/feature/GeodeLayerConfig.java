/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.feature;

import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.minecraft.block.BlockState;
import net.minecraft.util.Identifier;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.world.gen.stateprovider.BlockStateProvider;

public class GeodeLayerConfig {
    public final BlockStateProvider fillingProvider;
    public final BlockStateProvider innerLayerProvider;
    public final BlockStateProvider alternateInnerLayerProvider;
    public final BlockStateProvider middleLayerProvider;
    public final BlockStateProvider outerLayerProvider;
    public final List<BlockState> innerBlocks;
    public final Identifier cannotReplace;
    public final Identifier invalidBlocks;
    public static final Codec<GeodeLayerConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(((MapCodec)BlockStateProvider.TYPE_CODEC.fieldOf("filling_provider")).forGetter(geodeLayerConfig -> geodeLayerConfig.fillingProvider), ((MapCodec)BlockStateProvider.TYPE_CODEC.fieldOf("inner_layer_provider")).forGetter(geodeLayerConfig -> geodeLayerConfig.innerLayerProvider), ((MapCodec)BlockStateProvider.TYPE_CODEC.fieldOf("alternate_inner_layer_provider")).forGetter(geodeLayerConfig -> geodeLayerConfig.alternateInnerLayerProvider), ((MapCodec)BlockStateProvider.TYPE_CODEC.fieldOf("middle_layer_provider")).forGetter(geodeLayerConfig -> geodeLayerConfig.middleLayerProvider), ((MapCodec)BlockStateProvider.TYPE_CODEC.fieldOf("outer_layer_provider")).forGetter(geodeLayerConfig -> geodeLayerConfig.outerLayerProvider), ((MapCodec)Codecs.method_36973(BlockState.CODEC.listOf()).fieldOf("inner_placements")).forGetter(geodeLayerConfig -> geodeLayerConfig.innerBlocks), ((MapCodec)Identifier.CODEC.fieldOf("cannot_replace")).forGetter(geodeLayerConfig -> geodeLayerConfig.cannotReplace), ((MapCodec)Identifier.CODEC.fieldOf("invalid_blocks")).forGetter(geodeLayerConfig -> geodeLayerConfig.invalidBlocks)).apply((Applicative<GeodeLayerConfig, ?>)instance, GeodeLayerConfig::new));

    public GeodeLayerConfig(BlockStateProvider fillingProvider, BlockStateProvider innerLayerProvider, BlockStateProvider alternateInnerLayerProvider, BlockStateProvider middleLayerProvider, BlockStateProvider outerLayerProvider, List<BlockState> innerBlocks, Identifier cannotReplace, Identifier invalidBlocks) {
        this.fillingProvider = fillingProvider;
        this.innerLayerProvider = innerLayerProvider;
        this.alternateInnerLayerProvider = alternateInnerLayerProvider;
        this.middleLayerProvider = middleLayerProvider;
        this.outerLayerProvider = outerLayerProvider;
        this.innerBlocks = innerBlocks;
        this.cannotReplace = cannotReplace;
        this.invalidBlocks = invalidBlocks;
    }
}

