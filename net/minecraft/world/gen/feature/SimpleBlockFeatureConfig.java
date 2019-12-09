/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.feature;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.List;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.world.gen.feature.FeatureConfig;

public class SimpleBlockFeatureConfig
implements FeatureConfig {
    public final BlockState toPlace;
    public final List<BlockState> placeOn;
    public final List<BlockState> placeIn;
    public final List<BlockState> placeUnder;

    public SimpleBlockFeatureConfig(BlockState toPlace, List<BlockState> placeOn, List<BlockState> placeIn, List<BlockState> placeUnder) {
        this.toPlace = toPlace;
        this.placeOn = placeOn;
        this.placeIn = placeIn;
        this.placeUnder = placeUnder;
    }

    public SimpleBlockFeatureConfig(BlockState toPlace, BlockState[] placeOn, BlockState[] placeIn, BlockState[] placeUnder) {
        this(toPlace, Lists.newArrayList(placeOn), Lists.newArrayList(placeIn), Lists.newArrayList(placeUnder));
    }

    @Override
    public <T> Dynamic<T> serialize(DynamicOps<T> ops) {
        T object = BlockState.serialize(ops, this.toPlace).getValue();
        Object object2 = ops.createList(this.placeOn.stream().map(blockState -> BlockState.serialize(ops, blockState).getValue()));
        Object object3 = ops.createList(this.placeIn.stream().map(blockState -> BlockState.serialize(ops, blockState).getValue()));
        Object object4 = ops.createList(this.placeUnder.stream().map(blockState -> BlockState.serialize(ops, blockState).getValue()));
        return new Dynamic<Object>(ops, ops.createMap(ImmutableMap.of(ops.createString("to_place"), object, ops.createString("place_on"), object2, ops.createString("place_in"), object3, ops.createString("place_under"), object4)));
    }

    public static <T> SimpleBlockFeatureConfig deserialize(Dynamic<T> dynamic) {
        BlockState blockState = dynamic.get("to_place").map(BlockState::deserialize).orElse(Blocks.AIR.getDefaultState());
        List<BlockState> list = dynamic.get("place_on").asList(BlockState::deserialize);
        List<BlockState> list2 = dynamic.get("place_in").asList(BlockState::deserialize);
        List<BlockState> list3 = dynamic.get("place_under").asList(BlockState::deserialize);
        return new SimpleBlockFeatureConfig(blockState, list, list2, list3);
    }
}

