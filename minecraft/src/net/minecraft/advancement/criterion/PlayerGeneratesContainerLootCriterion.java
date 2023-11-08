package net.minecraft.advancement.criterion;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.advancement.AdvancementCriterion;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.predicate.entity.LootContextPredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.dynamic.Codecs;

public class PlayerGeneratesContainerLootCriterion extends AbstractCriterion<PlayerGeneratesContainerLootCriterion.Conditions> {
	@Override
	public Codec<PlayerGeneratesContainerLootCriterion.Conditions> getConditionsCodec() {
		return PlayerGeneratesContainerLootCriterion.Conditions.CODEC;
	}

	public void trigger(ServerPlayerEntity player, Identifier id) {
		this.trigger(player, conditions -> conditions.test(id));
	}

	public static record Conditions(Optional<LootContextPredicate> player, Identifier lootTable) implements AbstractCriterion.Conditions {
		public static final Codec<PlayerGeneratesContainerLootCriterion.Conditions> CODEC = RecordCodecBuilder.create(
			instance -> instance.group(
						Codecs.createStrictOptionalFieldCodec(EntityPredicate.LOOT_CONTEXT_PREDICATE_CODEC, "player")
							.forGetter(PlayerGeneratesContainerLootCriterion.Conditions::getPlayerPredicate),
						Identifier.CODEC.fieldOf("loot_table").forGetter(PlayerGeneratesContainerLootCriterion.Conditions::lootTable)
					)
					.apply(instance, PlayerGeneratesContainerLootCriterion.Conditions::new)
		);

		public static AdvancementCriterion<PlayerGeneratesContainerLootCriterion.Conditions> create(Identifier lootTable) {
			return Criteria.PLAYER_GENERATES_CONTAINER_LOOT.create(new PlayerGeneratesContainerLootCriterion.Conditions(Optional.empty(), lootTable));
		}

		public boolean test(Identifier lootTable) {
			return this.lootTable.equals(lootTable);
		}

		@Override
		public Optional<LootContextPredicate> getPlayerPredicate() {
			return this.player;
		}
	}
}
