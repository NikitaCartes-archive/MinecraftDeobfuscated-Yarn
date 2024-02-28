package net.minecraft.component.type;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.Identifier;
import net.minecraft.util.dynamic.Codecs;

public record ContainerLootComponent(Identifier lootTable, long seed) {
	public static final Codec<ContainerLootComponent> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
					Identifier.CODEC.fieldOf("loot_table").forGetter(ContainerLootComponent::lootTable),
					Codecs.createStrictOptionalFieldCodec(Codec.LONG, "seed", 0L).forGetter(ContainerLootComponent::seed)
				)
				.apply(instance, ContainerLootComponent::new)
	);
}
