package net.minecraft.advancement.criterion;

import com.google.gson.JsonObject;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.entity.Entity;
import net.minecraft.loot.context.LootContext;
import net.minecraft.predicate.entity.AdvancementEntityPredicateDeserializer;
import net.minecraft.predicate.entity.AdvancementEntityPredicateSerializer;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class ChanneledLightningCriterion extends AbstractCriterion<ChanneledLightningCriterion.Conditions> {
	static final Identifier ID = new Identifier("channeled_lightning");

	@Override
	public Identifier getId() {
		return ID;
	}

	public ChanneledLightningCriterion.Conditions conditionsFromJson(
		JsonObject jsonObject, EntityPredicate.Extended extended, AdvancementEntityPredicateDeserializer advancementEntityPredicateDeserializer
	) {
		EntityPredicate.Extended[] extendeds = EntityPredicate.Extended.requireInJson(jsonObject, "victims", advancementEntityPredicateDeserializer);
		return new ChanneledLightningCriterion.Conditions(extended, extendeds);
	}

	public void trigger(ServerPlayerEntity player, Collection<? extends Entity> victims) {
		List<LootContext> list = (List<LootContext>)victims.stream()
			.map(entity -> EntityPredicate.createAdvancementEntityLootContext(player, entity))
			.collect(Collectors.toList());
		this.test(player, conditions -> conditions.matches(list));
	}

	public static class Conditions extends AbstractCriterionConditions {
		private final EntityPredicate.Extended[] victims;

		public Conditions(EntityPredicate.Extended player, EntityPredicate.Extended[] victims) {
			super(ChanneledLightningCriterion.ID, player);
			this.victims = victims;
		}

		public static ChanneledLightningCriterion.Conditions create(EntityPredicate... victims) {
			return new ChanneledLightningCriterion.Conditions(
				EntityPredicate.Extended.EMPTY,
				(EntityPredicate.Extended[])Stream.of(victims).map(EntityPredicate.Extended::ofLegacy).toArray(EntityPredicate.Extended[]::new)
			);
		}

		public boolean matches(Collection<? extends LootContext> victims) {
			for (EntityPredicate.Extended extended : this.victims) {
				boolean bl = false;

				for (LootContext lootContext : victims) {
					if (extended.test(lootContext)) {
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
		public JsonObject toJson(AdvancementEntityPredicateSerializer predicateSerializer) {
			JsonObject jsonObject = super.toJson(predicateSerializer);
			jsonObject.add("victims", EntityPredicate.Extended.toPredicatesJsonArray(this.victims, predicateSerializer));
			return jsonObject;
		}
	}
}
