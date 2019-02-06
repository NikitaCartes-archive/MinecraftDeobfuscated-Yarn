package net.minecraft;

import java.util.function.Predicate;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;

public class class_3993<T extends MobEntity> extends Goal {
	private final T field_17755;
	private final ItemStack field_17756;
	private final Predicate<? super T> field_17757;

	public class_3993(T mobEntity, ItemStack itemStack, Predicate<? super T> predicate) {
		this.field_17755 = mobEntity;
		this.field_17756 = itemStack;
		this.field_17757 = predicate;
	}

	@Override
	public boolean canStart() {
		return this.field_17757.test(this.field_17755);
	}

	@Override
	public boolean shouldContinue() {
		return this.field_17755.isUsingItem();
	}

	@Override
	public void start() {
		this.field_17755.setEquippedStack(EquipmentSlot.HAND_MAIN, this.field_17756.copy());
		this.field_17755.setCurrentHand(Hand.MAIN);
	}

	@Override
	public void onRemove() {
		this.field_17755.setEquippedStack(EquipmentSlot.HAND_MAIN, ItemStack.EMPTY);
	}
}
