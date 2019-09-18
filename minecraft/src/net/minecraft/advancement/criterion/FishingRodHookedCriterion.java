package net.minecraft.advancement.criterion;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.Collection;
import net.minecraft.class_4558;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.projectile.FishingBobberEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class FishingRodHookedCriterion extends class_4558<FishingRodHookedCriterion.Conditions> {
	private static final Identifier ID = new Identifier("fishing_rod_hooked");

	@Override
	public Identifier getId() {
		return ID;
	}

	public FishingRodHookedCriterion.Conditions method_8941(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
		ItemPredicate itemPredicate = ItemPredicate.deserialize(jsonObject.get("rod"));
		EntityPredicate entityPredicate = EntityPredicate.deserialize(jsonObject.get("entity"));
		ItemPredicate itemPredicate2 = ItemPredicate.deserialize(jsonObject.get("item"));
		return new FishingRodHookedCriterion.Conditions(itemPredicate, entityPredicate, itemPredicate2);
	}

	public void handle(ServerPlayerEntity serverPlayerEntity, ItemStack itemStack, FishingBobberEntity fishingBobberEntity, Collection<ItemStack> collection) {
		this.method_22510(
			serverPlayerEntity.getAdvancementManager(), conditions -> conditions.matches(serverPlayerEntity, itemStack, fishingBobberEntity, collection)
		);
	}

	public static class Conditions extends AbstractCriterionConditions {
		private final ItemPredicate rod;
		private final EntityPredicate bobber;
		private final ItemPredicate item;

		public Conditions(ItemPredicate itemPredicate, EntityPredicate entityPredicate, ItemPredicate itemPredicate2) {
			super(FishingRodHookedCriterion.ID);
			this.rod = itemPredicate;
			this.bobber = entityPredicate;
			this.item = itemPredicate2;
		}

		public static FishingRodHookedCriterion.Conditions create(ItemPredicate itemPredicate, EntityPredicate entityPredicate, ItemPredicate itemPredicate2) {
			return new FishingRodHookedCriterion.Conditions(itemPredicate, entityPredicate, itemPredicate2);
		}

		public boolean matches(ServerPlayerEntity serverPlayerEntity, ItemStack itemStack, FishingBobberEntity fishingBobberEntity, Collection<ItemStack> collection) {
			if (!this.rod.test(itemStack)) {
				return false;
			} else if (!this.bobber.test(serverPlayerEntity, fishingBobberEntity.hookedEntity)) {
				return false;
			} else {
				if (this.item != ItemPredicate.ANY) {
					boolean bl = false;
					if (fishingBobberEntity.hookedEntity instanceof ItemEntity) {
						ItemEntity itemEntity = (ItemEntity)fishingBobberEntity.hookedEntity;
						if (this.item.test(itemEntity.getStack())) {
							bl = true;
						}
					}

					for (ItemStack itemStack2 : collection) {
						if (this.item.test(itemStack2)) {
							bl = true;
							break;
						}
					}

					if (!bl) {
						return false;
					}
				}

				return true;
			}
		}

		@Override
		public JsonElement toJson() {
			JsonObject jsonObject = new JsonObject();
			jsonObject.add("rod", this.rod.serialize());
			jsonObject.add("entity", this.bobber.serialize());
			jsonObject.add("item", this.item.serialize());
			return jsonObject;
		}
	}
}
