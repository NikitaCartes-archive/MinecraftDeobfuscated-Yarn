package net.minecraft.advancement.criterion;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class CuredZombieVillagerCriterion extends AbstractCriterion<CuredZombieVillagerCriterion.Conditions> {
	private static final Identifier ID = new Identifier("cured_zombie_villager");

	@Override
	public Identifier getId() {
		return ID;
	}

	public CuredZombieVillagerCriterion.Conditions conditionsFromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
		EntityPredicate entityPredicate = EntityPredicate.fromJson(jsonObject.get("zombie"));
		EntityPredicate entityPredicate2 = EntityPredicate.fromJson(jsonObject.get("villager"));
		return new CuredZombieVillagerCriterion.Conditions(entityPredicate, entityPredicate2);
	}

	public void trigger(ServerPlayerEntity player, ZombieEntity zombie, VillagerEntity villager) {
		this.test(player.getAdvancementTracker(), conditions -> conditions.matches(player, zombie, villager));
	}

	public static class Conditions extends AbstractCriterionConditions {
		private final EntityPredicate zombie;
		private final EntityPredicate villager;

		public Conditions(EntityPredicate zombie, EntityPredicate villager) {
			super(CuredZombieVillagerCriterion.ID);
			this.zombie = zombie;
			this.villager = villager;
		}

		public static CuredZombieVillagerCriterion.Conditions any() {
			return new CuredZombieVillagerCriterion.Conditions(EntityPredicate.ANY, EntityPredicate.ANY);
		}

		public boolean matches(ServerPlayerEntity player, ZombieEntity zombie, VillagerEntity villager) {
			return !this.zombie.test(player, zombie) ? false : this.villager.test(player, villager);
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
