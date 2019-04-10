package net.minecraft.entity.ai.goal;

import java.util.EnumSet;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

public class WolfBegGoal extends Goal {
	private final WolfEntity owner;
	private PlayerEntity begFrom;
	private final World world;
	private final float begDistance;
	private int timer;
	private final TargetPredicate validPlayerPredicate;

	public WolfBegGoal(WolfEntity wolfEntity, float f) {
		this.owner = wolfEntity;
		this.world = wolfEntity.world;
		this.begDistance = f;
		this.validPlayerPredicate = new TargetPredicate().setBaseMaxDistance((double)f).includeInvulnerable().includeTeammates().ignoreEntityTargetRules();
		this.setControls(EnumSet.of(Goal.Control.field_18406));
	}

	@Override
	public boolean canStart() {
		this.begFrom = this.world.getClosestPlayer(this.validPlayerPredicate, this.owner);
		return this.begFrom == null ? false : this.isAttractive(this.begFrom);
	}

	@Override
	public boolean shouldContinue() {
		if (!this.begFrom.isAlive()) {
			return false;
		} else {
			return this.owner.squaredDistanceTo(this.begFrom) > (double)(this.begDistance * this.begDistance)
				? false
				: this.timer > 0 && this.isAttractive(this.begFrom);
		}
	}

	@Override
	public void start() {
		this.owner.setBegging(true);
		this.timer = 40 + this.owner.getRand().nextInt(40);
	}

	@Override
	public void stop() {
		this.owner.setBegging(false);
		this.begFrom = null;
	}

	@Override
	public void tick() {
		this.owner
			.getLookControl()
			.lookAt(this.begFrom.x, this.begFrom.y + (double)this.begFrom.getStandingEyeHeight(), this.begFrom.z, 10.0F, (float)this.owner.getLookPitchSpeed());
		this.timer--;
	}

	private boolean isAttractive(PlayerEntity playerEntity) {
		for (Hand hand : Hand.values()) {
			ItemStack itemStack = playerEntity.getStackInHand(hand);
			if (this.owner.isTamed() && itemStack.getItem() == Items.field_8606) {
				return true;
			}

			if (this.owner.isBreedingItem(itemStack)) {
				return true;
			}
		}

		return false;
	}
}
