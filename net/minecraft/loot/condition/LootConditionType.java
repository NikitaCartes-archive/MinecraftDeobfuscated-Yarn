/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.loot.condition;

import net.minecraft.loot.condition.LootCondition;
import net.minecraft.util.JsonSerializableType;
import net.minecraft.util.JsonSerializer;

public class LootConditionType
extends JsonSerializableType<LootCondition> {
    public LootConditionType(JsonSerializer<? extends LootCondition> jsonSerializer) {
        super(jsonSerializer);
    }
}

