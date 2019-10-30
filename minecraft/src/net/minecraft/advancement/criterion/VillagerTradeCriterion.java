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

	public void handle(ServerPlayerEntity player, AbstractTraderEntity trader, ItemStack stack) {
		this.test(player.getAdvancementManager(), conditions -> conditions.matches(player, trader, stack));
	}

	public static class Conditions extends AbstractCriterionConditions {
		private final EntityPredicate villager;
		private final ItemPredicate item;

		public Conditions(EntityPredicate entity, ItemPredicate item) {
			super(VillagerTradeCriterion.ID);
			this.villager = entity;
			this.item = item;
		}

		public static VillagerTradeCriterion.Conditions any() {
			return new VillagerTradeCriterion.Conditions(EntityPredicate.ANY, ItemPredicate.ANY);
		}

		public boolean matches(ServerPlayerEntity player, AbstractTraderEntity trader, ItemStack stack) {
			return !this.villager.test(player, trader) ? false : this.item.test(stack);
		}

		@Override
		public JsonElement toJson() {
			JsonObject jsonObject = new JsonObject();
			jsonObject.add("item", this.item.serialize());
			jsonObject.add("villager", this.villager.serialize());
			return jsonObject;
		}
	}
}
