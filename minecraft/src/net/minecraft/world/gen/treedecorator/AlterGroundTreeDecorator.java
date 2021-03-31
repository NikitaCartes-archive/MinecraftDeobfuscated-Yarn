package net.minecraft.world.gen.treedecorator;

import com.mojang.serialization.Codec;
import java.util.List;
import java.util.Random;
import java.util.function.BiConsumer;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.TestableWorld;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.stateprovider.BlockStateProvider;

public class AlterGroundTreeDecorator extends TreeDecorator {
	public static final Codec<AlterGroundTreeDecorator> CODEC = BlockStateProvider.TYPE_CODEC
		.fieldOf("provider")
		.<AlterGroundTreeDecorator>xmap(AlterGroundTreeDecorator::new, alterGroundTreeDecorator -> alterGroundTreeDecorator.provider)
		.codec();
	private final BlockStateProvider provider;

	public AlterGroundTreeDecorator(BlockStateProvider provider) {
		this.provider = provider;
	}

	@Override
	protected TreeDecoratorType<?> getType() {
		return TreeDecoratorType.ALTER_GROUND;
	}

	@Override
	public void generate(
		TestableWorld testableWorld, BiConsumer<BlockPos, BlockState> biConsumer, Random random, List<BlockPos> leavesPositions, List<BlockPos> list
	) {
		int i = ((BlockPos)leavesPositions.get(0)).getY();
		leavesPositions.stream().filter(pos -> pos.getY() == i).forEach(blockPos -> {
			this.setArea(testableWorld, biConsumer, random, blockPos.west().north());
			this.setArea(testableWorld, biConsumer, random, blockPos.east(2).north());
			this.setArea(testableWorld, biConsumer, random, blockPos.west().south(2));
			this.setArea(testableWorld, biConsumer, random, blockPos.east(2).south(2));

			for (int ix = 0; ix < 5; ix++) {
				int j = random.nextInt(64);
				int k = j % 8;
				int l = j / 8;
				if (k == 0 || k == 7 || l == 0 || l == 7) {
					this.setArea(testableWorld, biConsumer, random, blockPos.add(-3 + k, 0, -3 + l));
				}
			}
		});
	}

	private void setArea(TestableWorld testableWorld, BiConsumer<BlockPos, BlockState> biConsumer, Random random, BlockPos blockPos) {
		for (int i = -2; i <= 2; i++) {
			for (int j = -2; j <= 2; j++) {
				if (Math.abs(i) != 2 || Math.abs(j) != 2) {
					this.setColumn(testableWorld, biConsumer, random, blockPos.add(i, 0, j));
				}
			}
		}
	}

	private void setColumn(TestableWorld testableWorld, BiConsumer<BlockPos, BlockState> biConsumer, Random random, BlockPos blockPos) {
		for (int i = 2; i >= -3; i--) {
			BlockPos blockPos2 = blockPos.up(i);
			if (Feature.isSoil(testableWorld, blockPos2)) {
				biConsumer.accept(blockPos2, this.provider.getBlockState(random, blockPos));
				break;
			}

			if (!Feature.isAir(testableWorld, blockPos2) && i < 0) {
				break;
			}
		}
	}
}
