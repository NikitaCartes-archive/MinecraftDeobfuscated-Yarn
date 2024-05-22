package net.minecraft.data.server.advancement.vanilla;

import java.util.Optional;
import java.util.function.Consumer;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementEntry;
import net.minecraft.advancement.AdvancementFrame;
import net.minecraft.advancement.AdvancementRequirements;
import net.minecraft.advancement.AdvancementRewards;
import net.minecraft.advancement.criterion.BrewedPotionCriterion;
import net.minecraft.advancement.criterion.ChangedDimensionCriterion;
import net.minecraft.advancement.criterion.ConstructBeaconCriterion;
import net.minecraft.advancement.criterion.EffectsChangedCriterion;
import net.minecraft.advancement.criterion.InventoryChangedCriterion;
import net.minecraft.advancement.criterion.ItemCriterion;
import net.minecraft.advancement.criterion.ItemDurabilityChangedCriterion;
import net.minecraft.advancement.criterion.OnKilledCriterion;
import net.minecraft.advancement.criterion.PlayerGeneratesContainerLootCriterion;
import net.minecraft.advancement.criterion.PlayerInteractedWithEntityCriterion;
import net.minecraft.advancement.criterion.SummonedEntityCriterion;
import net.minecraft.advancement.criterion.ThrownItemPickedUpByEntityCriterion;
import net.minecraft.advancement.criterion.TickCriterion;
import net.minecraft.advancement.criterion.TravelCriterion;
import net.minecraft.block.Blocks;
import net.minecraft.block.RespawnAnchorBlock;
import net.minecraft.data.server.advancement.AdvancementTabGenerator;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.PiglinBrain;
import net.minecraft.item.Items;
import net.minecraft.loot.LootTables;
import net.minecraft.loot.condition.EntityPropertiesLootCondition;
import net.minecraft.loot.context.LootContext;
import net.minecraft.predicate.BlockPredicate;
import net.minecraft.predicate.NumberRange;
import net.minecraft.predicate.StatePredicate;
import net.minecraft.predicate.TagPredicate;
import net.minecraft.predicate.entity.DamageSourcePredicate;
import net.minecraft.predicate.entity.DistancePredicate;
import net.minecraft.predicate.entity.EntityEffectPredicate;
import net.minecraft.predicate.entity.EntityEquipmentPredicate;
import net.minecraft.predicate.entity.EntityFlagsPredicate;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.predicate.entity.LocationPredicate;
import net.minecraft.predicate.entity.LootContextPredicate;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.DamageTypeTags;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import net.minecraft.world.biome.source.MultiNoiseBiomeSourceParameterList;
import net.minecraft.world.gen.structure.StructureKeys;

public class VanillaNetherTabAdvancementGenerator implements AdvancementTabGenerator {
	private static final LootContextPredicate PIGLIN_DISTRACTION_PREDICATE = LootContextPredicate.create(
		EntityPropertiesLootCondition.builder(
				LootContext.EntityTarget.THIS,
				EntityPredicate.Builder.create().equipment(EntityEquipmentPredicate.Builder.create().head(ItemPredicate.Builder.create().items(Items.GOLDEN_HELMET)))
			)
			.invert()
			.build(),
		EntityPropertiesLootCondition.builder(
				LootContext.EntityTarget.THIS,
				EntityPredicate.Builder.create().equipment(EntityEquipmentPredicate.Builder.create().chest(ItemPredicate.Builder.create().items(Items.GOLDEN_CHESTPLATE)))
			)
			.invert()
			.build(),
		EntityPropertiesLootCondition.builder(
				LootContext.EntityTarget.THIS,
				EntityPredicate.Builder.create().equipment(EntityEquipmentPredicate.Builder.create().legs(ItemPredicate.Builder.create().items(Items.GOLDEN_LEGGINGS)))
			)
			.invert()
			.build(),
		EntityPropertiesLootCondition.builder(
				LootContext.EntityTarget.THIS,
				EntityPredicate.Builder.create().equipment(EntityEquipmentPredicate.Builder.create().feet(ItemPredicate.Builder.create().items(Items.GOLDEN_BOOTS)))
			)
			.invert()
			.build()
	);

