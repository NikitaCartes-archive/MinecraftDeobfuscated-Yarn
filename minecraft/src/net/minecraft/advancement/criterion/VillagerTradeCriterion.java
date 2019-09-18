package net.minecraft.advancement.criterion;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.entity.passive.AbstractTraderEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class VillagerTradeCriterion extends AbstractCriterion<VillagerTradeCriterion.Conditions> {
	private static final Identifier ID = new Identifier("villager_trade");

	@Override
	public Identifier getId() {
		return ID;
	}

	public VillagerTradeCriterion.Conditions method_9148(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
		EntityPredicate entityPredicate = EntityPredicate.fromJson(jsonObject.get("villager"));
		ItemPredicate itemPredicate = ItemPredicate.deserialize(jsonObject.get("item"));
		return new VillagerTradeCriterion.Conditions(entityPredicate, itemPredicate);
	}

	public void handle(ServerPlayerEntity serverPlayerEntity, AbstractTraderEntity abstractTraderEntity, ItemStack itemStack) {
		this.test(serverPlayerEntity.getAdvancementManager(), conditions -> conditions.matches(serverPlayerEntity, abstractTraderEntity, itemStack));
	}

	public static class Conditions extends AbstractCriterionConditions {
		private final EntityPredicate item;
		private final ItemPredicate villager;

		public Conditions(EntityPredicate entityPredicate, ItemPredicate itemPredicate) {
			super(VillagerTradeCriterion.ID);
			this.item = entityPredicate;
			this.villager = itemPredicate;
		}

		public static VillagerTradeCriterion.Conditions any() {
			return new VillagerTradeCriterion.Conditions(EntityPredicate.ANY, ItemPredicate.ANY);
		}

		public boolean matches(ServerPlayerEntity serverPlayerEntity, AbstractTraderEntity abstractTraderEntity, ItemStack itemStack) {
			return !this.item.test(serverPlayerEntity, abstractTraderEntity) ? false : this.villager.test(itemStack);
		}

		@Override
		public JsonElement toJson() {
			JsonObject jsonObject = new JsonObject();
			jsonObject.add("item", this.villager.serialize());
			jsonObject.add("villager", this.item.serialize());
			return jsonObject;
		}
	}
}
