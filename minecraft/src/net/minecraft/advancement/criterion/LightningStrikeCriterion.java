package net.minecraft.advancement.criterion;

import com.google.gson.JsonObject;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LightningEntity;
import net.minecraft.loot.context.LootContext;
import net.minecraft.predicate.entity.AdvancementEntityPredicateDeserializer;
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
		JsonObject jsonObject, Optional<LootContextPredicate> optional, AdvancementEntityPredicateDeserializer advancementEntityPredicateDeserializer
	) {
		Optional<LootContextPredicate> optional2 = EntityPredicate.contextPredicateFromJson(jsonObject, "lightning", advancementEntityPredicateDeserializer);
		Optional<LootContextPredicate> optional3 = EntityPredicate.contextPredicateFromJson(jsonObject, "bystander", advancementEntityPredicateDeserializer);
		return new LightningStrikeCriterion.Conditions(optional, optional2, optional3);
	}

	public void trigger(ServerPlayerEntity player, LightningEntity lightning, List<Entity> bystanders) {
		List<LootContext> list = (List<LootContext>)bystanders.stream()
			.map(bystander -> EntityPredicate.createAdvancementEntityLootContext(player, bystander))
			.collect(Collectors.toList());
		LootContext lootContext = EntityPredicate.createAdvancementEntityLootContext(player, lightning);
		this.trigger(player, conditions -> conditions.test(lootContext, list));
	}

	public static class Conditions extends AbstractCriterionConditions {
		private final Optional<LootContextPredicate> lightning;
		private final Optional<LootContextPredicate> bystander;

		public Conditions(Optional<LootContextPredicate> playerPredicate, Optional<LootContextPredicate> lightning, Optional<LootContextPredicate> bystander) {
			super(LightningStrikeCriterion.ID, playerPredicate);
			this.lightning = lightning;
			this.bystander = bystander;
		}

		public static LightningStrikeCriterion.Conditions create(Optional<EntityPredicate> lightning, Optional<EntityPredicate> bystander) {
			return new LightningStrikeCriterion.Conditions(
				Optional.empty(), EntityPredicate.contextPredicateFromEntityPredicate(lightning), EntityPredicate.contextPredicateFromEntityPredicate(bystander)
			);
		}

		public boolean test(LootContext lightning, List<LootContext> bystanders) {
			return this.lightning.isPresent() && !((LootContextPredicate)this.lightning.get()).test(lightning)
				? false
				: !this.bystander.isPresent() || !bystanders.stream().noneMatch(((LootContextPredicate)this.bystander.get())::test);
		}

		@Override
		public JsonObject toJson() {
			JsonObject jsonObject = super.toJson();
			this.lightning.ifPresent(lootContextPredicate -> jsonObject.add("lightning", lootContextPredicate.toJson()));
			this.bystander.ifPresent(lootContextPredicate -> jsonObject.add("bystander", lootContextPredicate.toJson()));
			return jsonObject;
		}
	}
}
