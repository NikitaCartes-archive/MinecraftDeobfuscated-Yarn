package net.minecraft.entity.ai.goal;

import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Hand;

public class HoldInHandsGoal<T extends MobEntity> extends Goal {
	private final T actor;
	private final ItemStack item;
	private final Predicate<? super T> condition;
	private final SoundEvent sound;

	public HoldInHandsGoal(T actor, ItemStack item, @Nullable SoundEvent sound, Predicate<? super T> condition) {
		this.actor = actor;
		this.item = item;
		this.sound = sound;
		this.condition = condition;
	}

	@Override
	public boolean canStart() {
		return this.condition.test(this.actor);
	}

	@Override
	public boolean shouldContinue() {
		return this.actor.isUsingItem();
	}

	@Override
	public void start() {
		this.actor.equipStack(EquipmentSlot.MAINHAND, this.item.copy());
		this.actor.setCurrentHand(Hand.MAIN_HAND);
	}

	@Override
	public void stop() {
		this.actor.equipStack(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
		if (this.sound != null) {
			this.actor.playSound(this.sound, 1.0F, this.actor.getRandom().nextFloat() * 0.2F + 0.9F);
		}
	}
}
