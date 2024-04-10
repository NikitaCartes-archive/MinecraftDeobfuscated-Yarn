package net.minecraft.data.server.advancement.onetwentyone;

import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementEntry;
import net.minecraft.advancement.AdvancementFrame;
import net.minecraft.advancement.AdvancementRewards;
import net.minecraft.advancement.criterion.FallAfterExplosionCriterion;
import net.minecraft.advancement.criterion.ItemCriterion;
import net.minecraft.advancement.criterion.OnKilledCriterion;
import net.minecraft.advancement.criterion.PlayerHurtEntityCriterion;
import net.minecraft.advancement.criterion.RecipeCraftedCriterion;
import net.minecraft.advancement.criterion.TickCriterion;
import net.minecraft.block.Blocks;
import net.minecraft.block.BulbBlock;
import net.minecraft.block.VaultBlock;
import net.minecraft.data.server.advancement.AdvancementTabGenerator;
import net.minecraft.data.server.advancement.vanilla.VanillaAdventureTabAdvancementGenerator;
import net.minecraft.data.server.advancement.vanilla.VanillaHusbandryTabAdvancementGenerator;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Items;
import net.minecraft.predicate.BlockPredicate;
import net.minecraft.predicate.DamagePredicate;
import net.minecraft.predicate.NumberRange;
import net.minecraft.predicate.StatePredicate;
import net.minecraft.predicate.TagPredicate;
import net.minecraft.predicate.entity.DamageSourcePredicate;
import net.minecraft.predicate.entity.DistancePredicate;
import net.minecraft.predicate.entity.EntityEquipmentPredicate;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.predicate.entity.LocationPredicate;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.DamageTypeTags;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.world.gen.structure.StructureKeys;

