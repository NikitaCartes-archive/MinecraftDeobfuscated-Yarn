package net.minecraft.entity.ai.goal;

import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Hand;

public class HoldInHandsGoal<T extends MobEntity> extends Goal {
	private final T field_17755;
	private final ItemStack item;
	private final Predicate<? super T> condition;
	private final SoundEvent sound;

	public HoldInHandsGoal(T mobEntity, ItemStack itemStack, @Nullable SoundEvent soundEvent, Predicate<? super T> predicate) {
		this.field_17755 = mobEntity;
		this.item = itemStack;
		this.sound = soundEvent;
		this.condition = predicate;
	}

	@Override
	public boolean canStart() {
		return this.condition.test(this.field_17755);
	}

	@Override
	public boolean shouldContinue() {
		return this.field_17755.isUsingItem();
	}

	@Override
	public void start() {
		this.field_17755.setEquippedStack(EquipmentSlot.HAND_MAIN, this.item.copy());
		this.field_17755.setCurrentHand(Hand.MAIN);
	}

	@Override
	public void stop() {
		this.field_17755.setEquippedStack(EquipmentSlot.HAND_MAIN, ItemStack.EMPTY);
		if (this.sound != null) {
			this.field_17755.playSound(this.sound, 1.0F, this.field_17755.getRand().nextFloat() * 0.2F + 0.9F);
		}
	}
}
