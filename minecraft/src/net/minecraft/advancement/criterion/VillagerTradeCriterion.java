package net.minecraft.advancement.criterion;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.advancement.AdvancementCriterion;
import net.minecraft.entity.passive.MerchantEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContext;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.predicate.entity.LootContextPredicate;
import net.minecraft.predicate.entity.LootContextPredicateValidator;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.server.network.ServerPlayerEntity;

public class VillagerTradeCriterion extends AbstractCriterion<VillagerTradeCriterion.Conditions> {
	@Override
	public Codec<VillagerTradeCriterion.Conditions> getConditionsCodec() {
		return VillagerTradeCriterion.Conditions.CODEC;
	}

	public void trigger(ServerPlayerEntity player, MerchantEntity merchant, ItemStack stack) {
		LootContext lootContext = EntityPredicate.createAdvancementEntityLootContext(player, merchant);
		this.trigger(player, conditions -> conditions.matches(lootContext, stack));
	}

	public static record Conditions(Optional<LootContextPredicate> player, Optional<LootContextPredicate> villager, Optional<ItemPredicate> item)
		implements AbstractCriterion.Conditions {
		public static final Codec<VillagerTradeCriterion.Conditions> CODEC = RecordCodecBuilder.create(
			instance -> instance.group(
						EntityPredicate.LOOT_CONTEXT_PREDICATE_CODEC.optionalFieldOf("player").forGetter(VillagerTradeCriterion.Conditions::player),
						EntityPredicate.LOOT_CONTEXT_PREDICATE_CODEC.optionalFieldOf("villager").forGetter(VillagerTradeCriterion.Conditions::villager),
						ItemPredicate.CODEC.optionalFieldOf("item").forGetter(VillagerTradeCriterion.Conditions::item)
					)
					.apply(instance, VillagerTradeCriterion.Conditions::new)
		);

		public static AdvancementCriterion<VillagerTradeCriterion.Conditions> any() {
			return Criteria.VILLAGER_TRADE.create(new VillagerTradeCriterion.Conditions(Optional.empty(), Optional.empty(), Optional.empty()));
		}

		public static AdvancementCriterion<VillagerTradeCriterion.Conditions> create(EntityPredicate.Builder playerPredicate) {
			return Criteria.VILLAGER_TRADE
				.create(
					new VillagerTradeCriterion.Conditions(
						Optional.of(EntityPredicate.contextPredicateFromEntityPredicate(playerPredicate)), Optional.empty(), Optional.empty()
					)
				);
		}

		public boolean matches(LootContext villager, ItemStack stack) {
			return this.villager.isPresent() && !((LootContextPredicate)this.villager.get()).test(villager)
				? false
				: !this.item.isPresent() || ((ItemPredicate)this.item.get()).test(stack);
		}

		@Override
		public void validate(LootContextPredicateValidator validator) {
			AbstractCriterion.Conditions.super.validate(validator);
			validator.validateEntityPredicate(this.villager, ".villager");
		}
	}
}
