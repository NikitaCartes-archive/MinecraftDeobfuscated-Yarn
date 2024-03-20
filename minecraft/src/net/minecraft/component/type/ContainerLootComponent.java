package net.minecraft.component.type;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.loot.LootTable;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.dynamic.Codecs;

public record ContainerLootComponent(RegistryKey<LootTable> lootTable, long seed) {
	public static final Codec<ContainerLootComponent> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
					RegistryKey.createCodec(RegistryKeys.LOOT_TABLE).fieldOf("loot_table").forGetter(ContainerLootComponent::lootTable),
					Codecs.createStrictOptionalFieldCodec(Codec.LONG, "seed", 0L).forGetter(ContainerLootComponent::seed)
				)
				.apply(instance, ContainerLootComponent::new)
	);
}
