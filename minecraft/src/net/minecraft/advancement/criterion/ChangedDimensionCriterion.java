package net.minecraft.advancement.criterion;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.advancement.AdvancementCriterion;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.predicate.entity.LootContextPredicate;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;

public class ChangedDimensionCriterion extends AbstractCriterion<ChangedDimensionCriterion.Conditions> {
	@Override
	public Codec<ChangedDimensionCriterion.Conditions> getConditionsCodec() {
		return ChangedDimensionCriterion.Conditions.CODEC;
	}

	public void trigger(ServerPlayerEntity player, RegistryKey<World> from, RegistryKey<World> to) {
		this.trigger(player, conditions -> conditions.matches(from, to));
	}

	public static record Conditions(Optional<LootContextPredicate> player, Optional<RegistryKey<World>> from, Optional<RegistryKey<World>> to)
		implements AbstractCriterion.Conditions {
		public static final Codec<ChangedDimensionCriterion.Conditions> CODEC = RecordCodecBuilder.create(
			instance -> instance.group(
						EntityPredicate.LOOT_CONTEXT_PREDICATE_CODEC.optionalFieldOf("player").forGetter(ChangedDimensionCriterion.Conditions::player),
						RegistryKey.createCodec(RegistryKeys.WORLD).optionalFieldOf("from").forGetter(ChangedDimensionCriterion.Conditions::from),
						RegistryKey.createCodec(RegistryKeys.WORLD).optionalFieldOf("to").forGetter(ChangedDimensionCriterion.Conditions::to)
					)
					.apply(instance, ChangedDimensionCriterion.Conditions::new)
		);

		public static AdvancementCriterion<ChangedDimensionCriterion.Conditions> create() {
			return Criteria.CHANGED_DIMENSION.create(new ChangedDimensionCriterion.Conditions(Optional.empty(), Optional.empty(), Optional.empty()));
		}

		public static AdvancementCriterion<ChangedDimensionCriterion.Conditions> create(RegistryKey<World> from, RegistryKey<World> to) {
			return Criteria.CHANGED_DIMENSION.create(new ChangedDimensionCriterion.Conditions(Optional.empty(), Optional.of(from), Optional.of(to)));
		}

		public static AdvancementCriterion<ChangedDimensionCriterion.Conditions> to(RegistryKey<World> to) {
			return Criteria.CHANGED_DIMENSION.create(new ChangedDimensionCriterion.Conditions(Optional.empty(), Optional.empty(), Optional.of(to)));
		}

		public static AdvancementCriterion<ChangedDimensionCriterion.Conditions> from(RegistryKey<World> from) {
			return Criteria.CHANGED_DIMENSION.create(new ChangedDimensionCriterion.Conditions(Optional.empty(), Optional.of(from), Optional.empty()));
		}

		public boolean matches(RegistryKey<World> from, RegistryKey<World> to) {
			return this.from.isPresent() && this.from.get() != from ? false : !this.to.isPresent() || this.to.get() == to;
		}
	}
}
