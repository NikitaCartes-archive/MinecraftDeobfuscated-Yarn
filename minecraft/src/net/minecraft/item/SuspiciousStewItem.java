package net.minecraft.item;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.potion.PotionUtil;
import net.minecraft.text.Text;
import net.minecraft.world.World;

public class SuspiciousStewItem extends Item {
	public static final String EFFECTS_KEY = "Effects";
	public static final String EFFECT_ID_KEY = "EffectId";
	public static final String EFFECT_DURATION_KEY = "EffectDuration";
	public static final int DEFAULT_DURATION = 160;

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

	private static void forEachEffect(ItemStack stew, Consumer<StatusEffectInstance> effectConsumer) {
		NbtCompound nbtCompound = stew.getNbt();
		if (nbtCompound != null && nbtCompound.contains("Effects", NbtElement.LIST_TYPE)) {
			NbtList nbtList = nbtCompound.getList("Effects", NbtElement.COMPOUND_TYPE);

			for (int i = 0; i < nbtList.size(); i++) {
				NbtCompound nbtCompound2 = nbtList.getCompound(i);
				int j;
				if (nbtCompound2.contains("EffectDuration", NbtElement.INT_TYPE)) {
					j = nbtCompound2.getInt("EffectDuration");
				} else {
					j = 160;
				}

				StatusEffect statusEffect = StatusEffect.byRawId(nbtCompound2.getInt("EffectId"));
				if (statusEffect != null) {
					effectConsumer.accept(new StatusEffectInstance(statusEffect, j));
				}
			}
		}
	}

	@Override
	public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
		super.appendTooltip(stack, world, tooltip, context);
		if (context.isCreative()) {
			List<StatusEffectInstance> list = new ArrayList();
			forEachEffect(stack, list::add);
			PotionUtil.buildTooltip(list, tooltip, 1.0F);
		}
	}

	@Override
	public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
		ItemStack itemStack = super.finishUsing(stack, world, user);
		forEachEffect(itemStack, user::addStatusEffect);
		return user instanceof PlayerEntity && ((PlayerEntity)user).getAbilities().creativeMode ? itemStack : new ItemStack(Items.BOWL);
	}
}
