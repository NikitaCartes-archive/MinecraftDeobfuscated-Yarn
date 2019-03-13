package net.minecraft.entity.ai.goal;

import java.util.EnumSet;
import net.minecraft.class_4051;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

public class WolfBegGoal extends Goal {
	private final WolfEntity field_6384;
	private PlayerEntity field_6383;
	private final World field_6381;
	private final float chance;
	private int timer;
	private final class_4051 field_18085;

	public WolfBegGoal(WolfEntity wolfEntity, float f) {
		this.field_6384 = wolfEntity;
		this.field_6381 = wolfEntity.field_6002;
		this.chance = f;
		this.field_18085 = new class_4051().method_18418((double)f).method_18417().method_18421().method_18423();
		this.setControlBits(EnumSet.of(Goal.class_4134.field_18406));
	}

	@Override
	public boolean canStart() {
		this.field_6383 = this.field_6381.method_18462(this.field_18085, this.field_6384);
		return this.field_6383 == null ? false : this.method_6244(this.field_6383);
	}

	@Override
	public boolean shouldContinue() {
		if (!this.field_6383.isValid()) {
			return false;
		} else {
			return this.field_6384.squaredDistanceTo(this.field_6383) > (double)(this.chance * this.chance)
				? false
				: this.timer > 0 && this.method_6244(this.field_6383);
		}
	}

	@Override
	public void start() {
		this.field_6384.method_6712(true);
		this.timer = 40 + this.field_6384.getRand().nextInt(40);
	}

	@Override
	public void onRemove() {
		this.field_6384.method_6712(false);
		this.field_6383 = null;
	}

	@Override
	public void tick() {
		this.field_6384
			.method_5988()
			.lookAt(
				this.field_6383.x, this.field_6383.y + (double)this.field_6383.getStandingEyeHeight(), this.field_6383.z, 10.0F, (float)this.field_6384.method_5978()
			);
		this.timer--;
	}

	private boolean method_6244(PlayerEntity playerEntity) {
		for (Hand hand : Hand.values()) {
			ItemStack itemStack = playerEntity.method_5998(hand);
			if (this.field_6384.isTamed() && itemStack.getItem() == Items.field_8606) {
				return true;
			}

			if (this.field_6384.method_6481(itemStack)) {
				return true;
			}
		}

		return false;
	}
}
