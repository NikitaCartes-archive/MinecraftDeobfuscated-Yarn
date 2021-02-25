package net.minecraft.world.gen.treedecorator;

import com.mojang.serialization.Codec;
import java.util.List;
import java.util.Random;
import java.util.Set;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ModifiableTestableWorld;
import net.minecraft.world.StructureWorldAccess;
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
		StructureWorldAccess world, Random random, List<BlockPos> logPositions, List<BlockPos> leavesPositions, Set<BlockPos> placedStates, BlockBox box
	) {
		int i = ((BlockPos)logPositions.get(0)).getY();
		logPositions.stream().filter(pos -> pos.getY() == i).forEach(pos -> {
			this.setArea(world, random, pos.west().north());
			this.setArea(world, random, pos.east(2).north());
			this.setArea(world, random, pos.west().south(2));
			this.setArea(world, random, pos.east(2).south(2));

			for (int ix = 0; ix < 5; ix++) {
				int j = random.nextInt(64);
				int k = j % 8;
				int l = j / 8;
				if (k == 0 || k == 7 || l == 0 || l == 7) {
					this.setArea(world, random, pos.add(-3 + k, 0, -3 + l));
				}
			}
		});
	}

	private void setArea(ModifiableTestableWorld world, Random random, BlockPos pos) {
		for (int i = -2; i <= 2; i++) {
			for (int j = -2; j <= 2; j++) {
				if (Math.abs(i) != 2 || Math.abs(j) != 2) {
					this.setColumn(world, random, pos.add(i, 0, j));
				}
			}
		}
	}

	private void setColumn(ModifiableTestableWorld world, Random random, BlockPos pos) {
		for (int i = 2; i >= -3; i--) {
			BlockPos blockPos = pos.up(i);
			if (Feature.isSoil(world, blockPos)) {
				world.setBlockState(blockPos, this.provider.getBlockState(random, pos), 19);
				break;
			}

			if (!Feature.isAir(world, blockPos) && i < 0) {
				break;
			}
		}
	}
}
