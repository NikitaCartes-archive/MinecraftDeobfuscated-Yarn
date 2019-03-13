package net.minecraft.entity.ai.goal;

import java.util.EnumSet;
import javax.annotation.Nullable;
import net.minecraft.class_1414;
import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.MobEntityWithAi;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.BlockView;

public class EscapeDangerGoal extends Goal {
	protected final MobEntityWithAi owner;
	protected final double speed;
	protected double targetX;
	protected double targetY;
	protected double targetZ;

	public EscapeDangerGoal(MobEntityWithAi mobEntityWithAi, double d) {
		this.owner = mobEntityWithAi;
		this.speed = d;
		this.setControlBits(EnumSet.of(Goal.class_4134.field_18405));
	}

	@Override
	public boolean canStart() {
		if (this.owner.getAttacker() == null && !this.owner.isOnFire()) {
			return false;
		} else {
			if (this.owner.isOnFire()) {
				BlockPos blockPos = this.method_6300(this.owner.field_6002, this.owner, 5, 4);
				if (blockPos != null) {
					this.targetX = (double)blockPos.getX();
					this.targetY = (double)blockPos.getY();
					this.targetZ = (double)blockPos.getZ();
					return true;
				}
			}

			return this.method_6301();
		}
	}

	protected boolean method_6301() {
		Vec3d vec3d = class_1414.method_6375(this.owner, 5, 4);
		if (vec3d == null) {
			return false;
		} else {
			this.targetX = vec3d.x;
			this.targetY = vec3d.y;
			this.targetZ = vec3d.z;
			return true;
		}
	}

	@Override
	public void start() {
		this.owner.method_5942().startMovingTo(this.targetX, this.targetY, this.targetZ, this.speed);
	}

	@Override
	public boolean shouldContinue() {
		return !this.owner.method_5942().isIdle();
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
					if (blockView.method_8316(mutable).method_15767(FluidTags.field_15517)) {
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
