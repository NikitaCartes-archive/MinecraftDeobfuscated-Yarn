package net.minecraft.world.gen.decorator;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.Stream;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.gen.chunk.ChunkGenerator;

public abstract class SimpleDecorator<DC extends DecoratorConfig> extends Decorator<DC> {
	public SimpleDecorator(Function<Dynamic<?>, ? extends DC> function) {
		super(function);
	}

	@Override
	public final Stream<BlockPos> getPositions(WorldAccess world, ChunkGenerator generator, Random random, DC config, BlockPos pos) {
		return this.getPositions(random, config, pos);
	}

	protected abstract Stream<BlockPos> getPositions(Random random, DC config, BlockPos pos);
}
