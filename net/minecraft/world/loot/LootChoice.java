/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.loot;

import java.util.function.Consumer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.loot.context.LootContext;

public interface LootChoice {
    public int getWeight(float var1);

    public void drop(Consumer<ItemStack> var1, LootContext var2);
}

