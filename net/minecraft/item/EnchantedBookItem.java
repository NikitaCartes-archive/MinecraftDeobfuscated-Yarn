/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.item;

import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentLevelEntry;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class EnchantedBookItem
extends Item {
    public EnchantedBookItem(Item.Settings settings) {
        super(settings);
    }

    @Override
    public boolean hasGlint(ItemStack stack) {
        return true;
    }

    @Override
    public boolean isEnchantable(ItemStack stack) {
        return false;
    }

    public static ListTag getEnchantmentNbt(ItemStack stack) {
        CompoundTag compoundTag = stack.getTag();
        if (compoundTag != null) {
            return compoundTag.getList("StoredEnchantments", 10);
        }
        return new ListTag();
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        super.appendTooltip(stack, world, tooltip, context);
        ItemStack.appendEnchantments(tooltip, EnchantedBookItem.getEnchantmentNbt(stack));
    }

    public static void addEnchantment(ItemStack stack, EnchantmentLevelEntry entry) {
        ListTag listTag = EnchantedBookItem.getEnchantmentNbt(stack);
        boolean bl = true;
        Identifier identifier = Registry.ENCHANTMENT.getId(entry.enchantment);
        for (int i = 0; i < listTag.size(); ++i) {
            CompoundTag compoundTag = listTag.getCompound(i);
            Identifier identifier2 = Identifier.tryParse(compoundTag.getString("id"));
            if (identifier2 == null || !identifier2.equals(identifier)) continue;
            if (compoundTag.getInt("lvl") < entry.level) {
                compoundTag.putShort("lvl", (short)entry.level);
            }
            bl = false;
            break;
        }
        if (bl) {
            CompoundTag compoundTag2 = new CompoundTag();
            compoundTag2.putString("id", String.valueOf(identifier));
            compoundTag2.putShort("lvl", (short)entry.level);
            listTag.add(compoundTag2);
        }
        stack.getOrCreateTag().put("StoredEnchantments", listTag);
    }

    public static ItemStack forEnchantment(EnchantmentLevelEntry info) {
        ItemStack itemStack = new ItemStack(Items.ENCHANTED_BOOK);
        EnchantedBookItem.addEnchantment(itemStack, info);
        return itemStack;
    }

    @Override
    public void appendStacks(ItemGroup group, DefaultedList<ItemStack> stacks) {
        block4: {
            block3: {
                if (group != ItemGroup.SEARCH) break block3;
                for (Enchantment enchantment : Registry.ENCHANTMENT) {
                    if (enchantment.type == null) continue;
                    for (int i = enchantment.getMinLevel(); i <= enchantment.getMaxLevel(); ++i) {
                        stacks.add(EnchantedBookItem.forEnchantment(new EnchantmentLevelEntry(enchantment, i)));
                    }
                }
                break block4;
            }
            if (group.getEnchantments().length == 0) break block4;
            for (Enchantment enchantment : Registry.ENCHANTMENT) {
                if (!group.containsEnchantments(enchantment.type)) continue;
                stacks.add(EnchantedBookItem.forEnchantment(new EnchantmentLevelEntry(enchantment, enchantment.getMaxLevel())));
            }
        }
    }
}

