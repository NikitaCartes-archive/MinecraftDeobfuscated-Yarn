package net.minecraft;

import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.mob.MobEntityWithAi;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ViewableWorld;

public abstract class class_1367 extends Goal {
	private final MobEntityWithAi field_6516;
	public double field_6514;
	protected int field_6518;
	protected int field_6517;
	private int field_6511;
	protected BlockPos field_6512 = BlockPos.ORIGIN;
	private boolean field_6513;
	private final int field_6510;
	private final int field_6519;
	public int field_6515;

	public class_1367(MobEntityWithAi mobEntityWithAi, double d, int i) {
		this(mobEntityWithAi, d, i, 1);
	}

	public class_1367(MobEntityWithAi mobEntityWithAi, double d, int i, int j) {
		this.field_6516 = mobEntityWithAi;
		this.field_6514 = d;
		this.field_6510 = i;
		this.field_6515 = 0;
		this.field_6519 = j;
		this.setControlBits(5);
	}

	@Override
	public boolean canStart() {
		if (this.field_6518 > 0) {
			this.field_6518--;
			return false;
		} else {
			this.field_6518 = this.method_6293(this.field_6516);
			return this.method_6292();
		}
	}

	protected int method_6293(MobEntityWithAi mobEntityWithAi) {
		return 200 + mobEntityWithAi.getRand().nextInt(200);
	}

	@Override
	public boolean shouldContinue() {
		return this.field_6517 >= -this.field_6511 && this.field_6517 <= 1200 && this.method_6296(this.field_6516.world, this.field_6512);
	}

	@Override
	public void start() {
		this.method_6290();
		this.field_6517 = 0;
		this.field_6511 = this.field_6516.getRand().nextInt(this.field_6516.getRand().nextInt(1200) + 1200) + 1200;
	}

	protected void method_6290() {
		this.field_6516
			.getNavigation()
			.method_6337(
				(double)((float)this.field_6512.getX()) + 0.5, (double)(this.field_6512.getY() + 1), (double)((float)this.field_6512.getZ()) + 0.5, this.field_6514
			);
	}

	public double method_6291() {
		return 1.0;
	}

	@Override
	public void tick() {
		if (this.field_6516.squaredDistanceToCenter(this.field_6512.up()) > this.method_6291()) {
			this.field_6513 = false;
			this.field_6517++;
			if (this.method_6294()) {
				this.field_6516
					.getNavigation()
					.method_6337(
						(double)((float)this.field_6512.getX()) + 0.5, (double)(this.field_6512.getY() + 1), (double)((float)this.field_6512.getZ()) + 0.5, this.field_6514
					);
			}
		} else {
			this.field_6513 = true;
			this.field_6517--;
		}
	}

	public boolean method_6294() {
		return this.field_6517 % 40 == 0;
	}

	protected boolean method_6295() {
		return this.field_6513;
	}

	protected boolean method_6292() {
		int i = this.field_6510;
		int j = this.field_6519;
		BlockPos blockPos = new BlockPos(this.field_6516);
		BlockPos.Mutable mutable = new BlockPos.Mutable();

		for (int k = this.field_6515; k <= j; k = k > 0 ? -k : 1 - k) {
			for (int l = 0; l < i; l++) {
				for (int m = 0; m <= l; m = m > 0 ? -m : 1 - m) {
					for (int n = m < l && m > -l ? l : 0; n <= l; n = n > 0 ? -n : 1 - n) {
						mutable.set(blockPos).method_10100(m, k - 1, n);
						if (this.field_6516.isInAiRange(mutable) && this.method_6296(this.field_6516.world, mutable)) {
							this.field_6512 = mutable;
							return true;
						}
					}
				}
			}
		}

		return false;
	}

	protected abstract boolean method_6296(ViewableWorld viewableWorld, BlockPos blockPos);
}
