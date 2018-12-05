package net.minecraft;

import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.mob.MobEntityWithAi;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.BlockView;

public class class_1374 extends Goal {
	protected final MobEntityWithAi field_6549;
	protected double field_6548;
	protected double field_6547;
	protected double field_6546;
	protected double field_6550;

	public class_1374(MobEntityWithAi mobEntityWithAi, double d) {
		this.field_6549 = mobEntityWithAi;
		this.field_6548 = d;
		this.setControlBits(1);
	}

	@Override
	public boolean canStart() {
		if (this.field_6549.getAttacker() == null && !this.field_6549.isOnFire()) {
			return false;
		} else {
			if (this.field_6549.isOnFire()) {
				BlockPos blockPos = this.method_6300(this.field_6549.world, this.field_6549, 5, 4);
				if (blockPos != null) {
					this.field_6547 = (double)blockPos.getX();
					this.field_6546 = (double)blockPos.getY();
					this.field_6550 = (double)blockPos.getZ();
					return true;
				}
			}

			return this.method_6301();
		}
	}

	protected boolean method_6301() {
		Vec3d vec3d = class_1414.method_6375(this.field_6549, 5, 4);
		if (vec3d == null) {
			return false;
		} else {
			this.field_6547 = vec3d.x;
			this.field_6546 = vec3d.y;
			this.field_6550 = vec3d.z;
			return true;
		}
	}

	@Override
	public void start() {
		this.field_6549.getNavigation().method_6337(this.field_6547, this.field_6546, this.field_6550, this.field_6548);
	}

	@Override
	public boolean shouldContinue() {
		return !this.field_6549.getNavigation().method_6357();
	}

	@Nullable
	protected BlockPos method_6300(BlockView blockView, Entity entity, int i, int j) {
		BlockPos blockPos = new BlockPos(entity);
		int k = blockPos.getX();
		int l = blockPos.getY();
		int m = blockPos.getZ();
		float f = (float)(i * i * j * 2);
		BlockPos blockPos2 = null;
		BlockPos.Mutable mutable = new BlockPos.Mutable();

		for (int n = k - i; n <= k + i; n++) {
			for (int o = l - j; o <= l + j; o++) {
				for (int p = m - i; p <= m + i; p++) {
					mutable.set(n, o, p);
					if (blockView.getFluidState(mutable).matches(FluidTags.field_15517)) {
						float g = (float)((n - k) * (n - k) + (o - l) * (o - l) + (p - m) * (p - m));
						if (g < f) {
							f = g;
							blockPos2 = new BlockPos(mutable);
						}
					}
				}
			}
		}

		return blockPos2;
	}
}
