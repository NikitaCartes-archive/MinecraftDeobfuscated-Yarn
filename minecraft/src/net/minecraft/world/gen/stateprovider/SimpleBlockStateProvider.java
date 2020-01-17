package net.minecraft.world.gen.stateprovider;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;

public class SimpleBlockStateProvider extends BlockStateProvider {
	private final BlockState state;

	public SimpleBlockStateProvider(BlockState state) {
		super(BlockStateProviderType.SIMPLE_STATE_PROVIDER);
		this.state = state;
	}

	public <T> SimpleBlockStateProvider(Dynamic<T> configDeserializer) {
		this(BlockState.deserialize(configDeserializer.get("state").orElseEmptyMap()));
	}

	@Override
	public BlockState getBlockState(Random random, BlockPos pos) {
		return this.state;
	}

	@Override
	public <T> T serialize(DynamicOps<T> ops) {
		Builder<T, T> builder = ImmutableMap.builder();
		builder.put(ops.createString("type"), ops.createString(Registry.BLOCK_STATE_PROVIDER_TYPE.getId(this.stateProvider).toString()))
			.put(ops.createString("state"), BlockState.serialize(ops, this.state).getValue());
		return new Dynamic<>(ops, ops.createMap(builder.build())).getValue();
	}
}
