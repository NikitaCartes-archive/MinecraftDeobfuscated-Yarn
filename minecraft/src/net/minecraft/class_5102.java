package net.minecraft;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Consumer;
import java.util.function.Function;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;
import net.minecraft.world.gen.feature.Feature;

public class class_5102 extends Feature<class_5104> {
	public class_5102(Function<Dynamic<?>, ? extends class_5104> function, Function<Random, ? extends class_5104> function2) {
		super(function, function2);
	}

	public boolean generate(IWorld iWorld, ChunkGenerator<? extends ChunkGeneratorConfig> chunkGenerator, Random random, BlockPos blockPos, class_5104 arg) {
		return method_26586(blockPos, arg, blockPosx -> this.setBlockState(iWorld, blockPosx, arg.field_23574.getBlockState(random, blockPosx)));
	}

	private static void method_26587(Consumer<BlockPos> consumer, BlockPos.Mutable mutable, Direction direction, int i, byte b) {
		if ((i & b) != 0) {
			consumer.accept(mutable);
		}

		mutable.move(direction);
	}

	public static boolean method_26586(BlockPos blockPos, class_5104 arg, Consumer<BlockPos> consumer) {
		Direction direction = arg.field_23576.map(Direction.EAST);
		Direction direction2 = arg.field_23576.map(Direction.DOWN);
		byte[] bs = arg.method_26604();
		if (bs == null) {
			return false;
		} else {
			BlockPos.Mutable mutable = new BlockPos.Mutable();

			for (int i = 0; i < 8; i++) {
				mutable.set(blockPos).move(direction2, i);
				byte b = bs[i];
				method_26587(consumer, mutable, direction, 128, b);
				method_26587(consumer, mutable, direction, 64, b);
				method_26587(consumer, mutable, direction, 32, b);
				method_26587(consumer, mutable, direction, 16, b);
				method_26587(consumer, mutable, direction, 8, b);
				method_26587(consumer, mutable, direction, 4, b);
				method_26587(consumer, mutable, direction, 2, b);
				method_26587(consumer, mutable, direction, 1, b);
			}

			return true;
		}
	}
}
