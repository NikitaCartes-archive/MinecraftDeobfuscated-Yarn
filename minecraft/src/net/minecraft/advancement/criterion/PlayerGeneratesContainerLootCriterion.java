package net.minecraft.advancement.criterion;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.advancement.AdvancementCriterion;
import net.minecraft.loot.LootTable;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.predicate.entity.LootContextPredicate;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.network.ServerPlayerEntity;

public class PlayerGeneratesContainerLootCriterion extends AbstractCriterion<PlayerGeneratesContainerLootCriterion.Conditions> {
	@Override
	public Codec<PlayerGeneratesContainerLootCriterion.Conditions> getConditionsCodec() {
		return PlayerGeneratesContainerLootCriterion.Conditions.CODEC;
	}

	public void trigger(ServerPlayerEntity player, RegistryKey<LootTable> lootTable) {
		this.trigger(player, conditions -> conditions.test(lootTable));
	}

	public static record Conditions(Optional<LootContextPredicate> player, RegistryKey<LootTable> lootTable) implements AbstractCriterion.Conditions {
		public static final Codec<PlayerGeneratesContainerLootCriterion.Conditions> CODEC = RecordCodecBuilder.create(
			instance -> instance.group(
						EntityPredicate.LOOT_CONTEXT_PREDICATE_CODEC.optionalFieldOf("player").forGetter(PlayerGeneratesContainerLootCriterion.Conditions::player),
						RegistryKey.createCodec(RegistryKeys.LOOT_TABLE).fieldOf("loot_table").forGetter(PlayerGeneratesContainerLootCriterion.Conditions::lootTable)
					)
					.apply(instance, PlayerGeneratesContainerLootCriterion.Conditions::new)
		);

		public static AdvancementCriterion<PlayerGeneratesContainerLootCriterion.Conditions> create(RegistryKey<LootTable> registryKey) {
			return Criteria.PLAYER_GENERATES_CONTAINER_LOOT.create(new PlayerGeneratesContainerLootCriterion.Conditions(Optional.empty(), registryKey));
		}

		public boolean test(RegistryKey<LootTable> lootTable) {
			return this.lootTable == lootTable;
		}
	}
}
