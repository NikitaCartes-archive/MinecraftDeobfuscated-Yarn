/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.item;

import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.ChatFormat;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.FireworkItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.util.DyeColor;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class FireworkChargeItem
extends Item {
    public FireworkChargeItem(Item.Settings settings) {
        super(settings);
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public void appendTooltip(ItemStack itemStack, @Nullable World world, List<Component> list, TooltipContext tooltipContext) {
        CompoundTag compoundTag = itemStack.getSubTag("Explosion");
        if (compoundTag != null) {
            FireworkChargeItem.appendFireworkTooltip(compoundTag, list);
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static void appendFireworkTooltip(CompoundTag compoundTag, List<Component> list) {
        int[] js;
        FireworkItem.Type type = FireworkItem.Type.byId(compoundTag.getByte("Type"));
        list.add(new TranslatableComponent("item.minecraft.firework_star.shape." + type.getName(), new Object[0]).applyFormat(ChatFormat.GRAY));
        int[] is = compoundTag.getIntArray("Colors");
        if (is.length > 0) {
            list.add(FireworkChargeItem.appendColors(new TextComponent("").applyFormat(ChatFormat.GRAY), is));
        }
        if ((js = compoundTag.getIntArray("FadeColors")).length > 0) {
            list.add(FireworkChargeItem.appendColors(new TranslatableComponent("item.minecraft.firework_star.fade_to", new Object[0]).append(" ").applyFormat(ChatFormat.GRAY), js));
        }
        if (compoundTag.getBoolean("Trail")) {
            list.add(new TranslatableComponent("item.minecraft.firework_star.trail", new Object[0]).applyFormat(ChatFormat.GRAY));
        }
        if (compoundTag.getBoolean("Flicker")) {
            list.add(new TranslatableComponent("item.minecraft.firework_star.flicker", new Object[0]).applyFormat(ChatFormat.GRAY));
        }
    }

    @Environment(value=EnvType.CLIENT)
    private static Component appendColors(Component component, int[] is) {
        for (int i = 0; i < is.length; ++i) {
            if (i > 0) {
                component.append(", ");
            }
            component.append(FireworkChargeItem.getColorText(is[i]));
        }
        return component;
    }

    @Environment(value=EnvType.CLIENT)
    private static Component getColorText(int i) {
        DyeColor dyeColor = DyeColor.byFireworkColor(i);
        if (dyeColor == null) {
            return new TranslatableComponent("item.minecraft.firework_star.custom_color", new Object[0]);
        }
        return new TranslatableComponent("item.minecraft.firework_star." + dyeColor.getName(), new Object[0]);
    }
}

