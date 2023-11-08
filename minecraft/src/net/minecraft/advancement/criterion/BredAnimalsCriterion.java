package net.minecraft.advancement.criterion;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.advancement.AdvancementCriterion;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.loot.context.LootContext;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.predicate.entity.LootContextPredicate;
import net.minecraft.predicate.entity.LootContextPredicateValidator;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.dynamic.Codecs;

public class BredAnimalsCriterion extends AbstractCriterion<BredAnimalsCriterion.Conditions> {
	@Override
	public Codec<BredAnimalsCriterion.Conditions> getConditionsCodec() {
		return BredAnimalsCriterion.Conditions.CODEC;
	}

	public void trigger(ServerPlayerEntity player, AnimalEntity parent, AnimalEntity partner, @Nullable PassiveEntity child) {
		LootContext lootContext = EntityPredicate.createAdvancementEntityLootContext(player, parent);
		LootContext lootContext2 = EntityPredicate.createAdvancementEntityLootContext(player, partner);
		LootContext lootContext3 = child != null ? EntityPredicate.createAdvancementEntityLootContext(player, child) : null;
		this.trigger(player, conditions -> conditions.matches(lootContext, lootContext2, lootContext3));
	}

	public static record Conditions(
		Optional<LootContextPredicate> player, Optional<LootContextPredicate> parent, Optional<LootContextPredicate> partner, Optional<LootContextPredicate> child
	) implements AbstractCriterion.Conditions {
		public static final Codec<BredAnimalsCriterion.Conditions> CODEC = RecordCodecBuilder.create(
			instance -> instance.group(
						Codecs.createStrictOptionalFieldCodec(EntityPredicate.LOOT_CONTEXT_PREDICATE_CODEC, "player")
							.forGetter(BredAnimalsCriterion.Conditions::getPlayerPredicate),
						Codecs.createStrictOptionalFieldCodec(EntityPredicate.LOOT_CONTEXT_PREDICATE_CODEC, "parent").forGetter(BredAnimalsCriterion.Conditions::parent),
						Codecs.createStrictOptionalFieldCodec(EntityPredicate.LOOT_CONTEXT_PREDICATE_CODEC, "partner").forGetter(BredAnimalsCriterion.Conditions::partner),
						Codecs.createStrictOptionalFieldCodec(EntityPredicate.LOOT_CONTEXT_PREDICATE_CODEC, "child").forGetter(BredAnimalsCriterion.Conditions::child)
					)
					.apply(instance, BredAnimalsCriterion.Conditions::new)
		);

		public static AdvancementCriterion<BredAnimalsCriterion.Conditions> any() {
			return Criteria.BRED_ANIMALS.create(new BredAnimalsCriterion.Conditions(Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()));
		}

		public static AdvancementCriterion<BredAnimalsCriterion.Conditions> create(EntityPredicate.Builder child) {
			return Criteria.BRED_ANIMALS
				.create(
					new BredAnimalsCriterion.Conditions(
						Optional.empty(), Optional.empty(), Optional.empty(), Optional.of(EntityPredicate.contextPredicateFromEntityPredicate(child))
					)
				);
		}

		public static AdvancementCriterion<BredAnimalsCriterion.Conditions> create(
			Optional<EntityPredicate> parent, Optional<EntityPredicate> partner, Optional<EntityPredicate> child
		) {
			return Criteria.BRED_ANIMALS
				.create(
					new BredAnimalsCriterion.Conditions(
						Optional.empty(),
						EntityPredicate.contextPredicateFromEntityPredicate(parent),
						EntityPredicate.contextPredicateFromEntityPredicate(partner),
						EntityPredicate.contextPredicateFromEntityPredicate(child)
					)
				);
		}

		public boolean matches(LootContext parentContext, LootContext partnerContext, @Nullable LootContext childContext) {
			return !this.child.isPresent() || childContext != null && ((LootContextPredicate)this.child.get()).test(childContext)
				? parentMatches(this.parent, parentContext) && parentMatches(this.partner, partnerContext)
					|| parentMatches(this.parent, partnerContext) && parentMatches(this.partner, parentContext)
				: false;
		}

		private static boolean parentMatches(Optional<LootContextPredicate> parent, LootContext parentContext) {
			return parent.isEmpty() || ((LootContextPredicate)parent.get()).test(parentContext);
		}

		@Override
		public void validate(LootContextPredicateValidator validator) {
			AbstractCriterion.Conditions.super.validate(validator);
			validator.validateEntityPredicate(this.parent, ".parent");
			validator.validateEntityPredicate(this.partner, ".partner");
			validator.validateEntityPredicate(this.child, ".child");
		}

		@Override
		public Optional<LootContextPredicate> getPlayerPredicate() {
			return this.player;
		}
	}
}
