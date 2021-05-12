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
	private final WolfEntity wolf;
	private PlayerEntity begFrom;
	private final World world;
	private final float begDistance;
	private int timer;
	private final TargetPredicate validPlayerPredicate;

	public WolfBegGoal(WolfEntity wolf, float begDistance) {
		this.wolf = wolf;
		this.world = wolf.world;
		this.begDistance = begDistance;
		this.validPlayerPredicate = TargetPredicate.createNonAttackable().setBaseMaxDistance((double)begDistance);
		this.setControls(EnumSet.of(Goal.Control.LOOK));
	}

	@Override
	public boolean canStart() {
		this.begFrom = this.world.getClosestPlayer(this.validPlayerPredicate, this.wolf);
		return this.begFrom == null ? false : this.isAttractive(this.begFrom);
	}

	@Override
	public boolean shouldContinue() {
		if (!this.begFrom.isAlive()) {
			return false;
		} else {
			return this.wolf.squaredDistanceTo(this.begFrom) > (double)(this.begDistance * this.begDistance) ? false : this.timer > 0 && this.isAttractive(this.begFrom);
		}
	}

	@Override
	public void start() {
		this.wolf.setBegging(true);
		this.timer = 40 + this.wolf.getRandom().nextInt(40);
	}

	@Override
	public void stop() {
		this.wolf.setBegging(false);
		this.begFrom = null;
	}

	@Override
	public void tick() {
		this.wolf.getLookControl().lookAt(this.begFrom.getX(), this.begFrom.getEyeY(), this.begFrom.getZ(), 10.0F, (float)this.wolf.getLookPitchSpeed());
		this.timer--;
	}

	private boolean isAttractive(PlayerEntity player) {
		for (Hand hand : Hand.values()) {
			ItemStack itemStack = player.getStackInHand(hand);
			if (this.wolf.isTamed() && itemStack.isOf(Items.BONE)) {
				return true;
			}

			if (this.wolf.isBreedingItem(itemStack)) {
				return true;
			}
		}

		return false;
	}
}
