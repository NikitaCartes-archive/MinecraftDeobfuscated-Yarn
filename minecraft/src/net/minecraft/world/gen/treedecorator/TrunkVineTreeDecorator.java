package net.minecraft.world.gen.treedecorator;

import com.mojang.serialization.MapCodec;
import java.util.function.Supplier;
import net.minecraft.block.VineBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;

public class TrunkVineTreeDecorator extends TreeDecorator {
	public static final MapCodec<TrunkVineTreeDecorator> CODEC = MapCodec.unit((Supplier<TrunkVineTreeDecorator>)(() -> TrunkVineTreeDecorator.INSTANCE));
	public static final TrunkVineTreeDecorator INSTANCE = new TrunkVineTreeDecorator();

	@Override
	protected TreeDecoratorType<?> getType() {
		return TreeDecoratorType.TRUNK_VINE;
	}

	@Override
	public void generate(TreeDecorator.Generator generator) {
		Random random = generator.getRandom();
		generator.getLogPositions().forEach(pos -> {
			if (random.nextInt(3) > 0) {
				BlockPos blockPos = pos.west();
				if (generator.isAir(blockPos)) {
					generator.replaceWithVine(blockPos, VineBlock.EAST);
				}
			}

			if (random.nextInt(3) > 0) {
				BlockPos blockPos = pos.east();
				if (generator.isAir(blockPos)) {
					generator.replaceWithVine(blockPos, VineBlock.WEST);
				}
			}

			if (random.nextInt(3) > 0) {
				BlockPos blockPos = pos.north();
				if (generator.isAir(blockPos)) {
					generator.replaceWithVine(blockPos, VineBlock.SOUTH);
				}
			}

			if (random.nextInt(3) > 0) {
				BlockPos blockPos = pos.south();
				if (generator.isAir(blockPos)) {
					generator.replaceWithVine(blockPos, VineBlock.NORTH);
				}
			}
		});
	}
}
