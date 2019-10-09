package net.minecraft;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;

public class class_4656 extends class_4651 {
	private final BlockState field_21314;

	public class_4656(BlockState blockState) {
		super(class_4652.SIMPLE_STATE_PROVIDER);
		this.field_21314 = blockState;
	}

	public <T> class_4656(Dynamic<T> dynamic) {
		this(BlockState.deserialize(dynamic.get("state").orElseEmptyMap()));
	}

	@Override
	public BlockState method_23455(Random random, BlockPos blockPos) {
		return this.field_21314;
	}

	@Override
	public <T> T serialize(DynamicOps<T> dynamicOps) {
		Builder<T, T> builder = ImmutableMap.builder();
		builder.put(dynamicOps.createString("type"), dynamicOps.createString(Registry.BLOCK_STATE_PROVIDER_TYPE.getId(this.field_21304).toString()))
			.put(dynamicOps.createString("state"), BlockState.serialize(dynamicOps, this.field_21314).getValue());
		return new Dynamic<>(dynamicOps, dynamicOps.createMap(builder.build())).getValue();
	}
}
