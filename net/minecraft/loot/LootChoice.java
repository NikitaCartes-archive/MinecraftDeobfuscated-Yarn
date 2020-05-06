/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.loot;

import java.util.function.Consumer;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContext;

public interface LootChoice {
    public int getWeight(float var1);

    public void generateLoot(Consumer<ItemStack> var1, LootContext var2);
}

