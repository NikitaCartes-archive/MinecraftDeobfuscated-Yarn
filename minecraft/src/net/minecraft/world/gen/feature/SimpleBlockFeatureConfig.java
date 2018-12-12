package net.minecraft.world.gen.feature;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;

public class SimpleBlockFeatureConfig implements FeatureConfig {
	final BlockState toPlace;
	final List<BlockState> placeOn;
	final List<BlockState> placeIn;
	final List<BlockState> placeUnder;

	public SimpleBlockFeatureConfig(BlockState blockState, List<BlockState> list, List<BlockState> list2, List<BlockState> list3) {
		this.toPlace = blockState;
		this.placeOn = list;
		this.placeIn = list2;
		this.placeUnder = list3;
	}

	public SimpleBlockFeatureConfig(BlockState blockState, BlockState[] blockStates, BlockState[] blockStates2, BlockState[] blockStates3) {
		this(blockState, Lists.<BlockState>newArrayList(blockStates), Lists.<BlockState>newArrayList(blockStates2), Lists.<BlockState>newArrayList(blockStates3));
	}

	@Override
	public <T> Dynamic<T> serialize(DynamicOps<T> dynamicOps) {
		T object = BlockState.serialize(dynamicOps, this.toPlace).getValue();
		T object2 = dynamicOps.createList(this.placeOn.stream().map(blockState -> BlockState.serialize(dynamicOps, blockState).getValue()));
		T object3 = dynamicOps.createList(this.placeIn.stream().map(blockState -> BlockState.serialize(dynamicOps, blockState).getValue()));
		T object4 = dynamicOps.createList(this.placeUnder.stream().map(blockState -> BlockState.serialize(dynamicOps, blockState).getValue()));
		return new Dynamic<>(
			dynamicOps,
			dynamicOps.createMap(
				ImmutableMap.of(
					dynamicOps.createString("to_place"),
					object,
					dynamicOps.createString("place_on"),
					object2,
					dynamicOps.createString("place_in"),
					object3,
					dynamicOps.createString("place_under"),
					object4
				)
			)
		);
	}

	public static <T> SimpleBlockFeatureConfig make(Dynamic<T> dynamic) {
		BlockState blockState = (BlockState)dynamic.get("to_place").map(BlockState::deserialize).orElse(Blocks.field_10124.getDefaultState());
		List<BlockState> list = (List<BlockState>)((Stream)dynamic.get("place_on").flatMap(Dynamic::getStream).orElseGet(Stream::empty))
			.map(BlockState::deserialize)
			.collect(Collectors.toList());
		List<BlockState> list2 = (List<BlockState>)((Stream)dynamic.get("place_in").flatMap(Dynamic::getStream).orElseGet(Stream::empty))
			.map(BlockState::deserialize)
			.collect(Collectors.toList());
		List<BlockState> list3 = (List<BlockState>)((Stream)dynamic.get("place_under").flatMap(Dynamic::getStream).orElseGet(Stream::empty))
			.map(BlockState::deserialize)
			.collect(Collectors.toList());
		return new SimpleBlockFeatureConfig(blockState, list, list2, list3);
	}
}
