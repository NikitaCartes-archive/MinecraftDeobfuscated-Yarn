package net.minecraft.advancement.criterion;

import com.google.gson.JsonObject;
import java.util.Optional;
import net.minecraft.predicate.entity.AdvancementEntityPredicateDeserializer;
import net.minecraft.predicate.entity.LootContextPredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;

public class PlayerGeneratesContainerLootCriterion extends AbstractCriterion<PlayerGeneratesContainerLootCriterion.Conditions> {
	static final Identifier ID = new Identifier("player_generates_container_loot");

	@Override
	public Identifier getId() {
		return ID;
	}

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
			super(PlayerGeneratesContainerLootCriterion.ID, playerPredicate);
			this.lootTable = lootTable;
		}

		public static PlayerGeneratesContainerLootCriterion.Conditions create(Identifier lootTable) {
			return new PlayerGeneratesContainerLootCriterion.Conditions(Optional.empty(), lootTable);
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
