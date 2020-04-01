package net.minecraft;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.List;
import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.stateprovider.BlockStateProvider;
import net.minecraft.world.gen.stateprovider.BlockStateProviderType;

public class class_5107 extends BlockStateProvider {
	private final List<BlockState> field_23594;

	public class_5107(Dynamic<?> dynamic) {
		this((List<BlockState>)dynamic.get("states").asStream().map(BlockState::deserialize).collect(ImmutableList.toImmutableList()));
	}

	public class_5107(List<BlockState> list) {
		super(BlockStateProviderType.RAINBOW_PROVIDER);
		this.field_23594 = list;
	}

	@Override
	public BlockState getBlockState(Random random, BlockPos pos) {
		int i = Math.abs(pos.getX() + pos.getY() + pos.getZ());
		return (BlockState)this.field_23594.get(i % this.field_23594.size());
	}

	@Override
	public <T> T serialize(DynamicOps<T> ops) {
		return ops.createMap(
			ImmutableMap.of(ops.createString("states"), ops.createList(this.field_23594.stream().map(blockState -> BlockState.serialize(ops, blockState).getValue())))
		);
	}
}
