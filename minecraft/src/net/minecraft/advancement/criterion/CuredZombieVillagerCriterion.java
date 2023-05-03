package net.minecraft.advancement.criterion;

import com.google.gson.JsonObject;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.loot.context.LootContext;
import net.minecraft.predicate.entity.AdvancementEntityPredicateDeserializer;
import net.minecraft.predicate.entity.AdvancementEntityPredicateSerializer;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.predicate.entity.LootContextPredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class CuredZombieVillagerCriterion extends AbstractCriterion<CuredZombieVillagerCriterion.Conditions> {
	static final Identifier ID = new Identifier("cured_zombie_villager");

	@Override
	public Identifier getId() {
		return ID;
	}

	public CuredZombieVillagerCriterion.Conditions conditionsFromJson(
		JsonObject jsonObject, LootContextPredicate lootContextPredicate, AdvancementEntityPredicateDeserializer advancementEntityPredicateDeserializer
	) {
		LootContextPredicate lootContextPredicate2 = EntityPredicate.contextPredicateFromJson(jsonObject, "zombie", advancementEntityPredicateDeserializer);
		LootContextPredicate lootContextPredicate3 = EntityPredicate.contextPredicateFromJson(jsonObject, "villager", advancementEntityPredicateDeserializer);
		return new CuredZombieVillagerCriterion.Conditions(lootContextPredicate, lootContextPredicate2, lootContextPredicate3);
	}

	public void trigger(ServerPlayerEntity player, ZombieEntity zombie, VillagerEntity villager) {
		LootContext lootContext = EntityPredicate.createAdvancementEntityLootContext(player, zombie);
		LootContext lootContext2 = EntityPredicate.createAdvancementEntityLootContext(player, villager);
		this.trigger(player, conditions -> conditions.matches(lootContext, lootContext2));
	}

	public static class Conditions extends AbstractCriterionConditions {
		private final LootContextPredicate zombie;
		private final LootContextPredicate villager;

		public Conditions(LootContextPredicate player, LootContextPredicate zombie, LootContextPredicate villager) {
			super(CuredZombieVillagerCriterion.ID, player);
			this.zombie = zombie;
			this.villager = villager;
		}

		public static CuredZombieVillagerCriterion.Conditions any() {
			return new CuredZombieVillagerCriterion.Conditions(LootContextPredicate.EMPTY, LootContextPredicate.EMPTY, LootContextPredicate.EMPTY);
		}

		public boolean matches(LootContext zombieContext, LootContext villagerContext) {
			return !this.zombie.test(zombieContext) ? false : this.villager.test(villagerContext);
		}

		@Override
		public JsonObject toJson(AdvancementEntityPredicateSerializer predicateSerializer) {
			JsonObject jsonObject = super.toJson(predicateSerializer);
			jsonObject.add("zombie", this.zombie.toJson(predicateSerializer));
			jsonObject.add("villager", this.villager.toJson(predicateSerializer));
			return jsonObject;
		}
	}
}
