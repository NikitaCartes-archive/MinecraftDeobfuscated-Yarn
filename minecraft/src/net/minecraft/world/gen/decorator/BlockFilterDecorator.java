package net.minecraft.world.gen.decorator;

import com.mojang.serialization.Codec;
import java.util.Random;
import java.util.stream.Stream;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

public class BlockFilterDecorator extends Decorator<BlockFilterDecoratorConfig> {
	public BlockFilterDecorator(Codec<BlockFilterDecoratorConfig> codec) {
		super(codec);
	}

	public Stream<BlockPos> getPositions(
		DecoratorContext decoratorContext, Random random, BlockFilterDecoratorConfig blockFilterDecoratorConfig, BlockPos blockPos
	) {
		BlockState blockState = decoratorContext.getWorld().getBlockState(blockPos.add(blockFilterDecoratorConfig.offset()));

		for (Block block : blockFilterDecoratorConfig.disallowed()) {
			if (blockState.isOf(block)) {
				return Stream.of();
			}
		}

		for (Block blockx : blockFilterDecoratorConfig.allowed()) {
			if (blockState.isOf(blockx)) {
				return Stream.of(blockPos);
			}
		}

		return blockFilterDecoratorConfig.allowed().isEmpty() ? Stream.of(blockPos) : Stream.of();
	}
}
