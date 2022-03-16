package net.minecraft.item;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.world.World;

public class SuspiciousStewItem extends Item {
	public static final String EFFECTS_KEY = "Effects";
	public static final String EFFECT_ID_KEY = "EffectId";
	public static final String EFFECT_DURATION_KEY = "EffectDuration";

	public SuspiciousStewItem(Item.Settings settings) {
		super(settings);
	}

	public static void addEffectToStew(ItemStack stew, StatusEffect effect, int duration) {
		NbtCompound nbtCompound = stew.getOrCreateNbt();
		NbtList nbtList = nbtCompound.getList("Effects", NbtElement.LIST_TYPE);
		NbtCompound nbtCompound2 = new NbtCompound();
		nbtCompound2.putInt("EffectId", StatusEffect.getRawId(effect));
		nbtCompound2.putInt("EffectDuration", duration);
		nbtList.add(nbtCompound2);
		nbtCompound.put("Effects", nbtList);
	}

	@Override
	public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
		ItemStack itemStack = super.finishUsing(stack, world, user);
		NbtCompound nbtCompound = stack.getNbt();
		if (nbtCompound != null && nbtCompound.contains("Effects", NbtElement.LIST_TYPE)) {
			NbtList nbtList = nbtCompound.getList("Effects", NbtElement.COMPOUND_TYPE);

			for (int i = 0; i < nbtList.size(); i++) {
				int j = 160;
				NbtCompound nbtCompound2 = nbtList.getCompound(i);
				if (nbtCompound2.contains("EffectDuration", NbtElement.INT_TYPE)) {
					j = nbtCompound2.getInt("EffectDuration");
				}

				StatusEffect statusEffect = StatusEffect.byRawId(nbtCompound2.getInt("EffectId"));
				if (statusEffect != null) {
					user.addStatusEffect(new StatusEffectInstance(statusEffect, j));
				}
			}
		}

		return user instanceof PlayerEntity && ((PlayerEntity)user).getAbilities().creativeMode ? itemStack : new ItemStack(Items.BOWL);
	}
}
