package net.minecraft;

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

public class class_4654 extends class_4651 {
	private static final BlockState[] field_21311 = new BlockState[]{
		Blocks.ORANGE_TULIP.getDefaultState(), Blocks.RED_TULIP.getDefaultState(), Blocks.PINK_TULIP.getDefaultState(), Blocks.WHITE_TULIP.getDefaultState()
	};
	private static final BlockState[] field_21312 = new BlockState[]{
		Blocks.POPPY.getDefaultState(), Blocks.AZURE_BLUET.getDefaultState(), Blocks.OXEYE_DAISY.getDefaultState(), Blocks.CORNFLOWER.getDefaultState()
	};

	public class_4654() {
		super(class_4652.PLAIN_FLOWER_PROVIDER);
	}

	public <T> class_4654(Dynamic<T> dynamic) {
		this();
	}

	@Override
	public BlockState method_23455(Random random, BlockPos blockPos) {
		double d = Biome.FOLIAGE_NOISE.sample((double)blockPos.getX() / 200.0, (double)blockPos.getZ() / 200.0, false);
		if (d < -0.8) {
			return field_21311[random.nextInt(field_21311.length)];
		} else {
			return random.nextInt(3) > 0 ? field_21312[random.nextInt(field_21312.length)] : Blocks.DANDELION.getDefaultState();
		}
	}

	@Override
	public <T> T serialize(DynamicOps<T> dynamicOps) {
		Builder<T, T> builder = ImmutableMap.builder();
		builder.put(dynamicOps.createString("type"), dynamicOps.createString(Registry.BLOCK_STATE_PROVIDER_TYPE.getId(this.field_21304).toString()));
		return new Dynamic<>(dynamicOps, dynamicOps.createMap(builder.build())).getValue();
	}
}
