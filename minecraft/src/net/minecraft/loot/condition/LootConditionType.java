package net.minecraft.loot.condition;

import com.mojang.serialization.Codec;

public record LootConditionType(Codec<? extends LootCondition> codec) {
}
