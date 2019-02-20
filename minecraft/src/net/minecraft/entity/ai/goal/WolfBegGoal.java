package net.minecraft.entity.ai.goal;

import net.minecraft.class_4051;
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
	private final float chance;
	private int timer;
	private final class_4051 field_18085;

	public WolfBegGoal(WolfEntity wolfEntity, float f) {
		this.owner = wolfEntity;
		this.world = wolfEntity.world;
		this.chance = f;
		this.field_18085 = new class_4051().method_18418((double)f).method_18417().method_18421().method_18423();
		this.setControlBits(2);
	}

	@Override
	public boolean canStart() {
		this.begFrom = this.world.method_18462(this.field_18085, this.owner);
		return this.begFrom == null ? false : this.method_6244(this.begFrom);
	}

	@Override
	public boolean shouldContinue() {
		if (!this.begFrom.isValid()) {
			return false;
		} else {
			return this.owner.squaredDistanceTo(this.begFrom) > (double)(this.chance * this.chance) ? false : this.timer > 0 && this.method_6244(this.begFrom);
		}
	}

	@Override
	public void start() {
		this.owner.method_6712(true);
		this.timer = 40 + this.owner.getRand().nextInt(40);
	}

	@Override
	public void onRemove() {
		this.owner.method_6712(false);
		this.begFrom = null;
	}

	@Override
	public void tick() {
		this.owner
			.getLookControl()
			.lookAt(this.begFrom.x, this.begFrom.y + (double)this.begFrom.getEyeHeight(), this.begFrom.z, 10.0F, (float)this.owner.method_5978());
		this.timer--;
	}

	private boolean method_6244(PlayerEntity playerEntity) {
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
