package net.minecraft.world.gen.decorator;

import com.mojang.serialization.Codec;
import java.util.Random;
import java.util.stream.Stream;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;

public class CarvingMaskDecorator extends Decorator<CarvingMaskDecoratorConfig> {
	public CarvingMaskDecorator(Codec<CarvingMaskDecoratorConfig> codec) {
		super(codec);
	}

	public Stream<BlockPos> getPositions(
		DecoratorContext decoratorContext, Random random, CarvingMaskDecoratorConfig carvingMaskDecoratorConfig, BlockPos blockPos
	) {
		ChunkPos chunkPos = new ChunkPos(blockPos);
		return decoratorContext.getOrCreateCarvingMask(chunkPos, carvingMaskDecoratorConfig.carver).method_38866(chunkPos);
	}
}
