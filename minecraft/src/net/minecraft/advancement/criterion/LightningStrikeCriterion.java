package net.minecraft.advancement.criterion;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import net.minecraft.advancement.AdvancementCriterion;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LightningEntity;
import net.minecraft.loot.context.LootContext;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.predicate.entity.LootContextPredicate;
import net.minecraft.predicate.entity.LootContextPredicateValidator;
import net.minecraft.server.network.ServerPlayerEntity;

public class LightningStrikeCriterion extends AbstractCriterion<LightningStrikeCriterion.Conditions> {
	@Override
	public Codec<LightningStrikeCriterion.Conditions> getConditionsCodec() {
		return LightningStrikeCriterion.Conditions.CODEC;
	}

	public void trigger(ServerPlayerEntity player, LightningEntity lightning, List<Entity> bystanders) {
		List<LootContext> list = (List<LootContext>)bystanders.stream()
			.map(bystander -> EntityPredicate.createAdvancementEntityLootContext(player, bystander))
			.collect(Collectors.toList());
		LootContext lootContext = EntityPredicate.createAdvancementEntityLootContext(player, lightning);
		this.trigger(player, conditions -> conditions.test(lootContext, list));
	}

	public static record Conditions(Optional<LootContextPredicate> player, Optional<LootContextPredicate> lightning, Optional<LootContextPredicate> bystander)
		implements AbstractCriterion.Conditions {
		public static final Codec<LightningStrikeCriterion.Conditions> CODEC = RecordCodecBuilder.create(
			instance -> instance.group(
						EntityPredicate.LOOT_CONTEXT_PREDICATE_CODEC.optionalFieldOf("player").forGetter(LightningStrikeCriterion.Conditions::player),
						EntityPredicate.LOOT_CONTEXT_PREDICATE_CODEC.optionalFieldOf("lightning").forGetter(LightningStrikeCriterion.Conditions::lightning),
						EntityPredicate.LOOT_CONTEXT_PREDICATE_CODEC.optionalFieldOf("bystander").forGetter(LightningStrikeCriterion.Conditions::bystander)
					)
					.apply(instance, LightningStrikeCriterion.Conditions::new)
		);

		public static AdvancementCriterion<LightningStrikeCriterion.Conditions> create(Optional<EntityPredicate> lightning, Optional<EntityPredicate> bystander) {
			return Criteria.LIGHTNING_STRIKE
				.create(
					new LightningStrikeCriterion.Conditions(
						Optional.empty(), EntityPredicate.contextPredicateFromEntityPredicate(lightning), EntityPredicate.contextPredicateFromEntityPredicate(bystander)
					)
				);
		}

		public boolean test(LootContext lightning, List<LootContext> bystanders) {
			return this.lightning.isPresent() && !((LootContextPredicate)this.lightning.get()).test(lightning)
				? false
				: !this.bystander.isPresent() || !bystanders.stream().noneMatch(((LootContextPredicate)this.bystander.get())::test);
		}

		@Override
		public void validate(LootContextPredicateValidator validator) {
			AbstractCriterion.Conditions.super.validate(validator);
			validator.validateEntityPredicate(this.lightning, ".lightning");
			validator.validateEntityPredicate(this.bystander, ".bystander");
		}
	}
}
