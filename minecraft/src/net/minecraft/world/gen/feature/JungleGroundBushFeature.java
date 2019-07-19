package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Heightmap;
import net.minecraft.world.ModifiableTestableWorld;

public class JungleGroundBushFeature extends AbstractTreeFeature<DefaultFeatureConfig> {
	private final BlockState leaves;
	private final BlockState log;

	public JungleGroundBushFeature(Function<Dynamic<?>, ? extends DefaultFeatureConfig> configFactory, BlockState log, BlockState leaves) {
		super(configFactory, false);
		this.log = log;
		this.leaves = leaves;
	}

	@Override
	public boolean generate(Set<BlockPos> logPositions, ModifiableTestableWorld world, Random random, BlockPos pos, BlockBox blockBox) {
		pos = world.getTopPosition(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, pos).down();
		if (isNaturalDirtOrGrass(world, pos)) {
			pos = pos.up();
			this.setBlockState(logPositions, world, pos, this.log, blockBox);

			for (int i = pos.getY(); i <= pos.getY() + 2; i++) {
				int j = i - pos.getY();
				int k = 2 - j;

				for (int l = pos.getX() - k; l <= pos.getX() + k; l++) {
					int m = l - pos.getX();

					for (int n = pos.getZ() - k; n <= pos.getZ() + k; n++) {
						int o = n - pos.getZ();
						if (Math.abs(m) != k || Math.abs(o) != k || random.nextInt(2) != 0) {
							BlockPos blockPos = new BlockPos(l, i, n);
							if (isAirOrLeaves(world, blockPos)) {
								this.setBlockState(logPositions, world, blockPos, this.leaves, blockBox);
							}
						}
					}
				}
			}
		}

		return true;
	}
}
