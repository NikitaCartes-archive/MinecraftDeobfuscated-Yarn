package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Heightmap;
import net.minecraft.world.ModifiableTestableWorld;

public class JungleGroundBushFeature extends AbstractTreeFeature<TreeFeatureConfig> {
	public JungleGroundBushFeature(Function<Dynamic<?>, ? extends TreeFeatureConfig> function) {
		super(function);
	}

	@Override
	public boolean generate(
		ModifiableTestableWorld world, Random random, BlockPos pos, Set<BlockPos> logPositions, Set<BlockPos> leavesPositions, BlockBox box, TreeFeatureConfig config
	) {
		pos = world.getTopPosition(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, pos).down();
		if (isNaturalDirtOrGrass(world, pos)) {
			pos = pos.up();
			setLogBlockState(world, random, pos, logPositions, box, config);

			for (int i = 0; i <= 2; i++) {
				int j = 2 - i;

				for (int k = -j; k <= j; k++) {
					for (int l = -j; l <= j; l++) {
						if (Math.abs(k) != j || Math.abs(l) != j || random.nextInt(2) != 0) {
							this.setLeavesBlockState(world, random, new BlockPos(k + pos.getX(), i + pos.getY(), l + pos.getZ()), leavesPositions, box, config);
						}
					}
				}
			}
		}

		return true;
	}
}
