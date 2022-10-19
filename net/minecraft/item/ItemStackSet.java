/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.item;

import it.unimi.dsi.fastutil.Hash;
import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenCustomHashSet;
import java.util.Objects;
import net.minecraft.item.ItemStack;

public final class ItemStackSet
extends ObjectLinkedOpenCustomHashSet<ItemStack> {
    private static final Hash.Strategy<? super ItemStack> HASH_STRATEGY = new Hash.Strategy<ItemStack>(){

        @Override
        public int hashCode(ItemStack itemStack) {
            if (itemStack != null) {
                return Objects.hash(itemStack.getItem(), itemStack.getNbt());
            }
            return 0;
        }

        @Override
        public boolean equals(ItemStack itemStack, ItemStack itemStack2) {
            return itemStack == itemStack2 || itemStack != null && itemStack2 != null && ItemStack.areEqual(itemStack, itemStack2);
        }

        @Override
        public /* synthetic */ boolean equals(Object first, Object second) {
            return this.equals((ItemStack)first, (ItemStack)second);
        }

        @Override
        public /* synthetic */ int hashCode(Object stack) {
            return this.hashCode((ItemStack)stack);
        }
    };

    public ItemStackSet() {
        super(HASH_STRATEGY);
    }
}

