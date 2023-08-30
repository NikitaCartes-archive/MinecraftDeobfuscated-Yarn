package net.minecraft.advancement.criterion;

import com.google.gson.JsonObject;
import java.util.Optional;
import net.minecraft.advancement.AdvancementCriterion;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.loot.context.LootContext;
import net.minecraft.predicate.entity.AdvancementEntityPredicateDeserializer;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.predicate.entity.LootContextPredicate;
import net.minecraft.server.network.ServerPlayerEntity;

public class CuredZombieVillagerCriterion extends AbstractCriterion<CuredZombieVillagerCriterion.Conditions> {
	public CuredZombieVillagerCriterion.Conditions conditionsFromJson(
		JsonObject jsonObject, Optional<LootContextPredicate> optional, AdvancementEntityPredicateDeserializer advancementEntityPredicateDeserializer
	) {
		Optional<LootContextPredicate> optional2 = EntityPredicate.contextPredicateFromJson(jsonObject, "zombie", advancementEntityPredicateDeserializer);
		Optional<LootContextPredicate> optional3 = EntityPredicate.contextPredicateFromJson(jsonObject, "villager", advancementEntityPredicateDeserializer);
		return new CuredZombieVillagerCriterion.Conditions(optional, optional2, optional3);
	}

	public void trigger(ServerPlayerEntity player, ZombieEntity zombie, VillagerEntity villager) {
		LootContext lootContext = EntityPredicate.createAdvancementEntityLootContext(player, zombie);
		LootContext lootContext2 = EntityPredicate.createAdvancementEntityLootContext(player, villager);
		this.trigger(player, conditions -> conditions.matches(lootContext, lootContext2));
	}

	public static class Conditions extends AbstractCriterionConditions {
		private final Optional<LootContextPredicate> zombie;
		private final Optional<LootContextPredicate> villager;

		public Conditions(Optional<LootContextPredicate> playerPredicate, Optional<LootContextPredicate> zombie, Optional<LootContextPredicate> villager) {
			super(playerPredicate);
			this.zombie = zombie;
			this.villager = villager;
		}

		public static AdvancementCriterion<CuredZombieVillagerCriterion.Conditions> any() {
			return Criteria.CURED_ZOMBIE_VILLAGER.create(new CuredZombieVillagerCriterion.Conditions(Optional.empty(), Optional.empty(), Optional.empty()));
		}

		public boolean matches(LootContext zombie, LootContext villager) {
			return this.zombie.isPresent() && !((LootContextPredicate)this.zombie.get()).test(zombie)
				? false
				: !this.villager.isPresent() || ((LootContextPredicate)this.villager.get()).test(villager);
		}

		@Override
		public JsonObject toJson() {
			JsonObject jsonObject = super.toJson();
			this.zombie.ifPresent(lootContextPredicate -> jsonObject.add("zombie", lootContextPredicate.toJson()));
			this.villager.ifPresent(lootContextPredicate -> jsonObject.add("villager", lootContextPredicate.toJson()));
			return jsonObject;
		}
	}
}
