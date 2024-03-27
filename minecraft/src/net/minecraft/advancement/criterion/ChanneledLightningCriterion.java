package net.minecraft.advancement.criterion;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import net.minecraft.advancement.AdvancementCriterion;
import net.minecraft.entity.Entity;
import net.minecraft.loot.context.LootContext;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.predicate.entity.LootContextPredicate;
import net.minecraft.predicate.entity.LootContextPredicateValidator;
import net.minecraft.server.network.ServerPlayerEntity;

public class ChanneledLightningCriterion extends AbstractCriterion<ChanneledLightningCriterion.Conditions> {
	@Override
	public Codec<ChanneledLightningCriterion.Conditions> getConditionsCodec() {
		return ChanneledLightningCriterion.Conditions.CODEC;
	}

	public void trigger(ServerPlayerEntity player, Collection<? extends Entity> victims) {
		List<LootContext> list = (List<LootContext>)victims.stream()
			.map(entity -> EntityPredicate.createAdvancementEntityLootContext(player, entity))
			.collect(Collectors.toList());
		this.trigger(player, conditions -> conditions.matches(list));
	}

	public static record Conditions(Optional<LootContextPredicate> player, List<LootContextPredicate> victims) implements AbstractCriterion.Conditions {
		public static final Codec<ChanneledLightningCriterion.Conditions> CODEC = RecordCodecBuilder.create(
			instance -> instance.group(
						EntityPredicate.LOOT_CONTEXT_PREDICATE_CODEC.optionalFieldOf("player").forGetter(ChanneledLightningCriterion.Conditions::player),
						EntityPredicate.LOOT_CONTEXT_PREDICATE_CODEC.listOf().optionalFieldOf("victims", List.of()).forGetter(ChanneledLightningCriterion.Conditions::victims)
					)
					.apply(instance, ChanneledLightningCriterion.Conditions::new)
		);

		public static AdvancementCriterion<ChanneledLightningCriterion.Conditions> create(EntityPredicate.Builder... victims) {
			return Criteria.CHANNELED_LIGHTNING
				.create(new ChanneledLightningCriterion.Conditions(Optional.empty(), EntityPredicate.contextPredicateFromEntityPredicates(victims)));
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
		public void validate(LootContextPredicateValidator validator) {
			AbstractCriterion.Conditions.super.validate(validator);
			validator.validateEntityPredicates(this.victims, ".victims");
		}
	}
}
