package net.minecraft.advancement.criterion;

import com.google.gson.JsonObject;
import java.util.Optional;
import net.minecraft.advancement.AdvancementCriterion;
import net.minecraft.predicate.entity.AdvancementEntityPredicateDeserializer;
import net.minecraft.predicate.entity.LootContextPredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;

public class PlayerGeneratesContainerLootCriterion extends AbstractCriterion<PlayerGeneratesContainerLootCriterion.Conditions> {
	protected PlayerGeneratesContainerLootCriterion.Conditions conditionsFromJson(
		JsonObject jsonObject, Optional<LootContextPredicate> optional, AdvancementEntityPredicateDeserializer advancementEntityPredicateDeserializer
	) {
		Identifier identifier = new Identifier(JsonHelper.getString(jsonObject, "loot_table"));
		return new PlayerGeneratesContainerLootCriterion.Conditions(optional, identifier);
	}

	public void trigger(ServerPlayerEntity player, Identifier id) {
		this.trigger(player, conditions -> conditions.test(id));
	}

	public static class Conditions extends AbstractCriterionConditions {
		private final Identifier lootTable;

		public Conditions(Optional<LootContextPredicate> playerPredicate, Identifier lootTable) {
			super(playerPredicate);
			this.lootTable = lootTable;
		}

		public static AdvancementCriterion<PlayerGeneratesContainerLootCriterion.Conditions> create(Identifier lootTable) {
			return Criteria.PLAYER_GENERATES_CONTAINER_LOOT.create(new PlayerGeneratesContainerLootCriterion.Conditions(Optional.empty(), lootTable));
		}

		public boolean test(Identifier lootTable) {
			return this.lootTable.equals(lootTable);
		}

		@Override
		public JsonObject toJson() {
			JsonObject jsonObject = super.toJson();
			jsonObject.addProperty("loot_table", this.lootTable.toString());
			return jsonObject;
		}
	}
}
