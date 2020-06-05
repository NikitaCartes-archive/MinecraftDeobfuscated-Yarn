/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.loot.function;

import net.minecraft.loot.function.LootFunction;
import net.minecraft.util.JsonSerializableType;
import net.minecraft.util.JsonSerializer;

public class LootFunctionType
extends JsonSerializableType<LootFunction> {
    public LootFunctionType(JsonSerializer<? extends LootFunction> jsonSerializer) {
        super(jsonSerializer);
    }
}

