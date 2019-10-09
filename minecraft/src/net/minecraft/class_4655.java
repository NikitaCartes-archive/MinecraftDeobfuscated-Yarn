package net.minecraft;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.PillarBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.registry.Registry;

public class class_4655 extends class_4651 {
	private final Block field_21313;

	public class_4655(Block block) {
		super(class_4652.SIMPLE_STATE_PROVIDER);
		this.field_21313 = block;
	}

	public <T> class_4655(Dynamic<T> dynamic) {
		this(BlockState.deserialize(dynamic.get("state").orElseEmptyMap()).getBlock());
	}

	@Override
	public BlockState method_23455(Random random, BlockPos blockPos) {
		Direction.Axis axis = Direction.Axis.method_16699(random);
		return this.field_21313.getDefaultState().with(PillarBlock.AXIS, axis);
	}

	@Override
	public <T> T serialize(DynamicOps<T> dynamicOps) {
		Builder<T, T> builder = ImmutableMap.builder();
		builder.put(dynamicOps.createString("type"), dynamicOps.createString(Registry.BLOCK_STATE_PROVIDER_TYPE.getId(this.field_21304).toString()))
			.put(dynamicOps.createString("state"), BlockState.serialize(dynamicOps, this.field_21313.getDefaultState()).getValue());
		return new Dynamic<>(dynamicOps, dynamicOps.createMap(builder.build())).getValue();
	}
}
