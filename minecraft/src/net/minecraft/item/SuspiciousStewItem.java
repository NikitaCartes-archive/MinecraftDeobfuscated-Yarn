package net.minecraft.item;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.world.World;

public class SuspiciousStewItem extends Item {
	public SuspiciousStewItem(Item.Settings settings) {
		super(settings);
	}

	public static void addEffectToStew(ItemStack stew, StatusEffect effect, int duration) {
		NbtCompound nbtCompound = stew.getOrCreateTag();
		NbtList nbtList = nbtCompound.getList("Effects", 9);
		NbtCompound nbtCompound2 = new NbtCompound();
		nbtCompound2.putByte("EffectId", (byte)StatusEffect.getRawId(effect));
		nbtCompound2.putInt("EffectDuration", duration);
		nbtList.add(nbtCompound2);
		nbtCompound.put("Effects", nbtList);
	}

	@Override
	public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
		ItemStack itemStack = super.finishUsing(stack, world, user);
		NbtCompound nbtCompound = stack.getTag();
		if (nbtCompound != null && nbtCompound.contains("Effects", 9)) {
			NbtList nbtList = nbtCompound.getList("Effects", 10);

			for (int i = 0; i < nbtList.size(); i++) {
				int j = 160;
				NbtCompound nbtCompound2 = nbtList.getCompound(i);
				if (nbtCompound2.contains("EffectDuration", 3)) {
					j = nbtCompound2.getInt("EffectDuration");
				}

				StatusEffect statusEffect = StatusEffect.byRawId(nbtCompound2.getByte("EffectId"));
				if (statusEffect != null) {
					user.addStatusEffect(new StatusEffectInstance(statusEffect, j));
				}
			}
		}

		return user instanceof PlayerEntity && ((PlayerEntity)user).abilities.creativeMode ? itemStack : new ItemStack(Items.BOWL);
	}
}
