package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.VineBlock;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ModifiableTestableWorld;
import net.minecraft.world.gen.Heightmap;

public class SwampTreeFeature extends AbstractTreeFeature<DefaultFeatureConfig> {
	private static final BlockState LOG = Blocks.field_10431.method_9564();
	private static final BlockState LEAVES = Blocks.field_10503.method_9564();

	public SwampTreeFeature(Function<Dynamic<?>, ? extends DefaultFeatureConfig> function) {
		super(function, false);
	}

	@Override
	public boolean method_12775(Set<BlockPos> set, ModifiableTestableWorld modifiableTestableWorld, Random random, BlockPos blockPos) {
		int i = random.nextInt(4) + 5;
		blockPos = modifiableTestableWorld.method_8598(Heightmap.Type.OCEAN_FLOOR, blockPos);
		boolean bl = true;
		if (blockPos.getY() >= 1 && blockPos.getY() + i + 1 <= 256) {
			for (int j = blockPos.getY(); j <= blockPos.getY() + 1 + i; j++) {
				int k = 1;
				if (j == blockPos.getY()) {
					k = 0;
				}

				if (j >= blockPos.getY() + 1 + i - 2) {
					k = 3;
				}

				BlockPos.Mutable mutable = new BlockPos.Mutable();

				for (int l = blockPos.getX() - k; l <= blockPos.getX() + k && bl; l++) {
					for (int m = blockPos.getZ() - k; m <= blockPos.getZ() + k && bl; m++) {
						if (j >= 0 && j < 256) {
							mutable.set(l, j, m);
							if (!method_16420(modifiableTestableWorld, mutable)) {
								if (method_16422(modifiableTestableWorld, mutable)) {
									if (j > blockPos.getY()) {
										bl = false;
									}
								} else {
									bl = false;
								}
							}
						} else {
							bl = false;
						}
					}
				}
			}

			if (!bl) {
				return false;
			} else if (method_16430(modifiableTestableWorld, blockPos.down()) && blockPos.getY() < 256 - i - 1) {
				this.method_16427(modifiableTestableWorld, blockPos.down());

				for (int j = blockPos.getY() - 3 + i; j <= blockPos.getY() + i; j++) {
					int kx = j - (blockPos.getY() + i);
					int n = 2 - kx / 2;

					for (int l = blockPos.getX() - n; l <= blockPos.getX() + n; l++) {
						int mx = l - blockPos.getX();

						for (int o = blockPos.getZ() - n; o <= blockPos.getZ() + n; o++) {
							int p = o - blockPos.getZ();
							if (Math.abs(mx) != n || Math.abs(p) != n || random.nextInt(2) != 0 && kx != 0) {
								BlockPos blockPos2 = new BlockPos(l, j, o);
								if (method_16420(modifiableTestableWorld, blockPos2) || method_16425(modifiableTestableWorld, blockPos2)) {
									this.method_13153(modifiableTestableWorld, blockPos2, LEAVES);
								}
							}
						}
					}
				}

				for (int j = 0; j < i; j++) {
					BlockPos blockPos3 = blockPos.up(j);
					if (method_16420(modifiableTestableWorld, blockPos3) || method_16422(modifiableTestableWorld, blockPos3)) {
						this.method_12773(set, modifiableTestableWorld, blockPos3, LOG);
					}
				}

				for (int jx = blockPos.getY() - 3 + i; jx <= blockPos.getY() + i; jx++) {
					int kx = jx - (blockPos.getY() + i);
					int n = 2 - kx / 2;
					BlockPos.Mutable mutable2 = new BlockPos.Mutable();

					for (int mx = blockPos.getX() - n; mx <= blockPos.getX() + n; mx++) {
						for (int ox = blockPos.getZ() - n; ox <= blockPos.getZ() + n; ox++) {
							mutable2.set(mx, jx, ox);
							if (method_16416(modifiableTestableWorld, mutable2)) {
								BlockPos blockPos4 = mutable2.west();
								BlockPos blockPos2 = mutable2.east();
								BlockPos blockPos5 = mutable2.north();
								BlockPos blockPos6 = mutable2.south();
								if (random.nextInt(4) == 0 && method_16424(modifiableTestableWorld, blockPos4)) {
									this.method_14030(modifiableTestableWorld, blockPos4, VineBlock.field_11702);
								}

								if (random.nextInt(4) == 0 && method_16424(modifiableTestableWorld, blockPos2)) {
									this.method_14030(modifiableTestableWorld, blockPos2, VineBlock.field_11696);
								}

								if (random.nextInt(4) == 0 && method_16424(modifiableTestableWorld, blockPos5)) {
									this.method_14030(modifiableTestableWorld, blockPos5, VineBlock.field_11699);
								}

								if (random.nextInt(4) == 0 && method_16424(modifiableTestableWorld, blockPos6)) {
									this.method_14030(modifiableTestableWorld, blockPos6, VineBlock.field_11706);
								}
							}
						}
					}
				}

				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	private void method_14030(ModifiableTestableWorld modifiableTestableWorld, BlockPos blockPos, BooleanProperty booleanProperty) {
		BlockState blockState = Blocks.field_10597.method_9564().method_11657(booleanProperty, Boolean.valueOf(true));
		this.method_13153(modifiableTestableWorld, blockPos, blockState);
		int i = 4;

		for (BlockPos var6 = blockPos.down(); method_16424(modifiableTestableWorld, var6) && i > 0; i--) {
			this.method_13153(modifiableTestableWorld, var6, blockState);
			var6 = var6.down();
		}
	}
}
