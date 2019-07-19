/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.item;

import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.ArrowItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionUtil;
import net.minecraft.potion.Potions;
import net.minecraft.text.Text;
import net.minecraft.util.DefaultedList;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class TippedArrowItem
extends ArrowItem {
    public TippedArrowItem(Item.Settings settings) {
        super(settings);
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public ItemStack getStackForRender() {
        return PotionUtil.setPotion(super.getStackForRender(), Potions.POISON);
    }

    @Override
    public void appendStacks(ItemGroup itemGroup, DefaultedList<ItemStack> defaultedList) {
        if (this.isIn(itemGroup)) {
            for (Potion potion : Registry.POTION) {
                if (potion.getEffects().isEmpty()) continue;
                defaultedList.add(PotionUtil.setPotion(new ItemStack(this), potion));
            }
        }
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public void appendTooltip(ItemStack itemStack, @Nullable World world, List<Text> list, TooltipContext tooltipContext) {
        PotionUtil.buildTooltip(itemStack, list, 0.125f);
    }

    @Override
    public String getTranslationKey(ItemStack itemStack) {
        return PotionUtil.getPotion(itemStack).finishTranslationKey(this.getTranslationKey() + ".effect.");
    }
}

