package net.minecraft.world.gen.feature;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;

public class HugeFungusFeatureConfig implements FeatureConfig {
	public static final HugeFungusFeatureConfig field_22431 = new HugeFungusFeatureConfig(
		Blocks.CRIMSON_NYLIUM.getDefaultState(),
		Blocks.CRIMSON_STEM.getDefaultState(),
		Blocks.NETHER_WART_BLOCK.getDefaultState(),
		Blocks.SHROOMLIGHT.getDefaultState(),
		true
	);
	public static final HugeFungusFeatureConfig field_22432;
	public static final HugeFungusFeatureConfig field_22433 = new HugeFungusFeatureConfig(
		Blocks.WARPED_NYLIUM.getDefaultState(),
		Blocks.WARPED_STEM.getDefaultState(),
		Blocks.WARPED_WART_BLOCK.getDefaultState(),
		Blocks.SHROOMLIGHT.getDefaultState(),
		true
	);
	public static final HugeFungusFeatureConfig field_22434;
	public final BlockState field_22435;
	public final BlockState stemState;
	public final BlockState hatState;
	public final BlockState decorationState;
	public final boolean planted;

	public HugeFungusFeatureConfig(BlockState stemState, BlockState blockState, BlockState blockState2, BlockState blockState3, boolean bl) {
		this.field_22435 = stemState;
		this.stemState = blockState;
		this.hatState = blockState2;
		this.decorationState = blockState3;
		this.planted = bl;
	}

	@Override
	public <T> Dynamic<T> serialize(DynamicOps<T> ops) {
		return new Dynamic<>(
			ops,
			ops.createMap(
				ImmutableMap.of(
					ops.createString("valid_base_block"),
					BlockState.serialize(ops, this.field_22435).getValue(),
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

	public static <T> HugeFungusFeatureConfig deserialize(Dynamic<T> dynamic) {
		BlockState blockState = (BlockState)dynamic.get("valid_base_state").map(BlockState::deserialize).orElse(Blocks.AIR.getDefaultState());
		BlockState blockState2 = (BlockState)dynamic.get("stem_state").map(BlockState::deserialize).orElse(Blocks.AIR.getDefaultState());
		BlockState blockState3 = (BlockState)dynamic.get("hat_state").map(BlockState::deserialize).orElse(Blocks.AIR.getDefaultState());
		BlockState blockState4 = (BlockState)dynamic.get("decor_state").map(BlockState::deserialize).orElse(Blocks.AIR.getDefaultState());
		boolean bl = dynamic.get("planted").asBoolean(false);
		return new HugeFungusFeatureConfig(blockState, blockState2, blockState3, blockState4, bl);
	}

	static {
		field_22432 = new HugeFungusFeatureConfig(field_22431.field_22435, field_22431.stemState, field_22431.hatState, field_22431.decorationState, false);
		field_22434 = new HugeFungusFeatureConfig(field_22433.field_22435, field_22433.stemState, field_22433.hatState, field_22433.decorationState, false);
	}
}
