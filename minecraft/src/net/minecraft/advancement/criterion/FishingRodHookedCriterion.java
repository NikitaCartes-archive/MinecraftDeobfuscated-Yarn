package net.minecraft.advancement.criterion;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Collection;
import java.util.Optional;
import net.minecraft.advancement.AdvancementCriterion;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.projectile.FishingBobberEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.predicate.entity.LootContextPredicate;
import net.minecraft.predicate.entity.LootContextPredicateValidator;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.server.network.ServerPlayerEntity;

public class FishingRodHookedCriterion extends AbstractCriterion<FishingRodHookedCriterion.Conditions> {
	@Override
	public Codec<FishingRodHookedCriterion.Conditions> getConditionsCodec() {
		return FishingRodHookedCriterion.Conditions.CODEC;
	}

	public void trigger(ServerPlayerEntity player, ItemStack rod, FishingBobberEntity bobber, Collection<ItemStack> fishingLoots) {
		LootContext lootContext = EntityPredicate.createAdvancementEntityLootContext(
			player, (Entity)(bobber.getHookedEntity() != null ? bobber.getHookedEntity() : bobber)
		);
		this.trigger(player, conditions -> conditions.matches(rod, lootContext, fishingLoots));
	}

	public static record Conditions(
		Optional<LootContextPredicate> player, Optional<ItemPredicate> rod, Optional<LootContextPredicate> entity, Optional<ItemPredicate> item
	) implements AbstractCriterion.Conditions {
		public static final Codec<FishingRodHookedCriterion.Conditions> CODEC = RecordCodecBuilder.create(
			instance -> instance.group(
						EntityPredicate.LOOT_CONTEXT_PREDICATE_CODEC.optionalFieldOf("player").forGetter(FishingRodHookedCriterion.Conditions::player),
						ItemPredicate.CODEC.optionalFieldOf("rod").forGetter(FishingRodHookedCriterion.Conditions::rod),
						EntityPredicate.LOOT_CONTEXT_PREDICATE_CODEC.optionalFieldOf("entity").forGetter(FishingRodHookedCriterion.Conditions::entity),
						ItemPredicate.CODEC.optionalFieldOf("item").forGetter(FishingRodHookedCriterion.Conditions::item)
					)
					.apply(instance, FishingRodHookedCriterion.Conditions::new)
		);

		public static AdvancementCriterion<FishingRodHookedCriterion.Conditions> create(
			Optional<ItemPredicate> rod, Optional<EntityPredicate> hookedEntity, Optional<ItemPredicate> caughtItem
		) {
			return Criteria.FISHING_ROD_HOOKED
				.create(new FishingRodHookedCriterion.Conditions(Optional.empty(), rod, EntityPredicate.contextPredicateFromEntityPredicate(hookedEntity), caughtItem));
		}

		public boolean matches(ItemStack rodStack, LootContext hookedEntity, Collection<ItemStack> fishingLoots) {
			if (this.rod.isPresent() && !((ItemPredicate)this.rod.get()).test(rodStack)) {
				return false;
			} else if (this.entity.isPresent() && !((LootContextPredicate)this.entity.get()).test(hookedEntity)) {
				return false;
			} else {
				if (this.item.isPresent()) {
					boolean bl = false;
					Entity entity = hookedEntity.get(LootContextParameters.THIS_ENTITY);
					if (entity instanceof ItemEntity itemEntity && ((ItemPredicate)this.item.get()).test(itemEntity.getStack())) {
						bl = true;
					}

					for (ItemStack itemStack : fishingLoots) {
						if (((ItemPredicate)this.item.get()).test(itemStack)) {
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
		}

		@Override
		public void validate(LootContextPredicateValidator validator) {
			AbstractCriterion.Conditions.super.validate(validator);
			validator.validateEntityPredicate(this.entity, ".entity");
		}
	}
}
