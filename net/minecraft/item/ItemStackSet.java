/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.item;

import it.unimi.dsi.fastutil.Hash;
import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenCustomHashSet;
import java.util.Set;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import org.jetbrains.annotations.Nullable;

public class ItemStackSet {
    private static final Hash.Strategy<? super ItemStack> HASH_STRATEGY = new Hash.Strategy<ItemStack>(){

        @Override
        public int hashCode(@Nullable ItemStack itemStack) {
            return ItemStackSet.getHashCode(itemStack);
        }

        @Override
        public boolean equals(@Nullable ItemStack itemStack, @Nullable ItemStack itemStack2) {
            return itemStack == itemStack2 || itemStack != null && itemStack2 != null && itemStack.isEmpty() == itemStack2.isEmpty() && ItemStack.canCombine(itemStack, itemStack2);
        }

        @Override
        public /* synthetic */ boolean equals(@Nullable Object first, @Nullable Object second) {
            return this.equals((ItemStack)first, (ItemStack)second);
        }

        @Override
        public /* synthetic */ int hashCode(@Nullable Object stack) {
            return this.hashCode((ItemStack)stack);
        }
    };

    static int getHashCode(@Nullable ItemStack stack) {
        if (stack != null) {
            NbtCompound nbtCompound = stack.getNbt();
            int i = 31 + stack.getItem().hashCode();
            return 31 * i + (nbtCompound == null ? 0 : nbtCompound.hashCode());
        }
        return 0;
    }

    public static Set<ItemStack> create() {
        return new ObjectLinkedOpenCustomHashSet<ItemStack>(HASH_STRATEGY);
    }
}

