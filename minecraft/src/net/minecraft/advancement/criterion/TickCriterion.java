package net.minecraft.advancement.criterion;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.advancement.AdvancementCriterion;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.predicate.BlockPredicate;
import net.minecraft.predicate.NumberRange;
import net.minecraft.predicate.entity.EntityEquipmentPredicate;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.predicate.entity.LocationPredicate;
import net.minecraft.predicate.entity.LootContextPredicate;
import net.minecraft.predicate.entity.PlayerPredicate;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.stat.Stats;
import net.minecraft.util.Identifier;
import net.minecraft.util.dynamic.Codecs;

public class TickCriterion extends AbstractCriterion<TickCriterion.Conditions> {
	@Override
	public Codec<TickCriterion.Conditions> getConditionsCodec() {
		return TickCriterion.Conditions.CODEC;
	}

	public void trigger(ServerPlayerEntity player) {
		this.trigger(player, conditions -> true);
	}

	public static record Conditions(Optional<LootContextPredicate> player) implements AbstractCriterion.Conditions {
		public static final Codec<TickCriterion.Conditions> CODEC = RecordCodecBuilder.create(
			instance -> instance.group(
						Codecs.createStrictOptionalFieldCodec(EntityPredicate.LOOT_CONTEXT_PREDICATE_CODEC, "player").forGetter(TickCriterion.Conditions::player)
					)
					.apply(instance, TickCriterion.Conditions::new)
		);

		public static AdvancementCriterion<TickCriterion.Conditions> createLocation(LocationPredicate.Builder location) {
			return Criteria.LOCATION
				.create(new TickCriterion.Conditions(Optional.of(EntityPredicate.contextPredicateFromEntityPredicate(EntityPredicate.Builder.create().location(location)))));
		}

		public static AdvancementCriterion<TickCriterion.Conditions> createLocation(EntityPredicate.Builder entity) {
			return Criteria.LOCATION.create(new TickCriterion.Conditions(Optional.of(EntityPredicate.asLootContextPredicate(entity.build()))));
		}

		public static AdvancementCriterion<TickCriterion.Conditions> createLocation(Optional<EntityPredicate> entity) {
			return Criteria.LOCATION.create(new TickCriterion.Conditions(EntityPredicate.contextPredicateFromEntityPredicate(entity)));
		}

		public static AdvancementCriterion<TickCriterion.Conditions> createSleptInBed() {
			return Criteria.SLEPT_IN_BED.create(new TickCriterion.Conditions(Optional.empty()));
		}

		public static AdvancementCriterion<TickCriterion.Conditions> createGetPeeled() {
			return Criteria.GET_PEELED.create(new TickCriterion.Conditions(Optional.empty()));
		}

		public static AdvancementCriterion<TickCriterion.Conditions> createEatArmor() {
			return Criteria.EAT_ARMOR.create(new TickCriterion.Conditions(Optional.empty()));
		}

		public static AdvancementCriterion<TickCriterion.Conditions> createHeroOfTheVillage() {
			return Criteria.HERO_OF_THE_VILLAGE.create(new TickCriterion.Conditions(Optional.empty()));
		}

		public static AdvancementCriterion<TickCriterion.Conditions> createAvoidVibration() {
			return Criteria.AVOID_VIBRATION.create(new TickCriterion.Conditions(Optional.empty()));
		}

		public static AdvancementCriterion<TickCriterion.Conditions> createTick() {
			return Criteria.TICK.create(new TickCriterion.Conditions(Optional.empty()));
		}

		public static AdvancementCriterion<TickCriterion.Conditions> createRumblePlant() {
			return Criteria.RUMBLE_PLANT.create(new TickCriterion.Conditions(Optional.empty()));
		}

		public static AdvancementCriterion<TickCriterion.Conditions> createCompostStaff() {
			return Criteria.COMPOST_STAFF.create(new TickCriterion.Conditions(Optional.empty()));
		}

		public static AdvancementCriterion<TickCriterion.Conditions> createSaidPotato(int times) {
			PlayerPredicate.Builder builder = new PlayerPredicate.Builder()
				.stat(Stats.CUSTOM, (RegistryEntry.Reference<Identifier>)Registries.CUSTOM_STAT.getEntry(Stats.SAID_POTATO).orElseThrow(), NumberRange.IntRange.atLeast(99));
			LootContextPredicate lootContextPredicate = EntityPredicate.contextPredicateFromEntityPredicate(
				EntityPredicate.Builder.create().typeSpecific(builder.build())
			);
			return Criteria.SAID_POTATO.create(new TickCriterion.Conditions(Optional.of(lootContextPredicate)));
		}

		public static AdvancementCriterion<TickCriterion.Conditions> createBringHomeCorruption() {
			return Criteria.BRING_HOME_CORRUPTION.create(new TickCriterion.Conditions(Optional.empty()));
		}

		public static AdvancementCriterion<TickCriterion.Conditions> createLocation(Block block, Item item) {
			return createLocation(
				EntityPredicate.Builder.create()
					.equipment(EntityEquipmentPredicate.Builder.create().feet(ItemPredicate.Builder.create().items(item)))
					.steppingOn(LocationPredicate.Builder.create().block(BlockPredicate.Builder.create().blocks(block)))
			);
		}
	}
}
