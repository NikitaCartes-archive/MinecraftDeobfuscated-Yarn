package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableIntBoundingBox;
import net.minecraft.world.Heightmap;
import net.minecraft.world.ModifiableTestableWorld;

public class JungleGroundBushFeature extends AbstractTreeFeature<DefaultFeatureConfig> {
	private final BlockState leaves;
	private final BlockState log;

	public JungleGroundBushFeature(Function<Dynamic<?>, ? extends DefaultFeatureConfig> function, BlockState blockState, BlockState blockState2) {
		super(function, false);
		this.log = blockState;
		this.leaves = blockState2;
	}

	@Override
	public boolean generate(
		Set<BlockPos> set, ModifiableTestableWorld modifiableTestableWorld, Random random, BlockPos blockPos, MutableIntBoundingBox mutableIntBoundingBox
	) {
		blockPos = modifiableTestableWorld.getTopPosition(Heightmap.Type.field_13203, blockPos).down();
		if (isNaturalDirtOrGrass(modifiableTestableWorld, blockPos)) {
			blockPos = blockPos.up();
			this.setBlockState(set, modifiableTestableWorld, blockPos, this.log, mutableIntBoundingBox);

			for (int i = blockPos.getY(); i <= blockPos.getY() + 2; i++) {
				int j = i - blockPos.getY();
				int k = 2 - j;

				for (int l = blockPos.getX() - k; l <= blockPos.getX() + k; l++) {
					int m = l - blockPos.getX();

					for (int n = blockPos.getZ() - k; n <= blockPos.getZ() + k; n++) {
						int o = n - blockPos.getZ();
						if (Math.abs(m) != k || Math.abs(o) != k || random.nextInt(2) != 0) {
							BlockPos blockPos2 = new BlockPos(l, i, n);
							if (isAirOrLeaves(modifiableTestableWorld, blockPos2)) {
								this.setBlockState(set, modifiableTestableWorld, blockPos2, this.leaves, mutableIntBoundingBox);
							}
						}
					}
				}
			}
		}

		return true;
	}
}
