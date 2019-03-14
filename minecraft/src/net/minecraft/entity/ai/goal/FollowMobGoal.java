package net.minecraft.entity.ai.goal;

import java.util.EnumSet;
import java.util.List;
import java.util.function.Predicate;
import net.minecraft.entity.ai.control.LookControl;
import net.minecraft.entity.ai.pathing.BirdNavigation;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.ai.pathing.MobNavigation;
import net.minecraft.entity.ai.pathing.PathNodeType;
import net.minecraft.entity.mob.MobEntity;

public class FollowMobGoal extends Goal {
	private final MobEntity field_6432;
	private final Predicate<MobEntity> field_6436;
	private MobEntity field_6433;
	private final double field_6430;
	private final EntityNavigation field_6434;
	private int field_6431;
	private final float field_6438;
	private float field_6437;
	private final float field_6435;

	public FollowMobGoal(MobEntity mobEntity, double d, float f, float g) {
		this.field_6432 = mobEntity;
		this.field_6436 = mobEntity2 -> mobEntity2 != null && mobEntity.getClass() != mobEntity2.getClass();
		this.field_6430 = d;
		this.field_6434 = mobEntity.getNavigation();
		this.field_6438 = f;
		this.field_6435 = g;
		this.setControlBits(EnumSet.of(Goal.ControlBit.field_18405, Goal.ControlBit.field_18406));
		if (!(mobEntity.getNavigation() instanceof MobNavigation) && !(mobEntity.getNavigation() instanceof BirdNavigation)) {
			throw new IllegalArgumentException("Unsupported mob type for FollowMobGoal");
		}
	}

	@Override
	public boolean canStart() {
		List<MobEntity> list = this.field_6432.world.getEntities(MobEntity.class, this.field_6432.getBoundingBox().expand((double)this.field_6435), this.field_6436);
		if (!list.isEmpty()) {
			for (MobEntity mobEntity : list) {
				if (!mobEntity.isInvisible()) {
					this.field_6433 = mobEntity;
					return true;
				}
			}
		}

		return false;
	}

	@Override
	public boolean shouldContinue() {
		return this.field_6433 != null
			&& !this.field_6434.isIdle()
			&& this.field_6432.squaredDistanceTo(this.field_6433) > (double)(this.field_6438 * this.field_6438);
	}

	@Override
	public void start() {
		this.field_6431 = 0;
		this.field_6437 = this.field_6432.getPathNodeTypeWeight(PathNodeType.field_18);
		this.field_6432.setPathNodeTypeWeight(PathNodeType.field_18, 0.0F);
	}

	@Override
	public void onRemove() {
		this.field_6433 = null;
		this.field_6434.stop();
		this.field_6432.setPathNodeTypeWeight(PathNodeType.field_18, this.field_6437);
	}

	@Override
	public void tick() {
		if (this.field_6433 != null && !this.field_6432.isLeashed()) {
			this.field_6432.getLookControl().lookAt(this.field_6433, 10.0F, (float)this.field_6432.method_5978());
			if (--this.field_6431 <= 0) {
				this.field_6431 = 10;
				double d = this.field_6432.x - this.field_6433.x;
				double e = this.field_6432.y - this.field_6433.y;
				double f = this.field_6432.z - this.field_6433.z;
				double g = d * d + e * e + f * f;
				if (!(g <= (double)(this.field_6438 * this.field_6438))) {
					this.field_6434.startMovingTo(this.field_6433, this.field_6430);
				} else {
					this.field_6434.stop();
					LookControl lookControl = this.field_6433.getLookControl();
					if (g <= (double)this.field_6438
						|| lookControl.getLookX() == this.field_6432.x && lookControl.getLookY() == this.field_6432.y && lookControl.getLookZ() == this.field_6432.z) {
						double h = this.field_6433.x - this.field_6432.x;
						double i = this.field_6433.z - this.field_6432.z;
						this.field_6434.startMovingTo(this.field_6432.x - h, this.field_6432.y, this.field_6432.z - i, this.field_6430);
					}
				}
			}
		}
	}
}
