/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.loot.provider.number;

import net.minecraft.loot.provider.number.LootNumberProvider;
import net.minecraft.util.JsonSerializableType;
import net.minecraft.util.JsonSerializer;

public class LootNumberProviderType
extends JsonSerializableType<LootNumberProvider> {
    public LootNumberProviderType(JsonSerializer<? extends LootNumberProvider> jsonSerializer) {
        super(jsonSerializer);
    }
}

