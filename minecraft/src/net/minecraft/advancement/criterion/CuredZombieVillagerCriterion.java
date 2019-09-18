package net.minecraft.advancement.criterion;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.class_4558;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class CuredZombieVillagerCriterion extends class_4558<CuredZombieVillagerCriterion.Conditions> {
	private static final Identifier ID = new Identifier("cured_zombie_villager");

	@Override
	public Identifier getId() {
		return ID;
	}

	public CuredZombieVillagerCriterion.Conditions method_8830(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
		EntityPredicate entityPredicate = EntityPredicate.deserialize(jsonObject.get("zombie"));
		EntityPredicate entityPredicate2 = EntityPredicate.deserialize(jsonObject.get("villager"));
		return new CuredZombieVillagerCriterion.Conditions(entityPredicate, entityPredicate2);
	}

	public void handle(ServerPlayerEntity serverPlayerEntity, ZombieEntity zombieEntity, VillagerEntity villagerEntity) {
		this.method_22510(serverPlayerEntity.getAdvancementManager(), conditions -> conditions.matches(serverPlayerEntity, zombieEntity, villagerEntity));
	}

	public static class Conditions extends AbstractCriterionConditions {
		private final EntityPredicate zombie;
		private final EntityPredicate villager;

		public Conditions(EntityPredicate entityPredicate, EntityPredicate entityPredicate2) {
			super(CuredZombieVillagerCriterion.ID);
			this.zombie = entityPredicate;
			this.villager = entityPredicate2;
		}

		public static CuredZombieVillagerCriterion.Conditions any() {
			return new CuredZombieVillagerCriterion.Conditions(EntityPredicate.ANY, EntityPredicate.ANY);
		}

		public boolean matches(ServerPlayerEntity serverPlayerEntity, ZombieEntity zombieEntity, VillagerEntity villagerEntity) {
			return !this.zombie.test(serverPlayerEntity, zombieEntity) ? false : this.villager.test(serverPlayerEntity, villagerEntity);
		}

		@Override
		public JsonElement toJson() {
			JsonObject jsonObject = new JsonObject();
			jsonObject.add("zombie", this.zombie.serialize());
			jsonObject.add("villager", this.villager.serialize());
			return jsonObject;
		}
	}
}
