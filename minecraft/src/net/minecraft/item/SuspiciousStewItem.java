package net.minecraft.item;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.World;

public class SuspiciousStewItem extends Item {
	public SuspiciousStewItem(Item.Settings settings) {
		super(settings);
	}

	public static void addEffectToStew(ItemStack stew, StatusEffect effect, int duration) {
		CompoundTag compoundTag = stew.getOrCreateTag();
		ListTag listTag = compoundTag.getList("Effects", 9);
		CompoundTag compoundTag2 = new CompoundTag();
		compoundTag2.putByte("EffectId", (byte)StatusEffect.getRawId(effect));
		compoundTag2.putInt("EffectDuration", duration);
		listTag.add(compoundTag2);
		compoundTag.put("Effects", listTag);
	}

	@Override
	public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
		ItemStack itemStack = super.finishUsing(stack, world, user);
		CompoundTag compoundTag = stack.getTag();
		if (compoundTag != null && compoundTag.contains("Effects", 9)) {
			ListTag listTag = compoundTag.getList("Effects", 10);

			for (int i = 0; i < listTag.size(); i++) {
				int j = 160;
				CompoundTag compoundTag2 = listTag.getCompound(i);
				if (compoundTag2.contains("EffectDuration", 3)) {
					j = compoundTag2.getInt("EffectDuration");
				}

				StatusEffect statusEffect = StatusEffect.byRawId(compoundTag2.getByte("EffectId"));
				if (statusEffect != null) {
					user.addStatusEffect(new StatusEffectInstance(statusEffect, j));
				}
			}
		}

		return user instanceof PlayerEntity && ((PlayerEntity)user).abilities.creativeMode ? itemStack : new ItemStack(Items.BOWL);
	}
}
