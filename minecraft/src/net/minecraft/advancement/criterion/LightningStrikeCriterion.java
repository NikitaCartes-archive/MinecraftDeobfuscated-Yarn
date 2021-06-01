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
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class LightningStrikeCriterion extends AbstractCriterion<LightningStrikeCriterion.Conditions> {
	static final Identifier ID = new Identifier("lightning_strike");

	@Override
	public Identifier getId() {
		return ID;
	}

	public LightningStrikeCriterion.Conditions conditionsFromJson(
		JsonObject jsonObject, EntityPredicate.Extended extended, AdvancementEntityPredicateDeserializer advancementEntityPredicateDeserializer
	) {
		EntityPredicate.Extended extended2 = EntityPredicate.Extended.getInJson(jsonObject, "lightning", advancementEntityPredicateDeserializer);
		EntityPredicate.Extended extended3 = EntityPredicate.Extended.getInJson(jsonObject, "bystander", advancementEntityPredicateDeserializer);
		return new LightningStrikeCriterion.Conditions(extended, extended2, extended3);
	}

	public void test(ServerPlayerEntity player, LightningEntity lightning, List<Entity> bystanders) {
		List<LootContext> list = (List<LootContext>)bystanders.stream()
			.map(bystander -> EntityPredicate.createAdvancementEntityLootContext(player, bystander))
			.collect(Collectors.toList());
		LootContext lootContext = EntityPredicate.createAdvancementEntityLootContext(player, lightning);
		this.test(player, conditions -> conditions.test(lootContext, list));
	}

	public static class Conditions extends AbstractCriterionConditions {
		private final EntityPredicate.Extended lightning;
		private final EntityPredicate.Extended bystander;

		public Conditions(EntityPredicate.Extended player, EntityPredicate.Extended lightning, EntityPredicate.Extended bystander) {
			super(LightningStrikeCriterion.ID, player);
			this.lightning = lightning;
			this.bystander = bystander;
		}

		public static LightningStrikeCriterion.Conditions create(EntityPredicate lightning, EntityPredicate bystander) {
			return new LightningStrikeCriterion.Conditions(
				EntityPredicate.Extended.EMPTY, EntityPredicate.Extended.ofLegacy(lightning), EntityPredicate.Extended.ofLegacy(bystander)
			);
		}

		public boolean test(LootContext lightning, List<LootContext> bystanders) {
			return !this.lightning.test(lightning) ? false : this.bystander == EntityPredicate.Extended.EMPTY || !bystanders.stream().noneMatch(this.bystander::test);
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
