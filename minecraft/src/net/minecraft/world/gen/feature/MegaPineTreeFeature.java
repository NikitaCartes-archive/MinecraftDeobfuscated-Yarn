package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.ModifiableTestableWorld;

public class MegaPineTreeFeature extends MegaTreeFeature<DefaultFeatureConfig> {
	private static final BlockState LOG = Blocks.SPRUCE_LOG.getDefaultState();
	private static final BlockState LEAVES = Blocks.SPRUCE_LEAVES.getDefaultState();
	private static final BlockState PODZOL = Blocks.PODZOL.getDefaultState();
	private final boolean field_13677;

	public MegaPineTreeFeature(Function<Dynamic<?>, ? extends DefaultFeatureConfig> function, boolean bl, boolean bl2) {
		super(function, bl, 13, 15, LOG, LEAVES);
		this.field_13677 = bl2;
	}

	@Override
	public boolean generate(Set<BlockPos> logPositions, ModifiableTestableWorld world, Random random, BlockPos pos, BlockBox blockBox) {
		int i = this.getHeight(random);
		if (!this.checkTreeFitsAndReplaceGround(world, pos, i)) {
			return false;
		} else {
			this.makeTopLeaves(world, pos.getX(), pos.getZ(), pos.getY() + i, 0, random, blockBox, logPositions);

			for (int j = 0; j < i; j++) {
				if (isAirOrLeaves(world, pos.up(j))) {
					this.setBlockState(logPositions, world, pos.up(j), this.log, blockBox);
				}

				if (j < i - 1) {
					if (isAirOrLeaves(world, pos.add(1, j, 0))) {
						this.setBlockState(logPositions, world, pos.add(1, j, 0), this.log, blockBox);
					}

					if (isAirOrLeaves(world, pos.add(1, j, 1))) {
						this.setBlockState(logPositions, world, pos.add(1, j, 1), this.log, blockBox);
					}

					if (isAirOrLeaves(world, pos.add(0, j, 1))) {
						this.setBlockState(logPositions, world, pos.add(0, j, 1), this.log, blockBox);
					}
				}
			}

			this.replaceGround(world, random, pos);
			return true;
		}
	}

	private void makeTopLeaves(ModifiableTestableWorld modifiableTestableWorld, int i, int j, int k, int l, Random random, BlockBox blockBox, Set<BlockPos> set) {
		int m = random.nextInt(5) + (this.field_13677 ? this.baseHeight : 3);
		int n = 0;

		for (int o = k - m; o <= k; o++) {
			int p = k - o;
			int q = l + MathHelper.floor((float)p / (float)m * 3.5F);
			this.makeSquaredLeafLayer(modifiableTestableWorld, new BlockPos(i, o, j), q + (p > 0 && q == n && (o & 1) == 0 ? 1 : 0), blockBox, set);
			n = q;
		}
	}

	public void replaceGround(ModifiableTestableWorld world, Random random, BlockPos pos) {
		this.replaceGroundNear(world, pos.west().north());
		this.replaceGroundNear(world, pos.east(2).north());
		this.replaceGroundNear(world, pos.west().south(2));
		this.replaceGroundNear(world, pos.east(2).south(2));

		for (int i = 0; i < 5; i++) {
			int j = random.nextInt(64);
			int k = j % 8;
			int l = j / 8;
			if (k == 0 || k == 7 || l == 0 || l == 7) {
				this.replaceGroundNear(world, pos.add(-3 + k, 0, -3 + l));
			}
		}
	}

	private void replaceGroundNear(ModifiableTestableWorld modifiableTestableWorld, BlockPos blockPos) {
		for (int i = -2; i <= 2; i++) {
			for (int j = -2; j <= 2; j++) {
				if (Math.abs(i) != 2 || Math.abs(j) != 2) {
					this.prepareGroundColumn(modifiableTestableWorld, blockPos.add(i, 0, j));
				}
			}
		}
	}

	private void prepareGroundColumn(ModifiableTestableWorld modifiableTestableWorld, BlockPos blockPos) {
		for (int i = 2; i >= -3; i--) {
			BlockPos blockPos2 = blockPos.up(i);
			if (isNaturalDirtOrGrass(modifiableTestableWorld, blockPos2)) {
				this.setBlockState(modifiableTestableWorld, blockPos2, PODZOL);
				break;
			}

			if (!isAir(modifiableTestableWorld, blockPos2) && i < 0) {
				break;
			}
		}
	}
}
