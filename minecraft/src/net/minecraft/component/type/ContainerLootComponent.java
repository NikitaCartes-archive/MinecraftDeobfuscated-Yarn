package net.minecraft.component.type;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.loot.LootTable;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;

public record ContainerLootComponent(RegistryKey<LootTable> lootTable, long seed) {
	public static final Codec<ContainerLootComponent> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
					RegistryKey.createCodec(RegistryKeys.LOOT_TABLE).fieldOf("loot_table").forGetter(ContainerLootComponent::lootTable),
					Codec.LONG.optionalFieldOf("seed", Long.valueOf(0L)).forGetter(ContainerLootComponent::seed)
				)
				.apply(instance, ContainerLootComponent::new)
	);
}
