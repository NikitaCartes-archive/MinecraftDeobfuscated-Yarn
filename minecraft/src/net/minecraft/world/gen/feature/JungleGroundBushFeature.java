package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;
import net.minecraft.class_3747;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.config.feature.DefaultFeatureConfig;

public class JungleGroundBushFeature extends TreeFeature<DefaultFeatureConfig> {
	private final BlockState field_13646;
	private final BlockState field_13647;

	public JungleGroundBushFeature(Function<Dynamic<?>, ? extends DefaultFeatureConfig> function, BlockState blockState, BlockState blockState2) {
		super(function, false);
		this.field_13647 = blockState;
		this.field_13646 = blockState2;
	}

	@Override
	public boolean method_12775(Set<BlockPos> set, class_3747 arg, Random random, BlockPos blockPos) {
		blockPos = arg.getTopPosition(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, blockPos);
		if (method_16430(arg, blockPos)) {
			blockPos = blockPos.up();
			this.method_12773(set, arg, blockPos, this.field_13647);

			for (int i = blockPos.getY(); i <= blockPos.getY() + 2; i++) {
				int j = i - blockPos.getY();
				int k = 2 - j;

				for (int l = blockPos.getX() - k; l <= blockPos.getX() + k; l++) {
					int m = l - blockPos.getX();

					for (int n = blockPos.getZ() - k; n <= blockPos.getZ() + k; n++) {
						int o = n - blockPos.getZ();
						if (Math.abs(m) != k || Math.abs(o) != k || random.nextInt(2) != 0) {
							BlockPos blockPos2 = new BlockPos(l, i, n);
							if (method_16420(arg, blockPos2)) {
								this.method_13153(arg, blockPos2, this.field_13646);
							}
						}
					}
				}
			}
		}

		return true;
	}
}
