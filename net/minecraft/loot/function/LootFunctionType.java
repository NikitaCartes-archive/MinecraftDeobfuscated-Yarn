/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.loot.function;

import net.minecraft.loot.function.LootFunction;
import net.minecraft.util.JsonSerializable;
import net.minecraft.util.JsonSerializableType;

public class LootFunctionType
extends JsonSerializableType<LootFunction> {
    public LootFunctionType(JsonSerializable<? extends LootFunction> jsonSerializable) {
        super(jsonSerializable);
    }
}

