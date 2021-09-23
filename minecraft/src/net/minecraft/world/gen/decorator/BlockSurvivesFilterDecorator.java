package net.minecraft.world.gen.decorator;

import com.mojang.serialization.Codec;
import java.util.Random;
import java.util.stream.Stream;
import net.minecraft.util.math.BlockPos;

public class BlockSurvivesFilterDecorator extends Decorator<BlockSurvivesFilterDecoratorConfig> {
	public BlockSurvivesFilterDecorator(Codec<BlockSurvivesFilterDecoratorConfig> codec) {
		super(codec);
	}

	public Stream<BlockPos> getPositions(
		DecoratorContext decoratorContext, Random random, BlockSurvivesFilterDecoratorConfig blockSurvivesFilterDecoratorConfig, BlockPos blockPos
	) {
		return !blockSurvivesFilterDecoratorConfig.state().canPlaceAt(decoratorContext.getWorld(), blockPos) ? Stream.of() : Stream.of(blockPos);
	}
}
