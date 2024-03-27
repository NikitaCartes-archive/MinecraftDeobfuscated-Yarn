package net.minecraft.loot.condition;

import com.mojang.serialization.MapCodec;

public record LootConditionType(MapCodec<? extends LootCondition> codec) {
}
