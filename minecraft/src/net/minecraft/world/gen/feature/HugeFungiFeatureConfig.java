package net.minecraft.world.gen.feature;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;

public class HugeFungiFeatureConfig implements FeatureConfig {
	public final BlockState stemState;
	public final BlockState hatState;
	public final BlockState decorationState;
	public final boolean planted;

	public HugeFungiFeatureConfig(BlockState stemState, BlockState hatState, BlockState decorationState, boolean planted) {
		this.stemState = stemState;
		this.hatState = hatState;
		this.decorationState = decorationState;
		this.planted = planted;
	}

	@Override
	public <T> Dynamic<T> serialize(DynamicOps<T> ops) {
		return new Dynamic<>(
			ops,
			ops.createMap(
				ImmutableMap.of(
					ops.createString("stem_state"),
					BlockState.serialize(ops, this.stemState).getValue(),
					ops.createString("hat_state"),
					BlockState.serialize(ops, this.hatState).getValue(),
					ops.createString("decor_state"),
					BlockState.serialize(ops, this.decorationState).getValue(),
					ops.createString("planted"),
					ops.createBoolean(this.planted)
				)
			)
		);
	}

	public static <T> HugeFungiFeatureConfig deserialize(Dynamic<T> dynamic) {
		BlockState blockState = (BlockState)dynamic.get("stem_state").map(BlockState::deserialize).orElse(Blocks.AIR.getDefaultState());
		BlockState blockState2 = (BlockState)dynamic.get("hat_state").map(BlockState::deserialize).orElse(Blocks.AIR.getDefaultState());
		BlockState blockState3 = (BlockState)dynamic.get("decor_state").map(BlockState::deserialize).orElse(Blocks.AIR.getDefaultState());
		boolean bl = dynamic.get("planted").asBoolean(false);
		return new HugeFungiFeatureConfig(blockState, blockState2, blockState3, bl);
	}
}
