/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.item;

import java.util.List;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnchantmentLevelEntry;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class EnchantedBookItem
extends Item {
    public static final String STORED_ENCHANTMENTS_KEY = "StoredEnchantments";

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

    public static NbtList getEnchantmentNbt(ItemStack stack) {
        NbtCompound nbtCompound = stack.getNbt();
        if (nbtCompound != null) {
            return nbtCompound.getList(STORED_ENCHANTMENTS_KEY, NbtElement.COMPOUND_TYPE);
        }
        return new NbtList();
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        super.appendTooltip(stack, world, tooltip, context);
        ItemStack.appendEnchantments(tooltip, EnchantedBookItem.getEnchantmentNbt(stack));
    }

    public static void addEnchantment(ItemStack stack, EnchantmentLevelEntry entry) {
        NbtList nbtList = EnchantedBookItem.getEnchantmentNbt(stack);
        boolean bl = true;
        Identifier identifier = EnchantmentHelper.getEnchantmentId(entry.enchantment);
        for (int i = 0; i < nbtList.size(); ++i) {
            NbtCompound nbtCompound = nbtList.getCompound(i);
            Identifier identifier2 = EnchantmentHelper.getIdFromNbt(nbtCompound);
            if (identifier2 == null || !identifier2.equals(identifier)) continue;
            if (EnchantmentHelper.getLevelFromNbt(nbtCompound) < entry.level) {
                EnchantmentHelper.writeLevelToNbt(nbtCompound, entry.level);
            }
            bl = false;
            break;
        }
        if (bl) {
            nbtList.add(EnchantmentHelper.createNbt(identifier, entry.level));
        }
        stack.getOrCreateNbt().put(STORED_ENCHANTMENTS_KEY, nbtList);
    }

    public static ItemStack forEnchantment(EnchantmentLevelEntry info) {
        ItemStack itemStack = new ItemStack(Items.ENCHANTED_BOOK);
        EnchantedBookItem.addEnchantment(itemStack, info);
        return itemStack;
    }
}

