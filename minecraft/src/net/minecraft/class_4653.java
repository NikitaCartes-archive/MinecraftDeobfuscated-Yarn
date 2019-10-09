package net.minecraft;

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

public class class_4653 extends class_4651 {
	private static final BlockState[] field_21310 = new BlockState[]{
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

	public class_4653() {
		super(class_4652.FOREST_FLOWER_PROVIDER);
	}

	public <T> class_4653(Dynamic<T> dynamic) {
		this();
	}

	@Override
	public BlockState method_23455(Random random, BlockPos blockPos) {
		double d = MathHelper.clamp((1.0 + Biome.FOLIAGE_NOISE.sample((double)blockPos.getX() / 48.0, (double)blockPos.getZ() / 48.0, false)) / 2.0, 0.0, 0.9999);
		return field_21310[(int)(d * (double)field_21310.length)];
	}

	@Override
	public <T> T serialize(DynamicOps<T> dynamicOps) {
		Builder<T, T> builder = ImmutableMap.builder();
		builder.put(dynamicOps.createString("type"), dynamicOps.createString(Registry.BLOCK_STATE_PROVIDER_TYPE.getId(this.field_21304).toString()));
		return new Dynamic<>(dynamicOps, dynamicOps.createMap(builder.build())).getValue();
	}
}
