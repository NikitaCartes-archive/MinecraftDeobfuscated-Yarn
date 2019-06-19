package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.fluid.Fluids;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;

public class NetherSpringFeature extends Feature<NetherSpringFeatureConfig> {
	private static final BlockState NETHERRACK = Blocks.field_10515.getDefaultState();

	public NetherSpringFeature(Function<Dynamic<?>, ? extends NetherSpringFeatureConfig> function) {
		super(function);
	}

	public boolean method_13555(
		IWorld iWorld,
		ChunkGenerator<? extends ChunkGeneratorConfig> chunkGenerator,
		Random random,
		BlockPos blockPos,
		NetherSpringFeatureConfig netherSpringFeatureConfig
	) {
		if (iWorld.getBlockState(blockPos.up()) != NETHERRACK) {
			return false;
		} else if (!iWorld.getBlockState(blockPos).isAir() && iWorld.getBlockState(blockPos) != NETHERRACK) {
			return false;
		} else {
			int i = 0;
			if (iWorld.getBlockState(blockPos.west()) == NETHERRACK) {
				i++;
			}

			if (iWorld.getBlockState(blockPos.east()) == NETHERRACK) {
				i++;
			}

			if (iWorld.getBlockState(blockPos.north()) == NETHERRACK) {
				i++;
			}

			if (iWorld.getBlockState(blockPos.south()) == NETHERRACK) {
				i++;
			}

			if (iWorld.getBlockState(blockPos.down()) == NETHERRACK) {
				i++;
			}

			int j = 0;
			if (iWorld.isAir(blockPos.west())) {
				j++;
			}

			if (iWorld.isAir(blockPos.east())) {
				j++;
			}

			if (iWorld.isAir(blockPos.north())) {
				j++;
			}

			if (iWorld.isAir(blockPos.south())) {
				j++;
			}

			if (iWorld.isAir(blockPos.down())) {
				j++;
			}

			if (!netherSpringFeatureConfig.insideRock && i == 4 && j == 1 || i == 5) {
				iWorld.setBlockState(blockPos, Blocks.field_10164.getDefaultState(), 2);
				iWorld.getFluidTickScheduler().schedule(blockPos, Fluids.LAVA, 0);
			}

			return true;
		}
	}
}
