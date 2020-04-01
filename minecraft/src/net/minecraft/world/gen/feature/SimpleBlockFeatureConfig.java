package net.minecraft.world.gen.feature;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.Util;
import net.minecraft.util.registry.Registry;

public class SimpleBlockFeatureConfig implements FeatureConfig {
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
		this(toPlace, Lists.<BlockState>newArrayList(placeOn), Lists.<BlockState>newArrayList(placeIn), Lists.<BlockState>newArrayList(placeUnder));
	}

	@Override
	public <T> Dynamic<T> serialize(DynamicOps<T> ops) {
		T object = BlockState.serialize(ops, this.toPlace).getValue();
		T object2 = ops.createList(this.placeOn.stream().map(state -> BlockState.serialize(ops, state).getValue()));
		T object3 = ops.createList(this.placeIn.stream().map(state -> BlockState.serialize(ops, state).getValue()));
		T object4 = ops.createList(this.placeUnder.stream().map(state -> BlockState.serialize(ops, state).getValue()));
		return new Dynamic<>(
			ops,
			ops.createMap(
				ImmutableMap.of(
					ops.createString("to_place"),
					object,
					ops.createString("place_on"),
					object2,
					ops.createString("place_in"),
					object3,
					ops.createString("place_under"),
					object4
				)
			)
		);
	}

	public static <T> SimpleBlockFeatureConfig deserialize(Dynamic<T> dynamic) {
		BlockState blockState = (BlockState)dynamic.get("to_place").map(BlockState::deserialize).orElse(Blocks.AIR.getDefaultState());
		List<BlockState> list = dynamic.get("place_on").asList(BlockState::deserialize);
		List<BlockState> list2 = dynamic.get("place_in").asList(BlockState::deserialize);
		List<BlockState> list3 = dynamic.get("place_under").asList(BlockState::deserialize);
		return new SimpleBlockFeatureConfig(blockState, list, list2, list3);
	}

	public static SimpleBlockFeatureConfig method_26642(Random random) {
		return new SimpleBlockFeatureConfig(
			Registry.BLOCK.getRandom(random).getDefaultState(),
			(List<BlockState>)Util.method_26718(random, 20, Registry.BLOCK).map(Block::getDefaultState).collect(Collectors.toList()),
			(List<BlockState>)Util.method_26718(random, 20, Registry.BLOCK).map(Block::getDefaultState).collect(Collectors.toList()),
			(List<BlockState>)Util.method_26718(random, 20, Registry.BLOCK).map(Block::getDefaultState).collect(Collectors.toList())
		);
	}
}
