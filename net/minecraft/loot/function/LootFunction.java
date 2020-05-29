/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.loot.function;

import java.util.function.BiFunction;
import java.util.function.Consumer;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextAware;
import net.minecraft.loot.function.LootFunctionType;

public interface LootFunction
extends LootContextAware,
BiFunction<ItemStack, LootContext, ItemStack> {
    public LootFunctionType method_29321();

    public static Consumer<ItemStack> apply(BiFunction<ItemStack, LootContext, ItemStack> itemApplier, Consumer<ItemStack> lootConsumer, LootContext context) {
        return stack -> lootConsumer.accept((ItemStack)itemApplier.apply((ItemStack)stack, context));
    }

    public static interface Builder {
        public LootFunction build();
    }
}

