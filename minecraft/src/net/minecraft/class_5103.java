package net.minecraft;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;
import net.minecraft.world.gen.feature.Feature;

public class class_5103 extends Feature<class_5105> {
	public class_5103(Function<Dynamic<?>, ? extends class_5105> function, Function<Random, ? extends class_5105> function2) {
		super(function, function2);
	}

	public boolean generate(IWorld iWorld, ChunkGenerator<? extends ChunkGeneratorConfig> chunkGenerator, Random random, BlockPos blockPos, class_5105 arg) {
		float f = MathHelper.lerp(random.nextFloat(), arg.field_23583, arg.field_23584);
		int i = MathHelper.ceil(f);
		BlockPos.stream(blockPos.add(-i, -i, -i), blockPos.add(i, i, i))
			.filter(blockPos2 -> arg.field_23582.method_26637(blockPos2, blockPos) < f)
			.forEach(blockPosx -> this.setBlockState(iWorld, blockPosx, arg.field_23581.getBlockState(random, blockPosx)));
		return true;
	}
}
