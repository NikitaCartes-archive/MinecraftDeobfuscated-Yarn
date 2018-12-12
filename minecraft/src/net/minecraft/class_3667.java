package net.minecraft;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.Stream;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;
import net.minecraft.world.gen.decorator.Decorator;
import net.minecraft.world.gen.decorator.DecoratorConfig;

public abstract class class_3667<DC extends DecoratorConfig> extends Decorator<DC> {
	public class_3667(Function<Dynamic<?>, ? extends DC> function) {
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
