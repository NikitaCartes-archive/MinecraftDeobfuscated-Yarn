package net.minecraft.entity.ai.goal;

import java.util.EnumSet;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

public class TemptGoal extends Goal {
	private static final TargetPredicate TEMPTING_ENTITY_PREDICATE = TargetPredicate.createNonAttackable().ignoreVisibility();
	private final TargetPredicate predicate;
	protected final PathAwareEntity mob;
	private final double speed;
	private double lastPlayerX;
	private double lastPlayerY;
	private double lastPlayerZ;
	private double lastPlayerPitch;
	private double lastPlayerYaw;
	@Nullable
	protected PlayerEntity closestPlayer;
	private int cooldown;
	private boolean active;
	private final Predicate<ItemStack> foodPredicate;
	private final boolean canBeScared;

	public TemptGoal(PathAwareEntity entity, double speed, Predicate<ItemStack> foodPredicate, boolean canBeScared) {
		this.mob = entity;
		this.speed = speed;
		this.foodPredicate = foodPredicate;
		this.canBeScared = canBeScared;
		this.setControls(EnumSet.of(Goal.Control.MOVE, Goal.Control.LOOK));
		this.predicate = TEMPTING_ENTITY_PREDICATE.copy().setPredicate((entityx, world) -> this.isTemptedBy(entityx));
	}

	@Override
	public boolean canStart() {
		if (this.cooldown > 0) {
			this.cooldown--;
			return false;
		} else {
			this.closestPlayer = getServerWorld(this.mob)
				.getClosestPlayer(this.predicate.setBaseMaxDistance(this.mob.getAttributeValue(EntityAttributes.TEMPT_RANGE)), this.mob);
			return this.closestPlayer != null;
		}
	}

	private boolean isTemptedBy(LivingEntity entity) {
		return this.foodPredicate.test(entity.getMainHandStack()) || this.foodPredicate.test(entity.getOffHandStack());
	}

	@Override
	public boolean shouldContinue() {
		if (this.canBeScared()) {
			if (this.mob.squaredDistanceTo(this.closestPlayer) < 36.0) {
				if (this.closestPlayer.squaredDistanceTo(this.lastPlayerX, this.lastPlayerY, this.lastPlayerZ) > 0.010000000000000002) {
					return false;
				}

				if (Math.abs((double)this.closestPlayer.getPitch() - this.lastPlayerPitch) > 5.0
					|| Math.abs((double)this.closestPlayer.getYaw() - this.lastPlayerYaw) > 5.0) {
					return false;
				}
			} else {
				this.lastPlayerX = this.closestPlayer.getX();
				this.lastPlayerY = this.closestPlayer.getY();
				this.lastPlayerZ = this.closestPlayer.getZ();
			}

			this.lastPlayerPitch = (double)this.closestPlayer.getPitch();
			this.lastPlayerYaw = (double)this.closestPlayer.getYaw();
		}

		return this.canStart();
	}

	protected boolean canBeScared() {
		return this.canBeScared;
	}

	@Override
	public void start() {
		this.lastPlayerX = this.closestPlayer.getX();
		this.lastPlayerY = this.closestPlayer.getY();
		this.lastPlayerZ = this.closestPlayer.getZ();
		this.active = true;
	}

	@Override
	public void stop() {
		this.closestPlayer = null;
		this.mob.getNavigation().stop();
		this.cooldown = toGoalTicks(100);
		this.active = false;
	}

	@Override
	public void tick() {
		this.mob.getLookControl().lookAt(this.closestPlayer, (float)(this.mob.getMaxHeadRotation() + 20), (float)this.mob.getMaxLookPitchChange());
		if (this.mob.squaredDistanceTo(this.closestPlayer) < 6.25) {
			this.mob.getNavigation().stop();
		} else {
			this.mob.getNavigation().startMovingTo(this.closestPlayer, this.speed);
		}
	}

	public boolean isActive() {
		return this.active;
	}
}