public class OneTwentyOneAdventureTabAdvancementGenerator implements AdvancementTabGenerator {
	@Override
	public void accept(RegistryWrapper.WrapperLookup lookup, Consumer<AdvancementEntry> exporter) {
		AdvancementEntry advancementEntry = AdvancementTabGenerator.reference("adventure/root");
		VanillaAdventureTabAdvancementGenerator.createKillMobAdvancements(
			advancementEntry,
			exporter,
			(List<EntityType<?>>)Stream.concat(VanillaAdventureTabAdvancementGenerator.MONSTERS.stream(), Stream.of(EntityType.BREEZE, EntityType.BOGGED))
				.collect(Collectors.toList())
		);
		AdvancementEntry advancementEntry2 = Advancement.Builder.create()
			.parent(advancementEntry)
			.display(
				Blocks.CHISELED_TUFF,
				Text.translatable("advancements.adventure.minecraft_trials_edition.title"),
				Text.translatable("advancements.adventure.minecraft_trials_edition.description"),
				null,
				AdvancementFrame.TASK,
				true,
				true,
				false
			)
			.criterion(
				"minecraft_trials_edition",
				TickCriterion.Conditions.createLocation(
					LocationPredicate.Builder.createStructure(lookup.getWrapperOrThrow(RegistryKeys.STRUCTURE).getOrThrow(StructureKeys.TRIAL_CHAMBERS))
				)
			)
			.build(exporter, "adventure/minecraft_trials_edition");
		Advancement.Builder.create()
			.parent(advancementEntry2)
			.display(
				Items.COPPER_BULB,
				Text.translatable("advancements.adventure.lighten_up.title"),
				Text.translatable("advancements.adventure.lighten_up.description"),
				null,
				AdvancementFrame.TASK,
				true,
				true,
				false
			)
			.criterion(
				"lighten_up",
				ItemCriterion.Conditions.createItemUsedOnBlock(
					LocationPredicate.Builder.create()
						.block(
							BlockPredicate.Builder.create()
								.blocks(
									Blocks.OXIDIZED_COPPER_BULB,
									Blocks.WEATHERED_COPPER_BULB,
									Blocks.EXPOSED_COPPER_BULB,
									Blocks.WAXED_OXIDIZED_COPPER_BULB,
									Blocks.WAXED_WEATHERED_COPPER_BULB,
									Blocks.WAXED_EXPOSED_COPPER_BULB
								)
								.state(StatePredicate.Builder.create().exactMatch(BulbBlock.LIT, true))
						),
					ItemPredicate.Builder.create().items(VanillaHusbandryTabAdvancementGenerator.AXE_ITEMS)
				)
			)
			.build(exporter, "adventure/lighten_up");
		AdvancementEntry advancementEntry3 = Advancement.Builder.create()
			.parent(advancementEntry2)
			.display(
				Items.TRIAL_KEY,
				Text.translatable("advancements.adventure.under_lock_and_key.title"),
				Text.translatable("advancements.adventure.under_lock_and_key.description"),
				null,
				AdvancementFrame.TASK,
				true,
				true,
				false
			)
			.criterion(
				"under_lock_and_key",
				ItemCriterion.Conditions.createItemUsedOnBlock(
					LocationPredicate.Builder.create().block(BlockPredicate.Builder.create().blocks(Blocks.VAULT)), ItemPredicate.Builder.create().items(Items.TRIAL_KEY)
				)
			)
			.build(exporter, "adventure/under_lock_and_key");
		Advancement.Builder.create()
			.parent(advancementEntry3)
			.display(
				Items.OMINOUS_TRIAL_KEY,
				Text.translatable("advancements.adventure.revaulting.title"),
				Text.translatable("advancements.adventure.revaulting.description"),
				null,
				AdvancementFrame.GOAL,
				true,
				true,
				false
			)
			.criterion(
				"revaulting",
				ItemCriterion.Conditions.createItemUsedOnBlock(
					LocationPredicate.Builder.create()
						.block(BlockPredicate.Builder.create().blocks(Blocks.VAULT).state(StatePredicate.Builder.create().exactMatch(VaultBlock.OMINOUS, true))),
					ItemPredicate.Builder.create().items(Items.OMINOUS_TRIAL_KEY)
				)
			)
			.build(exporter, "adventure/revaulting");
		Advancement.Builder.create()
			.parent(advancementEntry2)
			.display(
				Items.WIND_CHARGE,
				Text.translatable("advancements.adventure.blowback.title"),
				Text.translatable("advancements.adventure.blowback.description"),
				null,
				AdvancementFrame.CHALLENGE,
				true,
				true,
				false
			)
			.rewards(AdvancementRewards.Builder.experience(40))
			.criterion(
				"blowback",
				OnKilledCriterion.Conditions.createPlayerKilledEntity(
					EntityPredicate.Builder.create().type(EntityType.BREEZE),
					DamageSourcePredicate.Builder.create()
						.tag(TagPredicate.expected(DamageTypeTags.IS_PROJECTILE))
						.directEntity(EntityPredicate.Builder.create().type(EntityType.BREEZE_WIND_CHARGE))
				)
			)
			.build(exporter, "adventure/blowback");
		Advancement.Builder.create()
			.parent(advancementEntry)
			.display(
				Items.CRAFTER,
				Text.translatable("advancements.adventure.crafters_crafting_crafters.title"),
				Text.translatable("advancements.adventure.crafters_crafting_crafters.description"),
				null,
				AdvancementFrame.TASK,
				true,
				true,
				false
			)
			.criterion("crafter_crafted_crafter", RecipeCraftedCriterion.Conditions.createCrafterRecipeCrafted(new Identifier("minecraft:crafter")))
			.build(exporter, "adventure/crafters_crafting_crafters");
		Advancement.Builder.create()
			.parent(advancementEntry2)
			.display(
				Items.WIND_CHARGE,
				Text.translatable("advancements.adventure.who_needs_rockets.title"),
				Text.translatable("advancements.adventure.who_needs_rockets.description"),
				null,
				AdvancementFrame.TASK,
				true,
				true,
				false
			)
			.criterion(
				"who_needs_rockets",
				FallAfterExplosionCriterion.Conditions.create(
					DistancePredicate.y(NumberRange.DoubleRange.atLeast(7.0)), EntityPredicate.Builder.create().type(EntityType.WIND_CHARGE)
				)
			)
			.build(exporter, "adventure/who_needs_rockets");
		Advancement.Builder.create()
			.parent(advancementEntry2)
			.display(
				Items.MACE,
				Text.translatable("advancements.adventure.overoverkill.title"),
				Text.translatable("advancements.adventure.overoverkill.description"),
				null,
				AdvancementFrame.CHALLENGE,
				true,
				true,
				false
			)
			.rewards(AdvancementRewards.Builder.experience(50))
			.criterion(
				"overoverkill",
				PlayerHurtEntityCriterion.Conditions.create(
					DamagePredicate.Builder.create()
						.dealt(NumberRange.DoubleRange.atLeast(100.0))
						.type(
							DamageSourcePredicate.Builder.create()
								.tag(TagPredicate.expected(DamageTypeTags.IS_PLAYER_ATTACK))
								.directEntity(
									EntityPredicate.Builder.create()
										.type(EntityType.PLAYER)
										.equipment(EntityEquipmentPredicate.Builder.create().mainhand(ItemPredicate.Builder.create().items(Items.MACE)))
								)
						)
				)
			)
			.build(exporter, "adventure/overoverkill");
	}
}
