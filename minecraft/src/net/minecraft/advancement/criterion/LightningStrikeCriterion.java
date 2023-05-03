package net.minecraft.advancement.criterion;

import com.google.gson.JsonObject;
import java.util.List;
import java.util.stream.Collectors;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LightningEntity;
import net.minecraft.loot.context.LootContext;
import net.minecraft.predicate.entity.AdvancementEntityPredicateDeserializer;
import net.minecraft.predicate.entity.AdvancementEntityPredicateSerializer;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.predicate.entity.LootContextPredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class LightningStrikeCriterion extends AbstractCriterion<LightningStrikeCriterion.Conditions> {
	static final Identifier ID = new Identifier("lightning_strike");

	@Override
	public Identifier getId() {
		return ID;
	}

	public LightningStrikeCriterion.Conditions conditionsFromJson(
		JsonObject jsonObject, LootContextPredicate lootContextPredicate, AdvancementEntityPredicateDeserializer advancementEntityPredicateDeserializer
	) {
		LootContextPredicate lootContextPredicate2 = EntityPredicate.contextPredicateFromJson(jsonObject, "lightning", advancementEntityPredicateDeserializer);
		LootContextPredicate lootContextPredicate3 = EntityPredicate.contextPredicateFromJson(jsonObject, "bystander", advancementEntityPredicateDeserializer);
		return new LightningStrikeCriterion.Conditions(lootContextPredicate, lootContextPredicate2, lootContextPredicate3);
	}

	public void trigger(ServerPlayerEntity player, LightningEntity lightning, List<Entity> bystanders) {
		List<LootContext> list = (List<LootContext>)bystanders.stream()
			.map(bystander -> EntityPredicate.createAdvancementEntityLootContext(player, bystander))
			.collect(Collectors.toList());
		LootContext lootContext = EntityPredicate.createAdvancementEntityLootContext(player, lightning);
		this.trigger(player, conditions -> conditions.test(lootContext, list));
	}

	public static class Conditions extends AbstractCriterionConditions {
		private final LootContextPredicate lightning;
		private final LootContextPredicate bystander;

		public Conditions(LootContextPredicate player, LootContextPredicate lightning, LootContextPredicate bystander) {
			super(LightningStrikeCriterion.ID, player);
			this.lightning = lightning;
			this.bystander = bystander;
		}

		public static LightningStrikeCriterion.Conditions create(EntityPredicate lightning, EntityPredicate bystander) {
			return new LightningStrikeCriterion.Conditions(
				LootContextPredicate.EMPTY, EntityPredicate.asLootContextPredicate(lightning), EntityPredicate.asLootContextPredicate(bystander)
			);
		}

		public boolean test(LootContext lightning, List<LootContext> bystanders) {
			return !this.lightning.test(lightning) ? false : this.bystander == LootContextPredicate.EMPTY || !bystanders.stream().noneMatch(this.bystander::test);
		}

		@Override
		public JsonObject toJson(AdvancementEntityPredicateSerializer predicateSerializer) {
			JsonObject jsonObject = super.toJson(predicateSerializer);
			jsonObject.add("lightning", this.lightning.toJson(predicateSerializer));
			jsonObject.add("bystander", this.bystander.toJson(predicateSerializer));
			return jsonObject;
		}
	}
}
