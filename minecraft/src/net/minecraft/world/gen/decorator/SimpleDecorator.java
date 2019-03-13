package net.minecraft.world.gen.decorator;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.Stream;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;

public abstract class SimpleDecorator<DC extends DecoratorConfig> extends Decorator<DC> {
	public SimpleDecorator(Function<Dynamic<?>, ? extends DC> function) {
		super(function);
	}

	@Override
	public final Stream<BlockPos> method_14452(
		IWorld iWorld, ChunkGenerator<? extends ChunkGeneratorConfig> chunkGenerator, Random random, DC decoratorConfig, BlockPos blockPos
	) {
		return this.method_15941(random, decoratorConfig, blockPos);
	}

	protected abstract Stream<BlockPos> method_15941(Random random, DC decoratorConfig, BlockPos blockPos);
}
