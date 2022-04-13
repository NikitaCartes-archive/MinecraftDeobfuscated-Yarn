package net.minecraft.world.gen.treedecorator;

import com.mojang.serialization.Codec;
import java.util.function.Supplier;
import net.minecraft.block.VineBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.AbstractRandom;

public class TrunkVineTreeDecorator extends TreeDecorator {
	public static final Codec<TrunkVineTreeDecorator> CODEC = Codec.unit((Supplier<TrunkVineTreeDecorator>)(() -> TrunkVineTreeDecorator.INSTANCE));
	public static final TrunkVineTreeDecorator INSTANCE = new TrunkVineTreeDecorator();

	@Override
	protected TreeDecoratorType<?> getType() {
		return TreeDecoratorType.TRUNK_VINE;
	}

	@Override
	public void generate(TreeDecorator.Generator generator) {
		AbstractRandom abstractRandom = generator.getRandom();
		generator.getLogPositions().forEach(pos -> {
			if (abstractRandom.nextInt(3) > 0) {
				BlockPos blockPos = pos.west();
				if (generator.isAir(blockPos)) {
					generator.replaceWithVine(blockPos, VineBlock.EAST);
				}
			}

			if (abstractRandom.nextInt(3) > 0) {
				BlockPos blockPos = pos.east();
				if (generator.isAir(blockPos)) {
					generator.replaceWithVine(blockPos, VineBlock.WEST);
				}
			}

			if (abstractRandom.nextInt(3) > 0) {
				BlockPos blockPos = pos.north();
				if (generator.isAir(blockPos)) {
					generator.replaceWithVine(blockPos, VineBlock.SOUTH);
				}
			}

			if (abstractRandom.nextInt(3) > 0) {
				BlockPos blockPos = pos.south();
				if (generator.isAir(blockPos)) {
					generator.replaceWithVine(blockPos, VineBlock.NORTH);
				}
			}
		});
	}
}
