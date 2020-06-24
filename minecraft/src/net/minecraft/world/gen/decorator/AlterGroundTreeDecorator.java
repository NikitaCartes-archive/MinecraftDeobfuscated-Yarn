package net.minecraft.world.gen.decorator;

import com.mojang.serialization.Codec;
import java.util.List;
import java.util.Random;
import java.util.Set;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ModifiableTestableWorld;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.stateprovider.BlockStateProvider;

public class AlterGroundTreeDecorator extends TreeDecorator {
	public static final Codec<AlterGroundTreeDecorator> field_24957 = BlockStateProvider.CODEC
		.fieldOf("provider")
		.<AlterGroundTreeDecorator>xmap(AlterGroundTreeDecorator::new, alterGroundTreeDecorator -> alterGroundTreeDecorator.field_21316)
		.codec();
	private final BlockStateProvider field_21316;

	public AlterGroundTreeDecorator(BlockStateProvider blockStateProvider) {
		this.field_21316 = blockStateProvider;
	}

	@Override
	protected TreeDecoratorType<?> getType() {
		return TreeDecoratorType.ALTER_GROUND;
	}

	@Override
	public void generate(WorldAccess world, Random random, List<BlockPos> logPositions, List<BlockPos> leavesPositions, Set<BlockPos> set, BlockBox box) {
		int i = ((BlockPos)logPositions.get(0)).getY();
		logPositions.stream().filter(blockPos -> blockPos.getY() == i).forEach(blockPos -> {
			this.method_23462(world, random, blockPos.west().north());
			this.method_23462(world, random, blockPos.east(2).north());
			this.method_23462(world, random, blockPos.west().south(2));
			this.method_23462(world, random, blockPos.east(2).south(2));

			for (int ix = 0; ix < 5; ix++) {
				int j = random.nextInt(64);
				int k = j % 8;
				int l = j / 8;
				if (k == 0 || k == 7 || l == 0 || l == 7) {
					this.method_23462(world, random, blockPos.add(-3 + k, 0, -3 + l));
				}
			}
		});
	}

	private void method_23462(ModifiableTestableWorld modifiableTestableWorld, Random random, BlockPos blockPos) {
		for (int i = -2; i <= 2; i++) {
			for (int j = -2; j <= 2; j++) {
				if (Math.abs(i) != 2 || Math.abs(j) != 2) {
					this.method_23463(modifiableTestableWorld, random, blockPos.add(i, 0, j));
				}
			}
		}
	}

	private void method_23463(ModifiableTestableWorld modifiableTestableWorld, Random random, BlockPos blockPos) {
		for (int i = 2; i >= -3; i--) {
			BlockPos blockPos2 = blockPos.up(i);
			if (Feature.isSoil(modifiableTestableWorld, blockPos2)) {
				modifiableTestableWorld.setBlockState(blockPos2, this.field_21316.getBlockState(random, blockPos), 19);
				break;
			}

			if (!Feature.isAir(modifiableTestableWorld, blockPos2) && i < 0) {
				break;
			}
		}
	}
}
