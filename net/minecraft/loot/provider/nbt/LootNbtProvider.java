/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.loot.provider.nbt;

import java.util.Set;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameter;
import net.minecraft.loot.provider.nbt.LootNbtProviderType;
import net.minecraft.nbt.Tag;
import org.jetbrains.annotations.Nullable;

public interface LootNbtProvider {
    @Nullable
    public Tag method_32440(LootContext var1);

    public Set<LootContextParameter<?>> method_32441();

    public LootNbtProviderType getType();
}

