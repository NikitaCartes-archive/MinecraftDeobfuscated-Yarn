/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.item;

import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.advancement.criterion.Criterions;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionUtil;
import net.minecraft.potion.Potions;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;
import net.minecraft.util.DefaultedList;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class PotionItem
extends Item {
    public PotionItem(Item.Settings settings) {
        super(settings);
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public ItemStack getStackForRender() {
        return PotionUtil.setPotion(super.getStackForRender(), Potions.WATER);
    }

    @Override
    public ItemStack finishUsing(ItemStack itemStack, World world, LivingEntity livingEntity) {
        PlayerEntity playerEntity;
        PlayerEntity playerEntity2 = playerEntity = livingEntity instanceof PlayerEntity ? (PlayerEntity)livingEntity : null;
        if (playerEntity instanceof ServerPlayerEntity) {
            Criterions.CONSUME_ITEM.trigger((ServerPlayerEntity)playerEntity, itemStack);
        }
        if (!world.isClient) {
            List<StatusEffectInstance> list = PotionUtil.getPotionEffects(itemStack);
            for (StatusEffectInstance statusEffectInstance : list) {
                if (statusEffectInstance.getEffectType().isInstant()) {
                    statusEffectInstance.getEffectType().applyInstantEffect(playerEntity, playerEntity, livingEntity, statusEffectInstance.getAmplifier(), 1.0);
                    continue;
                }
                livingEntity.addStatusEffect(new StatusEffectInstance(statusEffectInstance));
            }
        }
        if (playerEntity != null) {
            playerEntity.incrementStat(Stats.USED.getOrCreateStat(this));
            if (!playerEntity.abilities.creativeMode) {
                itemStack.decrement(1);
            }
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
        return TypedActionResult.successWithSwing(playerEntity.getStackInHand(hand));
    }

    @Override
    public String getTranslationKey(ItemStack itemStack) {
        return PotionUtil.getPotion(itemStack).getName(this.getTranslationKey() + ".effect.");
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public void appendTooltip(ItemStack itemStack, @Nullable World world, List<Text> list, TooltipContext tooltipContext) {
        PotionUtil.buildTooltip(itemStack, list, 1.0f);
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public boolean hasEnchantmentGlint(ItemStack itemStack) {
        return super.hasEnchantmentGlint(itemStack) || !PotionUtil.getPotionEffects(itemStack).isEmpty();
    }

    @Override
    public void appendStacks(ItemGroup itemGroup, DefaultedList<ItemStack> defaultedList) {
        if (this.isIn(itemGroup)) {
            for (Potion potion : Registry.POTION) {
                if (potion == Potions.EMPTY) continue;
                defaultedList.add(PotionUtil.setPotion(new ItemStack(this), potion));
            }
        }
    }
}

