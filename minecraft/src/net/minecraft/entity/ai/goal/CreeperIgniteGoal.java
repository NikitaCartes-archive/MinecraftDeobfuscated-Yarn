package net.minecraft.entity.ai.goal;

import java.util.EnumSet;
import javax.annotation.Nullable;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.item.Items;

public class CreeperIgniteGoal extends Goal {
	private final CreeperEntity creeper;
	@Nullable
	private LivingEntity target;

	public CreeperIgniteGoal(CreeperEntity creeper) {
		this.creeper = creeper;
		this.setControls(EnumSet.of(Goal.Control.MOVE));
	}

	@Override
	public boolean canStart() {
		LivingEntity livingEntity = this.creeper.getTarget();
		return livingEntity != null && livingEntity.isInSneakingPose() && livingEntity.getEquippedStack(EquipmentSlot.HEAD).isOf(Items.BARREL)
			? false
			: this.creeper.getFuseSpeed() > 0 || livingEntity != null && this.creeper.squaredDistanceTo(livingEntity) < 9.0;
	}

	@Override
	public void start() {
		this.creeper.getNavigation().stop();
		this.target = this.creeper.getTarget();
	}

	@Override
	public void stop() {
		this.target = null;
	}

	@Override
	public boolean shouldRunEveryTick() {
		return true;
	}

	@Override
	public void tick() {
		if (!this.creeper.field_38527) {
			if (this.target == null) {
				this.creeper.setFuseSpeed(-1);
				return;
			}

			if (this.target.isInSneakingPose() && this.target.getEquippedStack(EquipmentSlot.HEAD).isOf(Items.BARREL)) {
				this.creeper.setFuseSpeed(-1);
				return;
			}

			if (this.creeper.squaredDistanceTo(this.target) > 49.0) {
				this.creeper.setFuseSpeed(-1);
				return;
			}

			if (!this.creeper.getVisibilityCache().canSee(this.target)) {
				this.creeper.setFuseSpeed(-1);
				return;
			}
		}

		this.creeper.setFuseSpeed(1);
	}
}
