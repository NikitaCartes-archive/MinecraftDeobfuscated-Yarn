package net.minecraft.world.gen.stateprovider;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;

public class PlainFlowerStateProvider extends StateProvider {
	private static final BlockState[] tulips = new BlockState[]{
		Blocks.ORANGE_TULIP.getDefaultState(), Blocks.RED_TULIP.getDefaultState(), Blocks.PINK_TULIP.getDefaultState(), Blocks.WHITE_TULIP.getDefaultState()
	};
	private static final BlockState[] flowers = new BlockState[]{
		Blocks.POPPY.getDefaultState(), Blocks.AZURE_BLUET.getDefaultState(), Blocks.OXEYE_DAISY.getDefaultState(), Blocks.CORNFLOWER.getDefaultState()
	};

	public PlainFlowerStateProvider() {
		super(StateProviderType.PLAIN_FLOWER_PROVIDER);
	}

	public <T> PlainFlowerStateProvider(Dynamic<T> dynamic) {
		this();
	}

	@Override
	public BlockState getBlockState(Random random, BlockPos blockPos) {
		double d = Biome.FOLIAGE_NOISE.sample((double)blockPos.getX() / 200.0, (double)blockPos.getZ() / 200.0, false);
		if (d < -0.8) {
			return tulips[random.nextInt(tulips.length)];
		} else {
			return random.nextInt(3) > 0 ? flowers[random.nextInt(flowers.length)] : Blocks.DANDELION.getDefaultState();
		}
	}

	@Override
	public <T> T serialize(DynamicOps<T> dynamicOps) {
		Builder<T, T> builder = ImmutableMap.builder();
		builder.put(dynamicOps.createString("type"), dynamicOps.createString(Registry.BLOCK_STATE_PROVIDER_TYPE.getId(this.stateProvider).toString()));
		return new Dynamic<>(dynamicOps, dynamicOps.createMap(builder.build())).getValue();
	}
}
