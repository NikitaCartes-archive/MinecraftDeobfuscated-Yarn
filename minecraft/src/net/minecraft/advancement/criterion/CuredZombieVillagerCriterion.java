package net.minecraft.advancement.criterion;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.advancement.AdvancementCriterion;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.loot.context.LootContext;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.predicate.entity.LootContextPredicate;
import net.minecraft.predicate.entity.LootContextPredicateValidator;
import net.minecraft.server.network.ServerPlayerEntity;

public class CuredZombieVillagerCriterion extends AbstractCriterion<CuredZombieVillagerCriterion.Conditions> {
	@Override
	public Codec<CuredZombieVillagerCriterion.Conditions> getConditionsCodec() {
		return CuredZombieVillagerCriterion.Conditions.CODEC;
	}

	public void trigger(ServerPlayerEntity player, ZombieEntity zombie, VillagerEntity villager) {
		LootContext lootContext = EntityPredicate.createAdvancementEntityLootContext(player, zombie);
		LootContext lootContext2 = EntityPredicate.createAdvancementEntityLootContext(player, villager);
		this.trigger(player, conditions -> conditions.matches(lootContext, lootContext2));
	}

	public static record Conditions(Optional<LootContextPredicate> player, Optional<LootContextPredicate> zombie, Optional<LootContextPredicate> villager)
		implements AbstractCriterion.Conditions {
		public static final Codec<CuredZombieVillagerCriterion.Conditions> CODEC = RecordCodecBuilder.create(
			instance -> instance.group(
						EntityPredicate.LOOT_CONTEXT_PREDICATE_CODEC.optionalFieldOf("player").forGetter(CuredZombieVillagerCriterion.Conditions::player),
						EntityPredicate.LOOT_CONTEXT_PREDICATE_CODEC.optionalFieldOf("zombie").forGetter(CuredZombieVillagerCriterion.Conditions::zombie),
						EntityPredicate.LOOT_CONTEXT_PREDICATE_CODEC.optionalFieldOf("villager").forGetter(CuredZombieVillagerCriterion.Conditions::villager)
					)
					.apply(instance, CuredZombieVillagerCriterion.Conditions::new)
		);

		public static AdvancementCriterion<CuredZombieVillagerCriterion.Conditions> any() {
			return Criteria.CURED_ZOMBIE_VILLAGER.create(new CuredZombieVillagerCriterion.Conditions(Optional.empty(), Optional.empty(), Optional.empty()));
		}

		public boolean matches(LootContext zombie, LootContext villager) {
			return this.zombie.isPresent() && !((LootContextPredicate)this.zombie.get()).test(zombie)
				? false
				: !this.villager.isPresent() || ((LootContextPredicate)this.villager.get()).test(villager);
		}

		@Override
		public void validate(LootContextPredicateValidator validator) {
			AbstractCriterion.Conditions.super.validate(validator);
			validator.validateEntityPredicate(this.zombie, ".zombie");
			validator.validateEntityPredicate(this.villager, ".villager");
		}
	}
}
