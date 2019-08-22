package net.minecraft.item;

import java.util.List;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.advancement.criterion.Criterions;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionUtil;
import net.minecraft.potion.Potions;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.DefaultedList;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

public class PotionItem extends Item {
	public PotionItem(Item.Settings settings) {
		super(settings);
	}

	@Environment(EnvType.CLIENT)
	@Override
	public ItemStack getStackForRender() {
		return PotionUtil.setPotion(super.getStackForRender(), Potions.WATER);
	}

	@Override
	public ItemStack finishUsing(ItemStack itemStack, World world, LivingEntity livingEntity) {
		PlayerEntity playerEntity = livingEntity instanceof PlayerEntity ? (PlayerEntity)livingEntity : null;
		if (playerEntity == null || !playerEntity.abilities.creativeMode) {
			itemStack.decrement(1);
		}

		if (playerEntity instanceof ServerPlayerEntity) {
			Criterions.CONSUME_ITEM.handle((ServerPlayerEntity)playerEntity, itemStack);
		}

		if (!world.isClient) {
			for (StatusEffectInstance statusEffectInstance : PotionUtil.getPotionEffects(itemStack)) {
				if (statusEffectInstance.getEffectType().isInstant()) {
					statusEffectInstance.getEffectType().applyInstantEffect(playerEntity, playerEntity, livingEntity, statusEffectInstance.getAmplifier(), 1.0);
				} else {
					livingEntity.addStatusEffect(new StatusEffectInstance(statusEffectInstance));
				}
			}
		}

		if (playerEntity != null) {
			playerEntity.incrementStat(Stats.USED.getOrCreateStat(this));
		}

		if (playerEntity == null || !playerEntity.abilities.creativeMode) {
			if (itemStack.isEmpty()) {
				return new ItemStack(Items.GLASS_BOTTLE);
			}

			if (playerEntity != null) {
				playerEntity.inventory.insertStack(new ItemStack(Items.GLASS_BOTTLE));
			}
		}

		return itemStack;
	}

	@Override
	public int getMaxUseTime(ItemStack itemStack) {
		return 32;
	}

	@Override
	public UseAction getUseAction(ItemStack itemStack) {
		return UseAction.DRINK;
	}

	@Override
	public TypedActionResult<ItemStack> use(World world, PlayerEntity playerEntity, Hand hand) {
		playerEntity.setCurrentHand(hand);
		return new TypedActionResult<>(ActionResult.SUCCESS, playerEntity.getStackInHand(hand));
	}

	@Override
	public String getTranslationKey(ItemStack itemStack) {
		return PotionUtil.getPotion(itemStack).getName(this.getTranslationKey() + ".effect.");
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void appendTooltip(ItemStack itemStack, @Nullable World world, List<Text> list, TooltipContext tooltipContext) {
		PotionUtil.buildTooltip(itemStack, list, 1.0F);
	}

	@Environment(EnvType.CLIENT)
	@Override
	public boolean hasEnchantmentGlint(ItemStack itemStack) {
		return super.hasEnchantmentGlint(itemStack) || !PotionUtil.getPotionEffects(itemStack).isEmpty();
	}

	@Override
	public void appendStacks(ItemGroup itemGroup, DefaultedList<ItemStack> defaultedList) {
		if (this.isIn(itemGroup)) {
			for (Potion potion : Registry.POTION) {
				if (potion != Potions.EMPTY) {
					defaultedList.add(PotionUtil.setPotion(new ItemStack(this), potion));
				}
			}
		}
	}
}
