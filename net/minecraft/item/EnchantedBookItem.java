/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.item;

import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.InfoEnchantment;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.util.DefaultedList;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class EnchantedBookItem
extends Item {
    public EnchantedBookItem(Item.Settings settings) {
        super(settings);
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public boolean hasEnchantmentGlint(ItemStack itemStack) {
        return true;
    }

    @Override
    public boolean isTool(ItemStack itemStack) {
        return false;
    }

    public static ListTag getEnchantmentTag(ItemStack itemStack) {
        CompoundTag compoundTag = itemStack.getTag();
        if (compoundTag != null) {
            return compoundTag.getList("StoredEnchantments", 10);
        }
        return new ListTag();
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public void buildTooltip(ItemStack itemStack, @Nullable World world, List<Component> list, TooltipContext tooltipContext) {
        super.buildTooltip(itemStack, world, list, tooltipContext);
        ItemStack.appendEnchantmentComponents(list, EnchantedBookItem.getEnchantmentTag(itemStack));
    }

    public static void addEnchantment(ItemStack itemStack, InfoEnchantment infoEnchantment) {
        ListTag listTag = EnchantedBookItem.getEnchantmentTag(itemStack);
        boolean bl = true;
        Identifier identifier = Registry.ENCHANTMENT.getId(infoEnchantment.enchantment);
        for (int i = 0; i < listTag.size(); ++i) {
            CompoundTag compoundTag = listTag.getCompoundTag(i);
            Identifier identifier2 = Identifier.create(compoundTag.getString("id"));
            if (identifier2 == null || !identifier2.equals(identifier)) continue;
            if (compoundTag.getInt("lvl") < infoEnchantment.level) {
                compoundTag.putShort("lvl", (short)infoEnchantment.level);
            }
            bl = false;
            break;
        }
        if (bl) {
            CompoundTag compoundTag2 = new CompoundTag();
            compoundTag2.putString("id", String.valueOf(identifier));
            compoundTag2.putShort("lvl", (short)infoEnchantment.level);
            listTag.add(compoundTag2);
        }
        itemStack.getOrCreateTag().put("StoredEnchantments", listTag);
    }

    public static ItemStack makeStack(InfoEnchantment infoEnchantment) {
        ItemStack itemStack = new ItemStack(Items.ENCHANTED_BOOK);
        EnchantedBookItem.addEnchantment(itemStack, infoEnchantment);
        return itemStack;
    }

    @Override
    public void appendItemsForGroup(ItemGroup itemGroup, DefaultedList<ItemStack> defaultedList) {
        block4: {
            block3: {
                if (itemGroup != ItemGroup.SEARCH) break block3;
                for (Enchantment enchantment : Registry.ENCHANTMENT) {
                    if (enchantment.type == null) continue;
                    for (int i = enchantment.getMinimumLevel(); i <= enchantment.getMaximumLevel(); ++i) {
                        defaultedList.add(EnchantedBookItem.makeStack(new InfoEnchantment(enchantment, i)));
                    }
                }
                break block4;
            }
            if (itemGroup.getEnchantments().length == 0) break block4;
            for (Enchantment enchantment : Registry.ENCHANTMENT) {
                if (!itemGroup.containsEnchantments(enchantment.type)) continue;
                defaultedList.add(EnchantedBookItem.makeStack(new InfoEnchantment(enchantment, enchantment.getMaximumLevel())));
            }
        }
    }
}

