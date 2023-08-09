package net.minecraft.advancement.criterion;

import com.google.gson.JsonObject;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import net.minecraft.entity.Entity;
import net.minecraft.loot.context.LootContext;
import net.minecraft.predicate.entity.AdvancementEntityPredicateDeserializer;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.predicate.entity.LootContextPredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class ChanneledLightningCriterion extends AbstractCriterion<ChanneledLightningCriterion.Conditions> {
	static final Identifier ID = new Identifier("channeled_lightning");

	@Override
	public Identifier getId() {
		return ID;
	}

	public ChanneledLightningCriterion.Conditions conditionsFromJson(
		JsonObject jsonObject, Optional<LootContextPredicate> optional, AdvancementEntityPredicateDeserializer advancementEntityPredicateDeserializer
	) {
		List<LootContextPredicate> list = EntityPredicate.contextPredicateArrayFromJson(jsonObject, "victims", advancementEntityPredicateDeserializer);
		return new ChanneledLightningCriterion.Conditions(optional, list);
	}

	public void trigger(ServerPlayerEntity player, Collection<? extends Entity> victims) {
		List<LootContext> list = (List<LootContext>)victims.stream()
			.map(entity -> EntityPredicate.createAdvancementEntityLootContext(player, entity))
			.collect(Collectors.toList());
		this.trigger(player, conditions -> conditions.matches(list));
	}

	public static class Conditions extends AbstractCriterionConditions {
		private final List<LootContextPredicate> victims;

		public Conditions(Optional<LootContextPredicate> playerPredicate, List<LootContextPredicate> victims) {
			super(ChanneledLightningCriterion.ID, playerPredicate);
			this.victims = victims;
		}

		public static ChanneledLightningCriterion.Conditions create(EntityPredicate.Builder... victims) {
			return new ChanneledLightningCriterion.Conditions(Optional.empty(), EntityPredicate.contextPredicateFromEntityPredicates(victims));
		}

		public boolean matches(Collection<? extends LootContext> victims) {
			for (LootContextPredicate lootContextPredicate : this.victims) {
				boolean bl = false;

				for (LootContext lootContext : victims) {
					if (lootContextPredicate.test(lootContext)) {
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

		@Override
		public JsonObject toJson() {
			JsonObject jsonObject = super.toJson();
			jsonObject.add("victims", LootContextPredicate.toPredicatesJsonArray(this.victims));
			return jsonObject;
		}
	}
}