	@Override
	public void accept(RegistryWrapper.WrapperLookup lookup, Consumer<AdvancementEntry> exporter) {
		AdvancementEntry advancementEntry = Advancement.Builder.create()
			.display(
				Blocks.RED_NETHER_BRICKS,
				Text.translatable("advancements.nether.root.title"),
				Text.translatable("advancements.nether.root.description"),
				Identifier.ofVanilla("textures/gui/advancements/backgrounds/nether.png"),
				AdvancementFrame.TASK,
				false,
				false,
				false
			)
			.criterion("entered_nether", ChangedDimensionCriterion.Conditions.to(World.NETHER))
			.build(exporter, "nether/root");
		AdvancementEntry advancementEntry2 = Advancement.Builder.create()
			.parent(advancementEntry)
			.display(
				Items.FIRE_CHARGE,
				Text.translatable("advancements.nether.return_to_sender.title"),
				Text.translatable("advancements.nether.return_to_sender.description"),
				null,
				AdvancementFrame.CHALLENGE,
				true,
				true,
				false
			)
			.rewards(AdvancementRewards.Builder.experience(50))
			.criterion(
				"killed_ghast",
				OnKilledCriterion.Conditions.createPlayerKilledEntity(
					EntityPredicate.Builder.create().type(EntityType.GHAST),
					DamageSourcePredicate.Builder.create()
						.tag(TagPredicate.expected(DamageTypeTags.IS_PROJECTILE))
						.directEntity(EntityPredicate.Builder.create().type(EntityType.FIREBALL))
				)
			)
			.build(exporter, "nether/return_to_sender");
		AdvancementEntry advancementEntry3 = Advancement.Builder.create()
			.parent(advancementEntry)
			.display(
				Blocks.NETHER_BRICKS,
				Text.translatable("advancements.nether.find_fortress.title"),
				Text.translatable("advancements.nether.find_fortress.description"),
				null,
				AdvancementFrame.TASK,
				true,
				true,
				false
			)
			.criterion(
				"fortress",
				TickCriterion.Conditions.createLocation(
					LocationPredicate.Builder.createStructure(lookup.getWrapperOrThrow(RegistryKeys.STRUCTURE).getOrThrow(StructureKeys.FORTRESS))
				)
			)
			.build(exporter, "nether/find_fortress");
		Advancement.Builder.create()
			.parent(advancementEntry)
			.display(
				Items.MAP,
				Text.translatable("advancements.nether.fast_travel.title"),
				Text.translatable("advancements.nether.fast_travel.description"),
				null,
				AdvancementFrame.CHALLENGE,
				true,
				true,
				false
			)
			.rewards(AdvancementRewards.Builder.experience(100))
			.criterion("travelled", TravelCriterion.Conditions.netherTravel(DistancePredicate.horizontal(NumberRange.DoubleRange.atLeast(7000.0))))
			.build(exporter, "nether/fast_travel");
		Advancement.Builder.create()
			.parent(advancementEntry2)
			.display(
				Items.GHAST_TEAR,
				Text.translatable("advancements.nether.uneasy_alliance.title"),
				Text.translatable("advancements.nether.uneasy_alliance.description"),
				null,
				AdvancementFrame.CHALLENGE,
				true,
				true,
				false
			)
			.rewards(AdvancementRewards.Builder.experience(100))
			.criterion(
				"killed_ghast",
				OnKilledCriterion.Conditions.createPlayerKilledEntity(
					EntityPredicate.Builder.create().type(EntityType.GHAST).location(LocationPredicate.Builder.createDimension(World.OVERWORLD))
				)
			)
			.build(exporter, "nether/uneasy_alliance");
		AdvancementEntry advancementEntry4 = Advancement.Builder.create()
			.parent(advancementEntry3)
			.display(
				Blocks.WITHER_SKELETON_SKULL,
				Text.translatable("advancements.nether.get_wither_skull.title"),
				Text.translatable("advancements.nether.get_wither_skull.description"),
				null,
				AdvancementFrame.TASK,
				true,
				true,
				false
			)
			.criterion("wither_skull", InventoryChangedCriterion.Conditions.items(Blocks.WITHER_SKELETON_SKULL))
			.build(exporter, "nether/get_wither_skull");
		AdvancementEntry advancementEntry5 = Advancement.Builder.create()
			.parent(advancementEntry4)
			.display(
				Items.NETHER_STAR,
				Text.translatable("advancements.nether.summon_wither.title"),
				Text.translatable("advancements.nether.summon_wither.description"),
				null,
				AdvancementFrame.TASK,
				true,
				true,
				false
			)
			.criterion("summoned", SummonedEntityCriterion.Conditions.create(EntityPredicate.Builder.create().type(EntityType.WITHER)))
			.build(exporter, "nether/summon_wither");
		AdvancementEntry advancementEntry6 = Advancement.Builder.create()
			.parent(advancementEntry3)
			.display(
				Items.BLAZE_ROD,
				Text.translatable("advancements.nether.obtain_blaze_rod.title"),
				Text.translatable("advancements.nether.obtain_blaze_rod.description"),
				null,
				AdvancementFrame.TASK,
				true,
				true,
				false
			)
			.criterion("blaze_rod", InventoryChangedCriterion.Conditions.items(Items.BLAZE_ROD))
			.build(exporter, "nether/obtain_blaze_rod");
		AdvancementEntry advancementEntry7 = Advancement.Builder.create()
			.parent(advancementEntry5)
			.display(
				Blocks.BEACON,
				Text.translatable("advancements.nether.create_beacon.title"),
				Text.translatable("advancements.nether.create_beacon.description"),
				null,
				AdvancementFrame.TASK,
				true,
				true,
				false
			)
			.criterion("beacon", ConstructBeaconCriterion.Conditions.level(NumberRange.IntRange.atLeast(1)))
			.build(exporter, "nether/create_beacon");
		Advancement.Builder.create()
			.parent(advancementEntry7)
			.display(
				Blocks.BEACON,
				Text.translatable("advancements.nether.create_full_beacon.title"),
				Text.translatable("advancements.nether.create_full_beacon.description"),
				null,
				AdvancementFrame.GOAL,
				true,
				true,
				false
			)
			.criterion("beacon", ConstructBeaconCriterion.Conditions.level(NumberRange.IntRange.exactly(4)))
			.build(exporter, "nether/create_full_beacon");
		AdvancementEntry advancementEntry8 = Advancement.Builder.create()
			.parent(advancementEntry6)
			.display(
				Items.POTION,
				Text.translatable("advancements.nether.brew_potion.title"),
				Text.translatable("advancements.nether.brew_potion.description"),
				null,
				AdvancementFrame.TASK,
				true,
				true,
				false
			)
			.criterion("potion", BrewedPotionCriterion.Conditions.any())
			.build(exporter, "nether/brew_potion");
		AdvancementEntry advancementEntry9 = Advancement.Builder.create()
			.parent(advancementEntry8)
			.display(
				Items.MILK_BUCKET,
				Text.translatable("advancements.nether.all_potions.title"),
				Text.translatable("advancements.nether.all_potions.description"),
				null,
				AdvancementFrame.CHALLENGE,
				true,
				true,
				false
			)
			.rewards(AdvancementRewards.Builder.experience(100))
			.criterion(
				"all_effects",
				EffectsChangedCriterion.Conditions.create(
					EntityEffectPredicate.Builder.create()
						.addEffect(StatusEffects.SPEED)
						.addEffect(StatusEffects.SLOWNESS)
						.addEffect(StatusEffects.STRENGTH)
						.addEffect(StatusEffects.JUMP_BOOST)
						.addEffect(StatusEffects.REGENERATION)
						.addEffect(StatusEffects.FIRE_RESISTANCE)
						.addEffect(StatusEffects.WATER_BREATHING)
						.addEffect(StatusEffects.INVISIBILITY)
						.addEffect(StatusEffects.NIGHT_VISION)
						.addEffect(StatusEffects.WEAKNESS)
						.addEffect(StatusEffects.POISON)
						.addEffect(StatusEffects.SLOW_FALLING)
						.addEffect(StatusEffects.RESISTANCE)
						.addEffect(StatusEffects.OOZING)
						.addEffect(StatusEffects.INFESTED)
						.addEffect(StatusEffects.WIND_CHARGED)
						.addEffect(StatusEffects.WEAVING)
				)
			)
			.build(exporter, "nether/all_potions");
		Advancement.Builder.create()
			.parent(advancementEntry9)
			.display(
				Items.BUCKET,
				Text.translatable("advancements.nether.all_effects.title"),
				Text.translatable("advancements.nether.all_effects.description"),
				null,
				AdvancementFrame.CHALLENGE,
				true,
				true,
				true
			)
			.rewards(AdvancementRewards.Builder.experience(1000))
			.criterion(
				"all_effects",
				EffectsChangedCriterion.Conditions.create(
					EntityEffectPredicate.Builder.create()
						.addEffect(StatusEffects.SPEED)
						.addEffect(StatusEffects.SLOWNESS)
						.addEffect(StatusEffects.STRENGTH)
						.addEffect(StatusEffects.JUMP_BOOST)
						.addEffect(StatusEffects.REGENERATION)
						.addEffect(StatusEffects.FIRE_RESISTANCE)
						.addEffect(StatusEffects.WATER_BREATHING)
						.addEffect(StatusEffects.INVISIBILITY)
						.addEffect(StatusEffects.NIGHT_VISION)
						.addEffect(StatusEffects.WEAKNESS)
						.addEffect(StatusEffects.POISON)
						.addEffect(StatusEffects.WITHER)
						.addEffect(StatusEffects.HASTE)
						.addEffect(StatusEffects.MINING_FATIGUE)
						.addEffect(StatusEffects.LEVITATION)
						.addEffect(StatusEffects.GLOWING)
						.addEffect(StatusEffects.ABSORPTION)
						.addEffect(StatusEffects.HUNGER)
						.addEffect(StatusEffects.NAUSEA)
						.addEffect(StatusEffects.RESISTANCE)
						.addEffect(StatusEffects.SLOW_FALLING)
						.addEffect(StatusEffects.CONDUIT_POWER)
						.addEffect(StatusEffects.DOLPHINS_GRACE)
						.addEffect(StatusEffects.BLINDNESS)
						.addEffect(StatusEffects.BAD_OMEN)
						.addEffect(StatusEffects.HERO_OF_THE_VILLAGE)
						.addEffect(StatusEffects.DARKNESS)
						.addEffect(StatusEffects.OOZING)
						.addEffect(StatusEffects.INFESTED)
						.addEffect(StatusEffects.WIND_CHARGED)
						.addEffect(StatusEffects.WEAVING)
						.addEffect(StatusEffects.TRIAL_OMEN)
						.addEffect(StatusEffects.RAID_OMEN)
				)
			)
			.build(exporter, "nether/all_effects");
		AdvancementEntry advancementEntry10 = Advancement.Builder.create()
			.parent(advancementEntry)
			.display(
				Items.ANCIENT_DEBRIS,
				Text.translatable("advancements.nether.obtain_ancient_debris.title"),
				Text.translatable("advancements.nether.obtain_ancient_debris.description"),
				null,
				AdvancementFrame.TASK,
				true,
				true,
				false
			)
			.criterion("ancient_debris", InventoryChangedCriterion.Conditions.items(Items.ANCIENT_DEBRIS))
			.build(exporter, "nether/obtain_ancient_debris");
		Advancement.Builder.create()
			.parent(advancementEntry10)
			.display(
				Items.NETHERITE_CHESTPLATE,
				Text.translatable("advancements.nether.netherite_armor.title"),
				Text.translatable("advancements.nether.netherite_armor.description"),
				null,
				AdvancementFrame.CHALLENGE,
				true,
				true,
				false
			)
			.rewards(AdvancementRewards.Builder.experience(100))
			.criterion(
				"netherite_armor",
				InventoryChangedCriterion.Conditions.items(Items.NETHERITE_HELMET, Items.NETHERITE_CHESTPLATE, Items.NETHERITE_LEGGINGS, Items.NETHERITE_BOOTS)
			)
			.build(exporter, "nether/netherite_armor");
		Advancement.Builder.create()
			.parent(advancementEntry10)
			.display(
				Items.LODESTONE,
				Text.translatable("advancements.nether.use_lodestone.title"),
				Text.translatable("advancements.nether.use_lodestone.description"),
				null,
				AdvancementFrame.TASK,
				true,
				true,
				false
			)
			.criterion(
				"use_lodestone",
				ItemCriterion.Conditions.createItemUsedOnBlock(
					LocationPredicate.Builder.create().block(BlockPredicate.Builder.create().blocks(Blocks.LODESTONE)), ItemPredicate.Builder.create().items(Items.COMPASS)
				)
			)
			.build(exporter, "nether/use_lodestone");
		AdvancementEntry advancementEntry11 = Advancement.Builder.create()
			.parent(advancementEntry)
			.display(
				Items.CRYING_OBSIDIAN,
				Text.translatable("advancements.nether.obtain_crying_obsidian.title"),
				Text.translatable("advancements.nether.obtain_crying_obsidian.description"),
				null,
				AdvancementFrame.TASK,
				true,
				true,
				false
			)
			.criterion("crying_obsidian", InventoryChangedCriterion.Conditions.items(Items.CRYING_OBSIDIAN))
			.build(exporter, "nether/obtain_crying_obsidian");
		Advancement.Builder.create()
			.parent(advancementEntry11)
			.display(
				Items.RESPAWN_ANCHOR,
				Text.translatable("advancements.nether.charge_respawn_anchor.title"),
				Text.translatable("advancements.nether.charge_respawn_anchor.description"),
				null,
				AdvancementFrame.TASK,
				true,
				true,
				false
			)
			.criterion(
				"charge_respawn_anchor",
				ItemCriterion.Conditions.createItemUsedOnBlock(
					LocationPredicate.Builder.create()
						.block(BlockPredicate.Builder.create().blocks(Blocks.RESPAWN_ANCHOR).state(StatePredicate.Builder.create().exactMatch(RespawnAnchorBlock.CHARGES, 4))),
					ItemPredicate.Builder.create().items(Blocks.GLOWSTONE)
				)
			)
			.build(exporter, "nether/charge_respawn_anchor");
		AdvancementEntry advancementEntry12 = Advancement.Builder.create()
			.parent(advancementEntry)
			.display(
				Items.WARPED_FUNGUS_ON_A_STICK,
				Text.translatable("advancements.nether.ride_strider.title"),
				Text.translatable("advancements.nether.ride_strider.description"),
				null,
				AdvancementFrame.TASK,
				true,
				true,
				false
			)
			.criterion(
				"used_warped_fungus_on_a_stick",
				ItemDurabilityChangedCriterion.Conditions.create(
					Optional.of(
						EntityPredicate.contextPredicateFromEntityPredicate(EntityPredicate.Builder.create().vehicle(EntityPredicate.Builder.create().type(EntityType.STRIDER)))
					),
					Optional.of(ItemPredicate.Builder.create().items(Items.WARPED_FUNGUS_ON_A_STICK).build()),
					NumberRange.IntRange.ANY
				)
			)
			.build(exporter, "nether/ride_strider");
		Advancement.Builder.create()
			.parent(advancementEntry12)
			.display(
				Items.WARPED_FUNGUS_ON_A_STICK,
				Text.translatable("advancements.nether.ride_strider_in_overworld_lava.title"),
				Text.translatable("advancements.nether.ride_strider_in_overworld_lava.description"),
				null,
				AdvancementFrame.TASK,
				true,
				true,
				false
			)
			.criterion(
				"ride_entity_distance",
				TravelCriterion.Conditions.rideEntityInLava(
					EntityPredicate.Builder.create()
						.location(LocationPredicate.Builder.createDimension(World.OVERWORLD))
						.vehicle(EntityPredicate.Builder.create().type(EntityType.STRIDER)),
					DistancePredicate.horizontal(NumberRange.DoubleRange.atLeast(50.0))
				)
			)
			.build(exporter, "nether/ride_strider_in_overworld_lava");
		VanillaAdventureTabAdvancementGenerator.requireListedBiomesVisited(
				Advancement.Builder.create(), lookup, MultiNoiseBiomeSourceParameterList.Preset.NETHER.biomeStream().toList()
			)
			.parent(advancementEntry12)
			.display(
				Items.NETHERITE_BOOTS,
				Text.translatable("advancements.nether.explore_nether.title"),
				Text.translatable("advancements.nether.explore_nether.description"),
				null,
				AdvancementFrame.CHALLENGE,
				true,
				true,
				false
			)
			.rewards(AdvancementRewards.Builder.experience(500))
			.build(exporter, "nether/explore_nether");
		AdvancementEntry advancementEntry13 = Advancement.Builder.create()
			.parent(advancementEntry)
			.display(
				Items.POLISHED_BLACKSTONE_BRICKS,
				Text.translatable("advancements.nether.find_bastion.title"),
				Text.translatable("advancements.nether.find_bastion.description"),
				null,
				AdvancementFrame.TASK,
				true,
				true,
				false
			)
			.criterion(
				"bastion",
				TickCriterion.Conditions.createLocation(
					LocationPredicate.Builder.createStructure(lookup.getWrapperOrThrow(RegistryKeys.STRUCTURE).getOrThrow(StructureKeys.BASTION_REMNANT))
				)
			)
			.build(exporter, "nether/find_bastion");
		Advancement.Builder.create()
			.parent(advancementEntry13)
			.display(
				Blocks.CHEST,
				Text.translatable("advancements.nether.loot_bastion.title"),
				Text.translatable("advancements.nether.loot_bastion.description"),
				null,
				AdvancementFrame.TASK,
				true,
				true,
				false
			)
			.criteriaMerger(AdvancementRequirements.CriterionMerger.OR)
			.criterion("loot_bastion_other", PlayerGeneratesContainerLootCriterion.Conditions.create(LootTables.BASTION_OTHER_CHEST))
			.criterion("loot_bastion_treasure", PlayerGeneratesContainerLootCriterion.Conditions.create(LootTables.BASTION_TREASURE_CHEST))
			.criterion("loot_bastion_hoglin_stable", PlayerGeneratesContainerLootCriterion.Conditions.create(LootTables.BASTION_HOGLIN_STABLE_CHEST))
			.criterion("loot_bastion_bridge", PlayerGeneratesContainerLootCriterion.Conditions.create(LootTables.BASTION_BRIDGE_CHEST))
			.build(exporter, "nether/loot_bastion");
		Advancement.Builder.create()
			.parent(advancementEntry)
			.criteriaMerger(AdvancementRequirements.CriterionMerger.OR)
			.display(
				Items.GOLD_INGOT,
				Text.translatable("advancements.nether.distract_piglin.title"),
				Text.translatable("advancements.nether.distract_piglin.description"),
				null,
				AdvancementFrame.TASK,
				true,
				true,
				false
			)
			.criterion(
				"distract_piglin",
				ThrownItemPickedUpByEntityCriterion.Conditions.createThrownItemPickedUpByEntity(
					PIGLIN_DISTRACTION_PREDICATE,
					Optional.of(ItemPredicate.Builder.create().tag(ItemTags.PIGLIN_LOVED).build()),
					Optional.of(
						EntityPredicate.contextPredicateFromEntityPredicate(
							EntityPredicate.Builder.create().type(EntityType.PIGLIN).flags(EntityFlagsPredicate.Builder.create().isBaby(false))
						)
					)
				)
			)
			.criterion(
				"distract_piglin_directly",
				PlayerInteractedWithEntityCriterion.Conditions.create(
					Optional.of(PIGLIN_DISTRACTION_PREDICATE),
					ItemPredicate.Builder.create().items(PiglinBrain.BARTERING_ITEM),
					Optional.of(
						EntityPredicate.contextPredicateFromEntityPredicate(
							EntityPredicate.Builder.create().type(EntityType.PIGLIN).flags(EntityFlagsPredicate.Builder.create().isBaby(false))
						)
					)
				)
			)
			.build(exporter, "nether/distract_piglin");
	}
}
