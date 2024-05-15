package net.minecraft.advancement.criterion;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.advancement.AdvancementCriterion;
import net.minecraft.potion.Potion;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.predicate.entity.LootContextPredicate;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.network.ServerPlayerEntity;

public class BrewedPotionCriterion extends AbstractCriterion<BrewedPotionCriterion.Conditions> {
	@Override
	public Codec<BrewedPotionCriterion.Conditions> getConditionsCodec() {
		return BrewedPotionCriterion.Conditions.CODEC;
	}

	public void trigger(ServerPlayerEntity player, RegistryEntry<Potion> potion) {
		this.trigger(player, conditions -> conditions.matches(potion));
	}

	public static record Conditions(Optional<LootContextPredicate> player, Optional<RegistryEntry<Potion>> potion) implements AbstractCriterion.Conditions {
		public static final Codec<BrewedPotionCriterion.Conditions> CODEC = RecordCodecBuilder.create(
			instance -> instance.group(
						EntityPredicate.LOOT_CONTEXT_PREDICATE_CODEC.optionalFieldOf("player").forGetter(BrewedPotionCriterion.Conditions::player),
						Potion.CODEC.optionalFieldOf("potion").forGetter(BrewedPotionCriterion.Conditions::potion)
					)
					.apply(instance, BrewedPotionCriterion.Conditions::new)
		);

		public static AdvancementCriterion<BrewedPotionCriterion.Conditions> any() {
			return Criteria.BREWED_POTION.create(new BrewedPotionCriterion.Conditions(Optional.empty(), Optional.empty()));
		}

		public boolean matches(RegistryEntry<Potion> potion) {
			return !this.potion.isPresent() || ((RegistryEntry)this.potion.get()).equals(potion);
		}
	}
}
