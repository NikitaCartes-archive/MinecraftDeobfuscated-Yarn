package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.ModifiableTestableWorld;

public class MegaPineTreeFeature extends MegaTreeFeature<DefaultFeatureConfig> {
	private static final BlockState LOG = Blocks.field_10037.method_9564();
	private static final BlockState LEAVES = Blocks.field_9988.method_9564();
	private static final BlockState PODZOL = Blocks.field_10520.method_9564();
	private final boolean field_13677;

	public MegaPineTreeFeature(Function<Dynamic<?>, ? extends DefaultFeatureConfig> function, boolean bl, boolean bl2) {
		super(function, bl, 13, 15, LOG, LEAVES);
		this.field_13677 = bl2;
	}

	@Override
	public boolean method_12775(Set<BlockPos> set, ModifiableTestableWorld modifiableTestableWorld, Random random, BlockPos blockPos) {
		int i = this.getHeight(random);
		if (!this.method_13523(modifiableTestableWorld, blockPos, i)) {
			return false;
		} else {
			this.makeTopLeaves(modifiableTestableWorld, blockPos.getX(), blockPos.getZ(), blockPos.getY() + i, 0, random);

			for (int j = 0; j < i; j++) {
				if (method_16420(modifiableTestableWorld, blockPos.up(j))) {
					this.method_12773(set, modifiableTestableWorld, blockPos.up(j), this.log);
				}

				if (j < i - 1) {
					if (method_16420(modifiableTestableWorld, blockPos.add(1, j, 0))) {
						this.method_12773(set, modifiableTestableWorld, blockPos.add(1, j, 0), this.log);
					}

					if (method_16420(modifiableTestableWorld, blockPos.add(1, j, 1))) {
						this.method_12773(set, modifiableTestableWorld, blockPos.add(1, j, 1), this.log);
					}

					if (method_16420(modifiableTestableWorld, blockPos.add(0, j, 1))) {
						this.method_12773(set, modifiableTestableWorld, blockPos.add(0, j, 1), this.log);
					}
				}
			}

			this.method_13494(modifiableTestableWorld, random, blockPos);
			return true;
		}
	}

	private void makeTopLeaves(ModifiableTestableWorld modifiableTestableWorld, int i, int j, int k, int l, Random random) {
		int m = random.nextInt(5) + (this.field_13677 ? this.baseHeight : 3);
		int n = 0;

		for (int o = k - m; o <= k; o++) {
			int p = k - o;
			int q = l + MathHelper.floor((float)p / (float)m * 3.5F);
			this.method_13528(modifiableTestableWorld, new BlockPos(i, o, j), q + (p > 0 && q == n && (o & 1) == 0 ? 1 : 0));
			n = q;
		}
	}

	public void method_13494(ModifiableTestableWorld modifiableTestableWorld, Random random, BlockPos blockPos) {
		this.method_13496(modifiableTestableWorld, blockPos.west().north());
		this.method_13496(modifiableTestableWorld, blockPos.east(2).north());
		this.method_13496(modifiableTestableWorld, blockPos.west().south(2));
		this.method_13496(modifiableTestableWorld, blockPos.east(2).south(2));

		for (int i = 0; i < 5; i++) {
			int j = random.nextInt(64);
			int k = j % 8;
			int l = j / 8;
			if (k == 0 || k == 7 || l == 0 || l == 7) {
				this.method_13496(modifiableTestableWorld, blockPos.add(-3 + k, 0, -3 + l));
			}
		}
	}

	private void method_13496(ModifiableTestableWorld modifiableTestableWorld, BlockPos blockPos) {
		for (int i = -2; i <= 2; i++) {
			for (int j = -2; j <= 2; j++) {
				if (Math.abs(i) != 2 || Math.abs(j) != 2) {
					this.method_13493(modifiableTestableWorld, blockPos.add(i, 0, j));
				}
			}
		}
	}

	private void method_13493(ModifiableTestableWorld modifiableTestableWorld, BlockPos blockPos) {
		for (int i = 2; i >= -3; i--) {
			BlockPos blockPos2 = blockPos.up(i);
			if (method_16430(modifiableTestableWorld, blockPos2)) {
				this.method_13153(modifiableTestableWorld, blockPos2, PODZOL);
				break;
			}

			if (!method_16424(modifiableTestableWorld, blockPos2) && i < 0) {
				break;
			}
		}
	}
}
