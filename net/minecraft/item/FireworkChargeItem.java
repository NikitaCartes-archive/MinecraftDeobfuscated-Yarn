/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.item;

import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.FireworkItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class FireworkChargeItem
extends Item {
    public FireworkChargeItem(Item.Settings settings) {
        super(settings);
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public void appendTooltip(ItemStack itemStack, @Nullable World world, List<Text> list, TooltipContext tooltipContext) {
        CompoundTag compoundTag = itemStack.getSubTag("Explosion");
        if (compoundTag != null) {
            FireworkChargeItem.appendFireworkTooltip(compoundTag, list);
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static void appendFireworkTooltip(CompoundTag compoundTag, List<Text> list) {
        int[] js;
        FireworkItem.Type type = FireworkItem.Type.byId(compoundTag.getByte("Type"));
        list.add(new TranslatableText("item.minecraft.firework_star.shape." + type.getName(), new Object[0]).formatted(Formatting.GRAY));
        int[] is = compoundTag.getIntArray("Colors");
        if (is.length > 0) {
            list.add(FireworkChargeItem.appendColors(new LiteralText("").formatted(Formatting.GRAY), is));
        }
        if ((js = compoundTag.getIntArray("FadeColors")).length > 0) {
            list.add(FireworkChargeItem.appendColors(new TranslatableText("item.minecraft.firework_star.fade_to", new Object[0]).append(" ").formatted(Formatting.GRAY), js));
        }
        if (compoundTag.getBoolean("Trail")) {
            list.add(new TranslatableText("item.minecraft.firework_star.trail", new Object[0]).formatted(Formatting.GRAY));
        }
        if (compoundTag.getBoolean("Flicker")) {
            list.add(new TranslatableText("item.minecraft.firework_star.flicker", new Object[0]).formatted(Formatting.GRAY));
        }
    }

    @Environment(value=EnvType.CLIENT)
    private static Text appendColors(Text text, int[] is) {
        for (int i = 0; i < is.length; ++i) {
            if (i > 0) {
                text.append(", ");
            }
            text.append(FireworkChargeItem.getColorText(is[i]));
        }
        return text;
    }

    @Environment(value=EnvType.CLIENT)
    private static Text getColorText(int i) {
        DyeColor dyeColor = DyeColor.byFireworkColor(i);
        if (dyeColor == null) {
            return new TranslatableText("item.minecraft.firework_star.custom_color", new Object[0]);
        }
        return new TranslatableText("item.minecraft.firework_star." + dyeColor.getName(), new Object[0]);
    }
}

