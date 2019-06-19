package net.minecraft.entity.ai.goal;

import java.util.EnumSet;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.ai.pathing.MobNavigation;
import net.minecraft.entity.mob.MobEntityWithAi;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;

public class TemptGoal extends Goal {
	private static final TargetPredicate TEMPTING_ENTITY_PREDICATE = new TargetPredicate()
		.setBaseMaxDistance(10.0)
		.includeInvulnerable()
		.includeTeammates()
		.ignoreEntityTargetRules()
		.includeHidden();
	protected final MobEntityWithAi mob;
	private final double speed;
	private double lastPlayerX;
	private double lastPlayerY;
	private double lastPlayerZ;
	private double lastPlayerPitch;
	private double lastPlayerYaw;
	protected PlayerEntity closestPlayer;
	private int cooldown;
	private boolean active;
	private final Ingredient food;
	private final boolean canBeScared;

	public TemptGoal(MobEntityWithAi mobEntityWithAi, double d, Ingredient ingredient, boolean bl) {
		this(mobEntityWithAi, d, bl, ingredient);
	}

	public TemptGoal(MobEntityWithAi mobEntityWithAi, double d, boolean bl, Ingredient ingredient) {
		this.mob = mobEntityWithAi;
		this.speed = d;
		this.food = ingredient;
		this.canBeScared = bl;
		this.setControls(EnumSet.of(Goal.Control.field_18405, Goal.Control.field_18406));
		if (!(mobEntityWithAi.getNavigation() instanceof MobNavigation)) {
			throw new IllegalArgumentException("Unsupported mob type for TemptGoal");
		}
	}

	@Override
	public boolean canStart() {
		if (this.cooldown > 0) {
			this.cooldown--;
			return false;
		} else {
			this.closestPlayer = this.mob.world.getClosestPlayer(TEMPTING_ENTITY_PREDICATE, this.mob);
			return this.closestPlayer == null ? false : this.isTempedBy(this.closestPlayer.getMainHandStack()) || this.isTempedBy(this.closestPlayer.getOffHandStack());
		}
	}

	protected boolean isTempedBy(ItemStack itemStack) {
		return this.food.method_8093(itemStack);
	}

	@Override
	public boolean shouldContinue() {
		if (this.canBeScared()) {
			if (this.mob.squaredDistanceTo(this.closestPlayer) < 36.0) {
				if (this.closestPlayer.squaredDistanceTo(this.lastPlayerX, this.lastPlayerY, this.lastPlayerZ) > 0.010000000000000002) {
					return false;
				}

				if (Math.abs((double)this.closestPlayer.pitch - this.lastPlayerPitch) > 5.0 || Math.abs((double)this.closestPlayer.yaw - this.lastPlayerYaw) > 5.0) {
					return false;
				}
			} else {
				this.lastPlayerX = this.closestPlayer.x;
				this.lastPlayerY = this.closestPlayer.y;
				this.lastPlayerZ = this.closestPlayer.z;
			}

			this.lastPlayerPitch = (double)this.closestPlayer.pitch;
			this.lastPlayerYaw = (double)this.closestPlayer.yaw;
		}

		return this.canStart();
	}

	protected boolean canBeScared() {
		return this.canBeScared;
	}

	@Override
	public void start() {
		this.lastPlayerX = this.closestPlayer.x;
		this.lastPlayerY = this.closestPlayer.y;
		this.lastPlayerZ = this.closestPlayer.z;
		this.active = true;
	}

	@Override
	public void stop() {
		this.closestPlayer = null;
		this.mob.getNavigation().stop();
		this.cooldown = 100;
		this.active = false;
	}

	@Override
	public void tick() {
		this.mob.getLookControl().lookAt(this.closestPlayer, (float)(this.mob.method_5986() + 20), (float)this.mob.getLookPitchSpeed());
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
