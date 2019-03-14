package net.minecraft.entity.ai.goal;

import java.util.EnumSet;
import javax.annotation.Nullable;
import net.minecraft.entity.ai.AiUtil;
import net.minecraft.entity.mob.MobEntityWithAi;
import net.minecraft.util.math.Vec3d;

public class WanderAroundGoal extends Goal {
	protected final MobEntityWithAi owner;
	protected double targetX;
	protected double targetY;
	protected double targetZ;
	protected final double speed;
	protected int chance;
	protected boolean field_6565;

	public WanderAroundGoal(MobEntityWithAi mobEntityWithAi, double d) {
		this(mobEntityWithAi, d, 120);
	}

	public WanderAroundGoal(MobEntityWithAi mobEntityWithAi, double d, int i) {
		this.owner = mobEntityWithAi;
		this.speed = d;
		this.chance = i;
		this.setControlBits(EnumSet.of(Goal.ControlBit.field_18405));
	}

	@Override
	public boolean canStart() {
		if (this.owner.hasPassengers()) {
			return false;
		} else {
			if (!this.field_6565) {
				if (this.owner.getDespawnCounter() >= 100) {
					return false;
				}

				if (this.owner.getRand().nextInt(this.chance) != 0) {
					return false;
				}
			}

			Vec3d vec3d = this.getWanderTarget();
			if (vec3d == null) {
				return false;
			} else {
				this.targetX = vec3d.x;
				this.targetY = vec3d.y;
				this.targetZ = vec3d.z;
				this.field_6565 = false;
				return true;
			}
		}
	}

	@Nullable
	protected Vec3d getWanderTarget() {
		return AiUtil.method_6375(this.owner, 10, 7);
	}

	@Override
	public boolean shouldContinue() {
		return !this.owner.getNavigation().isIdle();
	}

	@Override
	public void start() {
		this.owner.getNavigation().startMovingTo(this.targetX, this.targetY, this.targetZ, this.speed);
	}

	public void method_6304() {
		this.field_6565 = true;
	}

	public void setChance(int i) {
		this.chance = i;
	}
}
