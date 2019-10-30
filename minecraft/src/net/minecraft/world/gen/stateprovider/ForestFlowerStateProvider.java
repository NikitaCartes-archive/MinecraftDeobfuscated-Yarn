package net.minecraft.world.gen.stateprovider;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;

public class ForestFlowerStateProvider extends StateProvider {
	private static final BlockState[] flowers = new BlockState[]{
		Blocks.DANDELION.getDefaultState(),
		Blocks.POPPY.getDefaultState(),
		Blocks.ALLIUM.getDefaultState(),
		Blocks.AZURE_BLUET.getDefaultState(),
		Blocks.RED_TULIP.getDefaultState(),
		Blocks.ORANGE_TULIP.getDefaultState(),
		Blocks.WHITE_TULIP.getDefaultState(),
		Blocks.PINK_TULIP.getDefaultState(),
		Blocks.OXEYE_DAISY.getDefaultState(),
		Blocks.CORNFLOWER.getDefaultState(),
		Blocks.LILY_OF_THE_VALLEY.getDefaultState()
	};

	public ForestFlowerStateProvider() {
		super(StateProviderType.FOREST_FLOWER_PROVIDER);
	}

	public <T> ForestFlowerStateProvider(Dynamic<T> configDeserializer) {
		this();
	}

	@Override
	public BlockState getBlockState(Random random, BlockPos pos) {
		double d = MathHelper.clamp((1.0 + Biome.FOLIAGE_NOISE.sample((double)pos.getX() / 48.0, (double)pos.getZ() / 48.0, false)) / 2.0, 0.0, 0.9999);
		return flowers[(int)(d * (double)flowers.length)];
	}

	@Override
	public <T> T serialize(DynamicOps<T> ops) {
		Builder<T, T> builder = ImmutableMap.builder();
		builder.put(ops.createString("type"), ops.createString(Registry.BLOCK_STATE_PROVIDER_TYPE.getId(this.stateProvider).toString()));
		return new Dynamic<>(ops, ops.createMap(builder.build())).getValue();
	}
}
